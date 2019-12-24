package dhu.cst.zhamao.zm_tiku.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dhu.cst.zhamao.zm_tiku.R;
import dhu.cst.zhamao.zm_tiku.object.TikuVersion;
import dhu.cst.zhamao.zm_tiku.view.SelectBank;


public class ZMUtil {

    public static final int API_VER = 1;

    public static String loadResource(Context context, String file_name) {
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
        String res = "";
        try {
            FileInputStream fin = context.openFileInput(file_name);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fin.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            output.close();
            fin.close();
            res = output.toString();
        } catch (Exception e) {
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
            file.delete();
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

    public static void submitFeedback(final Activity activity,
                                      final String contact,
                                      final String title,
                                      final String content,
                                      final Map<String, String> extra,
                                      final Runnable runnable,
                                      final Runnable failRunnable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("https://api.zhamao.xin/tiku-app?tiku_api=feedback");
                    connection = (HttpURLConnection) url.openConnection();
                    //设置请求方法
                    connection.setRequestMethod("POST");
                    //设置连接超时时间（毫秒）
                    connection.setConnectTimeout(5000);
                    //设置读取超时时间（毫秒）
                    connection.setReadTimeout(5000);

                    connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                    String data = "SystemVersion=" + URLEncoder.encode(android.os.Build.VERSION.RELEASE, "utf-8") + //获取当前手机系统版本号
                            "&SystemModel=" + URLEncoder.encode(android.os.Build.MODEL, "utf-8") + //获取手机型号
                            "&DeviceBrand=" + URLEncoder.encode(android.os.Build.MODEL, "utf-8") + //获取手机厂商
                            "&SystemLanguage=" + URLEncoder.encode(Locale.getDefault().getLanguage(), "utf-8"); //获取语言
                    data += "&contact=" + URLEncoder.encode(contact, "utf-8");
                    data += "&title=" + URLEncoder.encode(title, "utf-8");
                    data += "&content=" + URLEncoder.encode(content, "utf-8");
                    if(extra != null) {
                        for (Map.Entry<String, String> entry : extra.entrySet()) {
                            data += "&" + URLEncoder.encode(entry.getKey(), "utf-8") + "=" + URLEncoder.encode(entry.getValue(), "utf-8");
                        }
                    }

                    //3设置给服务器写的数据的长度
                    connection.setRequestProperty("Content-Length", String.valueOf(data.length()));

                    //4指定要给服务器写数据
                    connection.setDoOutput(true);

                    //5开始向服务器写数据
                    connection.getOutputStream().write(data.getBytes());

                    int code = connection.getResponseCode();
                    if (code != 200) throw new IOException("返回值不是200！");

                    //返回输入流
                    InputStream in = connection.getInputStream();

                    //读取输入流
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    activity.runOnUiThread(runnable);
                } catch (IOException e) {
                    activity.runOnUiThread(failRunnable);
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
        }).start();
    }

    public static void checkUpdate(final Activity activity, View v, final Runnable runnable, final Runnable failRunnable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("https://api.zhamao.xin/tiku-app?tiku_api=check_update&api_ver=" + API_VER);
                    connection = (HttpURLConnection) url.openConnection();
                    //设置请求方法
                    connection.setRequestMethod("POST");
                    //设置连接超时时间（毫秒）
                    connection.setConnectTimeout(5000);
                    //设置读取超时时间（毫秒）
                    connection.setReadTimeout(5000);

                    connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                    String data = "SystemVersion=" + URLEncoder.encode(android.os.Build.VERSION.RELEASE, "utf-8") + //获取当前手机系统版本号
                            "&SystemModel=" + URLEncoder.encode(android.os.Build.MODEL, "utf-8") + //获取手机型号
                            "&DeviceBrand=" + URLEncoder.encode(android.os.Build.MODEL, "utf-8") + //获取手机厂商
                            "&SystemLanguage=" + URLEncoder.encode(Locale.getDefault().getLanguage(), "utf-8"); //获取语言

                    //3设置给服务器写的数据的长度
                    connection.setRequestProperty("Content-Length", String.valueOf(data.length()));

                    //4指定要给服务器写数据
                    connection.setDoOutput(true);

                    //5开始向服务器写数据
                    connection.getOutputStream().write(data.getBytes());

                    int code = connection.getResponseCode();
                    if (code != 200) throw new IOException("返回值不是200！");

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
                } catch (IOException e) {
                    activity.runOnUiThread(failRunnable);
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

    public class TikuApiVersion {
        String latest_version;
        String download_link;
        String tiku_version;
        Map<String, String> tiku_download_link;
    }

    private static void compareUpdate(final Activity activity, String internet_ver) {
        try {
            Gson gson = new Gson();
            final TikuApiVersion v = gson.fromJson(internet_ver, TikuApiVersion.class);

            PackageInfo packageInfo = activity.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(activity.getPackageName(), 0);
            if (!v.latest_version.equals(packageInfo.versionName)) {
                Snackbar.make(activity.findViewById(R.id.ConstraintLayout), "App有新版本：" + v.latest_version, Snackbar.LENGTH_LONG).show();
                Handler startMainActivity = new Handler();
                startMainActivity.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                makeDialog(
                                        activity,
                                        "确定更新吗？",
                                        "App最新版本是 " + v.latest_version + "，点击确定下载最新版App",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Uri uri = Uri.parse(v.download_link);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                activity.startActivity(intent);
                                                activity.finish();
                                            }
                                        },
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }
                                );
                            }
                        });
                    }
                }, 300);
            } else /*if (v.tiku_version.equals(getTikuVersion(activity).version_name))*/ { //题库是最新版
                Snackbar.make(activity.findViewById(R.id.ConstraintLayout), "已经是最新版：" + v.latest_version, Snackbar.LENGTH_LONG).show();
            } /*else { //题库不是最新版，弹出更新题库的对话框
                Snackbar.make(activity.findViewById(R.id.ConstraintLayout), "有新版本：" + v.tiku_version, Snackbar.LENGTH_LONG).show();
                Handler startMainActivity = new Handler();
                startMainActivity.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                makeDialog(
                                        activity,
                                        "确定更新吗？",
                                        "最新版本是" + v.tiku_version + "，如果更新的话将重置你的做题进度和错题记录！",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Animation circle_anim = AnimationUtils.loadAnimation(activity, R.anim.rotate);
                                                        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
                                                        circle_anim.setInterpolator(interpolator);
                                                        activity.findViewById(R.id.upateButton).startAnimation(circle_anim);  //开始动画
                                                    }
                                                });
                                                updateTiku(activity, activity.getFilesDir().getAbsolutePath(), v);
                                            }
                                        },
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }
                                );
                            }
                        });

                    }
                }, 500);
            }*/
        } catch (JsonSyntaxException e) {
            Snackbar.make(activity.findViewById(R.id.ConstraintLayout), "更新服务器出错啦！请联系开发者！", Snackbar.LENGTH_LONG).show();
        } catch (PackageManager.NameNotFoundException e) {
            Snackbar.make(activity.findViewById(R.id.ConstraintLayout), "App出错啦！请联系开发者！", Snackbar.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public static void makeDialog(Activity activity,
                                  String title,
                                  String message,
                                  DialogInterface.OnClickListener positiveListener,
                                  DialogInterface.OnClickListener negativeListener) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton("确定", positiveListener)
                .setNegativeButton("取消", negativeListener).create().show();
    }

    public static void updateTiku(final Activity activity, final String path, final TikuApiVersion version) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream output = null;
                try {
                    TikuVersion ver = new TikuVersion();
                    ver.version_name = version.tiku_version;
                    ver.tiku_hash = "";
                    ver.tiku_list = new ArrayList<>();
                    Gson gson = new Gson();
                    String json = gson.toJson(ver);
                    File file = new File(path + File.separator + "version.json");
                    if (file.exists()) {
                        file.delete();
                        file = new File(path + File.separator + "version.json");
                    }
                    FileOutputStream outStream = new FileOutputStream(file);
                    outStream.write(json.getBytes());
                    outStream.close();

                    for (final Map.Entry<String, String> entry : version.tiku_download_link.entrySet()) {
                        URL url = new URL(entry.getValue());
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(7 * 1000); //6秒
                        InputStream input = conn.getInputStream();
                        file = new File(path + File.separator + entry.getKey() + ".json");
                        String p = path + File.separator + entry.getKey() + ".json";
                        if (file.exists()) {
                            file.delete();
                            file = new File(path + File.separator + entry.getKey() + ".json");
                        }
                        output = new FileOutputStream(file);
                        byte[] buffer = new byte[4 * 1024];
                        while (input.read(buffer) != -1) {
                            output.write(buffer);
                            output.flush();
                        }
                        output.close();
                        Log.i("ZMUtil", "成功写入" + entry.getKey() + "题库的文件");
                        System.out.println("Getting " + entry.getKey());
                        System.out.print(ZMUtil.loadInternalFile(activity, entry.getKey() + ".json"));
                    }
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                    showError();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    showError();
                } finally {
                    try {
                        output.close();
                        showSuccess();
                    } catch (IOException e) {
                        System.out.println("fail");
                        e.printStackTrace();
                        showError();
                    }
                }
            }

            private void showSuccess() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.findViewById(R.id.upateButton).clearAnimation();
                        QB qb = new QB(activity);
                        qb.getDB().queryQB("DELETE FROM qb", new String[]{});
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
                        builder.setTitle("成功更新题库");
                        builder.setMessage("点击确定关闭应用，之后重新打开App即完成更新题库！");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.finishAndRemoveTask();
                            }
                        });
                        builder.create().show();

                    }
                });
            }

            private void showError() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.findViewById(R.id.upateButton).clearAnimation();
                        makeDialog(activity, "哎呀，出错啦！", "下载题库失败，请重试！如果遇到应用崩溃，进入设置清除数据即可", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                copyAssetsFile2Phone(activity, "politics.json");
                                copyAssetsFile2Phone(activity, "maogai.json");
                                copyAssetsFile2Phone(activity, "history.json");
                                copyAssetsFile2Phone(activity, "makesi.json");
                                copyAssetsFile2Phone(activity, "version.json");
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                    }
                });
            }
        }).start();
    }
}
