package dhu.cst.zhamao.zm_tiku.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

class ZMUtil {

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

    static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    static String implode(String del, List<String> list) {
        StringBuilder p = new StringBuilder();
        for (int i = 0; i < list.size(); ++i) {
            if (i != list.size() - 1) p.append(list.get(i)).append(del);
            else p.append(list.get(i));
        }
        return p.toString();
    }

    static String jsonEncode(List list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
