package xin.zhamao.zm_tiku.utils;

import android.content.Context;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import xin.zhamao.zm_tiku.R;
import xin.zhamao.zm_tiku.object.TikuMeta;
import xin.zhamao.zm_tiku.object.TikuMetaList;

public class TikuManager {
    /**
     * 保存全局的 meta 信息的变量
     */
    private static TikuMetaList metas;

    public static final Map<Integer, TikuMeta> metaMap = new LinkedHashMap<>();

    /**
     * 上下文
     */
    private final Context context;

    /**
     * 本次初始化发现需要更新的列表
     */
    private final Map<TikuMeta, TikuMeta> updateList;

    private final ArrayList<TikuMeta> newList;

    public TikuManager(Context context) {
        this.context = context;
        this.updateList = new LinkedHashMap<>();
        this.newList = new ArrayList<>();
    }

    public static TikuMeta findMetaByName(String qb_name) {
        for (TikuMeta meta : metaMap.values()) {
            if (meta.name.equals(qb_name)) {
                return meta;
            }
        }
        return null;
    }

    /**
     * 初始化题库数据
     */
    public int initTiku() {
        //如果本地题库损坏或者本地还没拉题库，则从Asset拉取题库文件
        String file_path = context.getFilesDir().getAbsolutePath() + "/";
        File fil = new File(file_path + "tiku_list.json");

        // 如果文件不存在，则表明App数据还没初始化，先将元数据写到存储里
        if (!fil.exists()) {
            FileSystem.copyAssetsFile2Phone(context, "tiku_list.json");
        }

        // 读取 存储的 Json
        String metaJson = FileSystem.loadInternalFile(context, "tiku_list.json");
        metas = (new Gson()).fromJson(metaJson, TikuMetaList.class);

        // 读取 Assets 中的 Json
        FileSystem.copyAssetsFile2Phone(context, "tiku_list.json", "tiku_list_new.json");
        TikuMetaList newMeta = (new Gson()).fromJson(FileSystem.loadInternalFile(context, "tiku_list_new.json"), TikuMetaList.class);
        int importCnt = 0;
        // 遍历元数据
        boolean isExists;
        for (TikuMeta meta : newMeta) {
            isExists = false;
            for (TikuMeta m : metas) {
                if (!m.name.equals(meta.name)) {
                    continue;
                }

                // 找到需要更新的题库
                if (!m.version.equals(meta.version) || m.edit_version != meta.edit_version) {
                    updateList.put(m, meta);
                } else {
                    if (!FileSystem.isInternalFileExists(context, meta.tiku_file)) {
                        ++importCnt;
                        FileSystem.copyAssetsFile2Phone(context, meta.tiku_file);
                    }
                }
                isExists = true;
            }
            // 标识显示新的list额外有新的题库，要导入新的题库
            if (!isExists) {
                newList.add(meta);
                ++importCnt;
                FileSystem.copyAssetsFile2Phone(context, meta.tiku_file);
            }
        }
return importCnt;
    }

    /**
     * 返回是否需要更新
     *
     * @return 返回是否需要更新
     */
    public boolean isNeedUpdate() {
        return !updateList.isEmpty() || !newList.isEmpty();
    }

    public Map<TikuMeta, TikuMeta> getUpdateList() {
        return updateList;
    }

    public ArrayList<TikuMeta> getNewList() {
        return newList;
    }

    public static TikuMetaList getMetaList() {
        return metas;
    }

    public void updateTiku(TikuMeta newMeta, boolean finalFlushDB) {
        FileSystem.copyAssetsFile2Phone(context, newMeta.tiku_file);
        boolean updated = false;
        for (int i = 0; i < metas.size(); ++i) {
            if (metas.get(i).name.equals(newMeta.name)) {
                metas.set(i, newMeta);
                updated = true;
                break;
            }
        }
        if (!updated) {
            metas.add(newMeta);
            Toast.makeText(context, "导入新题库需要重启 App 生效", Toast.LENGTH_SHORT).show();
        }

        Gson gson = new Gson();
        String json = gson.toJson(metas);
        FileSystem.saveInternalFile(context, "tiku_list.json", json);
        if (finalFlushDB) {
            QB qb = new QB(context);
            qb.getDB().queryQB("DELETE FROM qb WHERE qb_name = ?", new String[]{newMeta.name});
            context.getSharedPreferences("qb_cache_" + newMeta.name, Context.MODE_PRIVATE).edit().clear().apply();
        }
    }
}
