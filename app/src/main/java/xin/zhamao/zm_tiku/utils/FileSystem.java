package xin.zhamao.zm_tiku.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileSystem {
    public static void copyAssetsFile2Phone(Context context, String fileName) {
        copyAssetsFile2Phone(context, fileName, fileName);
    }
    /**
     * 将文件从assets目录，考贝到 /data/data/包名/files/ 目录中。assets 目录中的文件，会不经压缩打包至APK包中，使用时还应从apk包中导出来
     *
     * @param fileName 文件名,如aaa.txt
     */
    public static void copyAssetsFile2Phone(Context context, String fileName, String dstFileName) {
        try {
            InputStream inputStream = context.getAssets().open("tiku/" + fileName);
            //getFilesDir() 获得当前APP的安装路径 /data/data/包名/files 目录
            File file = new File(context.getFilesDir().getAbsolutePath() + File.separator + dstFileName);
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

    /**
     * 读取存储在内部存储的文件
     *
     * @param context 上下文
     * @param file_name 文件名
     * @return 文件内容
     */
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

    /**
     * 保存文件到内部存储
     *
     * @param context 上下文
     * @param file_name 文件名
     * @param content 文件内容
     */
    public static void saveInternalFile(Context context, String file_name, String content) {
        try {
            // Toast.makeText(context, "已写入 " + content, Toast.LENGTH_SHORT).show();
            FileOutputStream fout = context.openFileOutput(file_name, Activity.MODE_PRIVATE);
            byte[] bytes = content.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 内部存储是否存在文件
     *
     * @param context 上下文
     * @param fileName 内部文件的名称
     * @return 是否存在
     */
    public static boolean isInternalFileExists(Context context, String fileName) {
        return (new File(context.getFilesDir().getAbsolutePath() + "/" + fileName)).exists();
    }
}
