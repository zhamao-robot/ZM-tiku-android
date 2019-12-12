package dhu.cst.zhamao.zm_tiku.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.text.Editable;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


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
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
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
}
