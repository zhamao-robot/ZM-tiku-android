package xin.zhamao.zm_tiku.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.Map;
import java.util.Objects;

import xin.zhamao.zm_tiku.R;
import xin.zhamao.zm_tiku.object.TikuMeta;
import xin.zhamao.zm_tiku.utils.QB;
import xin.zhamao.zm_tiku.utils.TikuManager;
import xin.zhamao.zm_tiku.view.SelectBank;

public class DialogUI {
    private final Activity activity;

    public DialogUI(Activity activity) {
        this.activity = activity;
    }

    public void showTikuUpdateDialog(String title, View view) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(activity);
        dialog.setTitle(title);
        dialog.setView(view);
        dialog.setNegativeButton("完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("不再提醒", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // 设置不再提醒的一个变量值为 true，防止下次启动时候检测到更新
                activity.getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putBoolean("no_update_dialog", true).apply();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    public View makeUpdateListLayout(final TikuManager tikuManager) {
        LinearLayout baseLayout = new LinearLayout(activity);
        // baseLayout为垂直排列
        baseLayout.setOrientation(LinearLayout.VERTICAL);

        // 生成一个具有升级按钮的放在对话框中的列表
        for (Map.Entry<TikuMeta, TikuMeta> entry : tikuManager.getUpdateList().entrySet()) {
            final TikuMeta newMeta = entry.getValue();
            TikuMeta oldMeta = entry.getKey();

            String buttonText = "升级";
            boolean flushDB = false;
            if (oldMeta.version.equals(newMeta.version)) {
                buttonText += "(不影响进度)";
            } else {
                buttonText += "(会重置进度)";
                flushDB = true;
            }
            LinearLayout generated = generateUpdateItem(newMeta, buttonText, flushDB, tikuManager);
            baseLayout.addView(generated);
        }
        for (TikuMeta newMeta : tikuManager.getNewList()) {
            String buttonText = "导入新题库";
            LinearLayout generated = generateUpdateItem(newMeta, buttonText, false, tikuManager);
            baseLayout.addView(generated);
        }

        return baseLayout;
    }

    private LinearLayout generateUpdateItem(final TikuMeta newMeta, String buttonText, boolean flushDB, final TikuManager tikuManager) {
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(10, 0, 10, 0);

        // 生成题库名称
        MaterialTextView textView = new MaterialTextView(activity);
        textView.setText("     " + newMeta.display_name);
        textView.setTextSize(16);
        // textView.setPadding(10, 10, 10, 10);
        layout.addView(textView);

        // 生成升级按钮
        MaterialButton button = new MaterialButton(activity);
        button.setText(buttonText);
        // 按钮只需要用文字撑开宽度就行了
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        final boolean finalFlushDB = flushDB;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 按下按钮就把按钮的文字改了，并且不能再按
                if (v instanceof Button) {
                    tikuManager.updateTiku(newMeta, finalFlushDB);
                    v.setEnabled(false);
                    ((Button) v).setText("升级完成");
                }
            }
        });
        layout.addView(button);
        // 设置当前layout 为大红色，显著分割一下
        // layout.setBackgroundColor(0xffff0000);

        // 让现有的按钮和文本采用两端对齐
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        textView.setLayoutParams(params);
        button.setLayoutParams(params);
        return layout;
    }

    public void showRestoreConfirmDialog(String title) {
        showRestoreConfirmDialog(title, "", true);
    }

    public void showRestoreConfirmDialog(String title, String message, boolean showNegative) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(activity);
        dialog.setTitle(title);
        if (!message.isEmpty()) {
            dialog.setMessage(message);
        }
        if (showNegative) {
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // 遍历删除所有 FilesDir中的文件
                for (String file : Objects.requireNonNull(activity.getFilesDir().list())) {
                    activity.deleteFile(file);
                }
                // 删除 QB 数据库
                QB qb = new QB(activity);
                qb.getDB().queryQB("DELETE FROM qb", new String[]{});
                // 删除所有 preferences
                activity.getSharedPreferences("settings", Context.MODE_PRIVATE).edit().clear().apply();
                if (TikuManager.getMetaList() != null) {
                    for (TikuMeta meta : TikuManager.getMetaList()) {
                        activity.getSharedPreferences("qb_cache_" + meta.name, Context.MODE_PRIVATE).edit().clear().apply();
                    }
                }
                activity.getSharedPreferences("qb_update", Context.MODE_PRIVATE).edit().clear().apply();
                activity.getSharedPreferences("notify", Context.MODE_PRIVATE).edit().clear().apply();
                activity.getSharedPreferences("tts", Context.MODE_PRIVATE).edit().clear().apply();
                Toast.makeText(activity, "成功清除，正在退出程序", Toast.LENGTH_SHORT).show();
                activity.finishAffinity();
            }
        });
        dialog.show();
    }
}
