package dhu.cst.zhamao.zm_tiku.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.util.Log;
import android.view.Display;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import dhu.cst.zhamao.zm_tiku.R;
import dhu.cst.zhamao.zm_tiku.object.TikuVersion;


public class ZMUtil {

    static String loadResource(Context context, String file_name) {
        if (null == context || null == file_name) return null;
        try {
            AssetManager am = context.getAssets();
            InputStream input = am.open(file_name);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            output.close();
            input.close();
            return output.toString();
        } catch (IOException e) {
            Log.e("ZMUtil", "读取文件错误！");
            return null;
        }
    }

    public static String loadInternalFile(Context context, String file_name) {
        String res="";
        try{
            FileInputStream fin = context.openFileInput("version.json");
            int length = fin.available();
            byte [] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer, StandardCharsets.UTF_8);
            fin.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }


    public static TikuVersion getTikuVersion(Context context) {
        String filename = "version.json";
        String file = loadInternalFile(context, filename);
        Log.w("ZMUtil", file);
        Gson gson = new Gson();
        TikuVersion v = new TikuVersion();
        v = gson.fromJson(file, TikuVersion.class);
        return v;
    }

    public static String implode(String del, List<String> list) {
        StringBuilder p = new StringBuilder();
        for (int i = 0; i < list.size(); ++i) {
            if (i != list.size() - 1) p.append(list.get(i)).append(del);
            else p.append(list.get(i));
        }
        return p.toString();
    }

    public static String implode(String del, ArrayList<Integer> list) {
        StringBuilder p = new StringBuilder();
        for (int i = 0; i < list.size(); ++i) {
            if (i != list.size() - 1) p.append(list.get(i)).append(del);
            else p.append(list.get(i));
        }
        return p.toString();
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void showDialog(Context context, String title, String content, DialogInterface.OnClickListener onClickListener) {
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
        normalDialog.setTitle(title);
        normalDialog.setMessage(content);
        normalDialog.setNegativeButton("返回", onClickListener);
        normalDialog.show();
    }

    public static void initUserDB(QB qb) {
        Cursor sqlite = qb.getDB().get().rawQuery("SELECT * FROM user_data WHERE id = ?", new String[]{qb.getUserId()});
        if (sqlite.getCount() == 0) {
            Log.e("QB", "新增本地用户中");
            qb.getDB().queryQB("INSERT INTO user_data VALUES (?,?,?,?)", new String[]{
                    "7cf10d37-ee8d-437b-b2a2-7b6a4c97ab5a",
                    "本地用户",
                    "",
                    "0"
            });
        }
        sqlite.close();
    }

    public static void copyAssetsDir2Phone(Activity activity, String filePath) {
        try {
            String[] fileList = activity.getAssets().list(filePath);
            if (fileList.length > 0) {//如果是目录
                File file = new File(activity.getFilesDir().getAbsolutePath() + File.separator + filePath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileList) {
                    filePath = filePath + File.separator + fileName;

                    copyAssetsDir2Phone(activity, filePath);

                    filePath = filePath.substring(0, filePath.lastIndexOf(File.separator));
                    Log.e("oldPath", filePath);
                }
            } else {//如果是文件
                InputStream inputStream = activity.getAssets().open(filePath);
                File file = new File(activity.getFilesDir().getAbsolutePath() + File.separator + filePath);
                Log.i("copyAssets2Phone", "file:" + file);
                if (!file.exists() || file.length() == 0) {
                    FileOutputStream fos = new FileOutputStream(file);
                    int len = -1;
                    byte[] buffer = new byte[1024];
                    while ((len = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.flush();
                    inputStream.close();
                    fos.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将文件从assets目录，考贝到 /data/data/包名/files/ 目录中。assets 目录中的文件，会不经压缩打包至APK包中，使用时还应从apk包中导出来
     *
     * @param fileName 文件名,如aaa.txt
     */
    public static void copyAssetsFile2Phone(Activity activity, String fileName) {
        try {
            InputStream inputStream = activity.getAssets().open("tiku/" + fileName);
            //getFilesDir() 获得当前APP的安装路径 /data/data/包名/files 目录
            File file = new File(activity.getFilesDir().getAbsolutePath() + File.separator + fileName);
            if (!file.exists() || file.length() == 0) {
                FileOutputStream fos = new FileOutputStream(file);//如果文件不存在，FileOutputStream会自动创建文件
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();//刷新缓存区
                inputStream.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkUpdate(final Activity activity, View v, final Runnable runnable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("https://zhamao.xin/static?tiku_api=check_update");
                    connection = (HttpURLConnection) url.openConnection();
                    //设置请求方法
                    connection.setRequestMethod("GET");
                    //设置连接超时时间（毫秒）
                    connection.setConnectTimeout(5000);
                    //设置读取超时时间（毫秒）
                    connection.setReadTimeout(5000);

                    //返回输入流
                    InputStream in = connection.getInputStream();

                    //读取输入流
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    runOnUI(activity, result.toString());
                    activity.runOnUiThread(runnable);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {//关闭连接
                        connection.disconnect();
                    }
                }
            }

            public void runOnUI(final Activity activity, final String text) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ZMUtil.compareUpdate(activity, text);
                    }
                });
            }
        }).start();
    }

    public static long time() {
        return System.currentTimeMillis();
    }

    public class TikuApiVersion{
        String latest_version;
        String download_link;
    }

    private static void compareUpdate(Activity activity, String internet_ver) {
        Gson gson = new Gson();
        TikuApiVersion v = gson.fromJson(internet_ver, TikuApiVersion.class);
        if(v.latest_version.equals(getTikuVersion(activity).version_name)) {
            Snackbar.make(activity.findViewById(R.id.ConstraintLayout), "已经是最新版：" + v.latest_version, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(activity.findViewById(R.id.ConstraintLayout), "有新版本：" + v.latest_version, Snackbar.LENGTH_LONG).show();
        }
    }
}
