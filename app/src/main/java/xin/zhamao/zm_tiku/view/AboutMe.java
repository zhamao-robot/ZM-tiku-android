package xin.zhamao.zm_tiku.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import xin.zhamao.zhamao.zm_tiku.R;
import xin.zhamao.zm_tiku.object.TikuVersion;
import xin.zhamao.zm_tiku.utils.ZMUtil;

public class AboutMe extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        TextView contact_author = findViewById(R.id.contact_author);
        registerForContextMenu(contact_author);

        TextView app_version = findViewById(R.id.appVersionText);
        try {
            PackageInfo packageInfo = this.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0);
            app_version.setText(String.format("应用版本：%s", packageInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView none = findViewById(R.id.tikuVersionText);
        none.setText("");

        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("关于");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= 26) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }
        });


    }

    public void onOpenService(View v) {
        Uri uri = Uri.parse("https://docs.zhamao.xin/service");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void onOpenHomepage(View v) {
        Uri uri = Uri.parse("https://zhamao.xin/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if(v.getId() == R.id.contact_author) {
            menu.setHeaderTitle("复制选项");
            menu.add(0, v.getId(), 0, "复制群号").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    cm.setPrimaryClip(ClipData.newPlainText("Label", "861087622"));
                    Snackbar.make(findViewById(R.id.ConstraintLayout), "成功复制到剪贴板！", Snackbar.LENGTH_SHORT).show();
                    return true;
                }
            });
            menu.add(0, v.getId(), 0, "复制Crane的QQ号").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    //获取剪贴板管理器：
                    cm.setPrimaryClip(ClipData.newPlainText("Label", "2257685948"));
                    Snackbar.make(findViewById(R.id.ConstraintLayout), "成功复制到剪贴板！", Snackbar.LENGTH_SHORT).show();
                    return true;
                }
            });
            menu.add(0, v.getId(), 0, "复制鲸鱼的QQ号").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    cm.setPrimaryClip(ClipData.newPlainText("Label", "627577391"));
                    Snackbar.make(findViewById(R.id.ConstraintLayout), "成功复制到剪贴板！", Snackbar.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }
}
