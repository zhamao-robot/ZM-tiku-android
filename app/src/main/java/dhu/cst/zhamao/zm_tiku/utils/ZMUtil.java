package dhu.cst.zhamao.zm_tiku.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ZMUtil {

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
        } catch(IOException e) {
            Log.e("ZMUtil", "读取文件错误！");
            return null;
        }
    }
}
