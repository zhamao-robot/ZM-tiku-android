package xin.zhamao.zm_tiku.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import xin.zhamao.zm_tiku.R;
import xin.zhamao.zm_tiku.object.TikuNotification;


public class ZMUtil {

    public static final int API_VER = 1;

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

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

    public static void getNotification(final Activity activity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("https://api.zhamao.xin/tiku-app?tiku_api=notify");
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
                            "&SystemLanguage=" + URLEncoder.encode(Locale.getDefault().getLanguage(), "utf-8") +  //获取语言
                            "&AndroidID=" + URLEncoder.encode(Settings.System.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID), "utf-8");

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
                    Gson gson = new Gson();
                    final TikuNotification v = gson.fromJson(result.toString(), TikuNotification.class);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ZMUtil.showDialog(activity, v.title, v.content, new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                }
                            });
                        }
                    });
                } catch (IOException e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ZMUtil.showDialog(activity, "出错啦！", "网络出现问题，请稍后再试！", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                }
                            });
                        }
                    });
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
        });
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

                    if (extra != null) {
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

    public static void checkUpdate(final Activity activity, final Runnable runnable, final Runnable failRunnable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    final PackageInfo packageInfo;
                    packageInfo = activity.getApplicationContext()
                            .getPackageManager()
                            .getPackageInfo(activity.getPackageName(), 0);
                    URL url = new URL("https://api.zhamao.xin/tiku-app?tiku_api=check_update&api_ver=" + packageInfo.versionName);
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
                            "&SystemLanguage=" + URLEncoder.encode(Locale.getDefault().getLanguage(), "utf-8") +  //获取语言
                            "&AndroidID=" + URLEncoder.encode(Settings.System.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID), "utf-8");

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
                    if (runnable != null) {
                        activity.runOnUiThread(runnable);
                        runOnUI(activity, result.toString());
                    }
                } catch (IOException | PackageManager.NameNotFoundException e) {
                    if (failRunnable != null)
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

            void runOnUI(final Activity activity, final String text) {
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

    public static class TikuApiVersion {
        String latest_version;
        String download_link;
        String commit_msg;
        String notify_title;
        String notify_content;
    }

    private static void compareUpdate(final Activity activity, String internet_ver) {
        try {
            Gson gson = new Gson();
            final TikuApiVersion v = gson.fromJson(internet_ver, TikuApiVersion.class);
            SharedPreferences.Editor editor = activity.getSharedPreferences("notify", Context.MODE_PRIVATE).edit();
            editor.putString("title", v.notify_title);
            editor.putString("content", v.notify_content);
            editor.apply();
            PackageInfo packageInfo = activity.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(activity.getPackageName(), 0);
            if (!v.latest_version.equals(packageInfo.versionName)) {
                Snackbar.make(activity.findViewById(R.id.appBar), "App有新版本：" + v.latest_version, Snackbar.LENGTH_LONG).show();
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
                                        "App最新版本是 " + v.latest_version + "，更新内容如下：" + v.commit_msg + "\n点击确定下载最新版App\n可以到设置中关闭自动检查更新",
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
            }
        } catch (JsonSyntaxException e) {
            Snackbar.make(activity.findViewById(R.id.appBar), "更新服务器出错啦！请联系开发者！", Snackbar.LENGTH_LONG).show();
        } catch (PackageManager.NameNotFoundException e) {
            Snackbar.make(activity.findViewById(R.id.appBar), "App出错啦！请联系开发者！", Snackbar.LENGTH_LONG).show();
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

}