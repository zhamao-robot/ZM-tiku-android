package dhu.cst.zhamao.zm_tiku.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.Console;
import java.io.File;

import dhu.cst.zhamao.zm_tiku.R;
import dhu.cst.zhamao.zm_tiku.object.TikuVersion;
import dhu.cst.zhamao.zm_tiku.utils.QB;
import dhu.cst.zhamao.zm_tiku.utils.ZMUtil;

public class SelectBank extends AppCompatActivity {

    boolean isUpdateActivated = false;
    private long mExitTime;
    private long last_update = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        final FloatingActionButton updateButton = findViewById(R.id.upateButton);
        updateButton.show();
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);

        //如果本地题库损坏或者本地还没拉题库，则从Asset拉取题库文件
        String file_path = getFilesDir().getAbsolutePath() + "/";
        File fil = new File(file_path + "version.json");
        if (!fil.exists()) {
            ZMUtil.copyAssetsFile2Phone(this, "history.json");
            ZMUtil.copyAssetsFile2Phone(this, "politics.json");
            ZMUtil.copyAssetsFile2Phone(this, "maogai.json");
            ZMUtil.copyAssetsFile2Phone(this, "makesi.json");
            ZMUtil.copyAssetsFile2Phone(this, "version.json");
            Snackbar.make(findViewById(R.id.fragment_container), "成功导入题库 !", Snackbar.LENGTH_LONG).show();
        } else {
            String json = ZMUtil.loadInternalFile(this, "version.json");
            Gson gson = new Gson();
            TikuVersion ver = gson.fromJson(json, TikuVersion.class);
            String asset = ZMUtil.loadResource(this, "tiku/version.json");
            final TikuVersion ass_ver = gson.fromJson(asset, TikuVersion.class);
            if (!ver.version_name.equals(ass_ver.version_name)) {
                SharedPreferences pref = getSharedPreferences("qb_update", Context.MODE_PRIVATE);
                final PackageInfo packageInfo;
                try {
                    packageInfo = this.getApplicationContext()
                            .getPackageManager()
                            .getPackageInfo(this.getPackageName(), 0);
                    //Snackbar.make(findViewById(R.id.fragment_container), pref.getString("current_version", "0.1"), Snackbar.LENGTH_LONG).show();
                    if(!pref.getString("current_version", "0.1").equals(packageInfo.versionName)) {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                        builder.setTitle("检测到题库App中题库有更新");
                        builder.setMessage("原来的题库版本是 " + ver.version_name + "，新版题库版本是 " + ass_ver.version_name + "，是否更新内置题库？更新题库将重置所有的做题进度且无法恢复！");
                        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ZMUtil.copyAssetsFile2Phone(SelectBank.this, "history.json");
                                ZMUtil.copyAssetsFile2Phone(SelectBank.this, "politics.json");
                                ZMUtil.copyAssetsFile2Phone(SelectBank.this, "maogai.json");
                                ZMUtil.copyAssetsFile2Phone(SelectBank.this, "makesi.json");
                                ZMUtil.copyAssetsFile2Phone(SelectBank.this, "version.json");
                                Snackbar.make(findViewById(R.id.ConstraintLayout), "成功更新题库到 " + ass_ver.version_name + " !", Snackbar.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = getSharedPreferences("qb_update", Context.MODE_PRIVATE).edit();
                                editor.putString("current_version", packageInfo.versionName);
                                editor.apply();
                                QB qb = new QB(SelectBank.this);
                                qb.getDB().queryQB("DELETE FROM qb", new String[]{});
                                getSharedPreferences("qb_cache_politics", Context.MODE_PRIVATE).edit().clear().apply();
                                getSharedPreferences("qb_cache_history", Context.MODE_PRIVATE).edit().clear().apply();
                                getSharedPreferences("qb_cache_maogai", Context.MODE_PRIVATE).edit().clear().apply();
                                getSharedPreferences("qb_cache_makesi", Context.MODE_PRIVATE).edit().clear().apply();
                            }
                        });
                        builder.setNeutralButton("不再提示", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = getSharedPreferences("qb_update", Context.MODE_PRIVATE).edit();
                                editor.putString("current_version", packageInfo.versionName);
                                editor.apply();
                                Snackbar.make(findViewById(R.id.fragment_container), "忽略更新题库", Snackbar.LENGTH_LONG).show();
                            }
                        });
                        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.create().show();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("选择题库");
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZM DEBUG", "onClick: drawer");
                drawer.openDrawer(GravityCompat.START);
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final SelectBankFragment fragment = new SelectBankFragment();
        fragmentTransaction.add(R.id.fragment_container, fragment,"main");
        fragmentTransaction.commit();

        updateButton.setOnClickListener(new OnClickUpdateListener());
        updateButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TikuVersion ver = ZMUtil.getTikuVersion(SelectBank.this);
                Snackbar.make(findViewById(R.id.ConstraintLayout), "题库当前版本：" + ver.version_name,Snackbar.LENGTH_SHORT).show();
                return true;
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_do_exam);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String item_name = item.getTitle().toString();
                if (item_name.equals("关于")) {
                    Intent intent = new Intent(SelectBank.this, AboutMe.class);
                    if (android.os.Build.VERSION.SDK_INT < 26) {
                        startActivity(intent);
                    } else {
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SelectBank.this).toBundle());
                    }
                }else if(item_name.equals("开始练习")){
                    item.setChecked(true);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    if(!(fragmentManager.findFragmentByTag("main") instanceof SelectBankFragment)){
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        SelectBankFragment fragment = new SelectBankFragment();
                        fragmentTransaction.replace(R.id.fragment_container, fragment,"main");
                        fragmentTransaction.commit();
                        updateButton.show();
                    }
                }else if(item_name.equals("查看错题本")){
                    item.setChecked(true);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    if(!(fragmentManager.findFragmentByTag("main") instanceof BookmarkFragment)){
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        BookmarkFragment fragment = new BookmarkFragment();
                        fragmentTransaction.replace(R.id.fragment_container, fragment,"main");
                        fragmentTransaction.commit();
                        updateButton.hide();
                    }
                } else if(item_name.equals("反馈")) {
                    Intent intent = new Intent(SelectBank.this, Feedback.class);
                    intent.putExtra("tiku_version", ZMUtil.getTikuVersion(SelectBank.this).version_name);
                    if (android.os.Build.VERSION.SDK_INT < 26) {
                        startActivity(intent);
                    } else {
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SelectBank.this).toBundle());
                    }
                }else {
                    Snackbar.make(findViewById(R.id.ConstraintLayout), "这个功能下个版本就有啦！", Snackbar.LENGTH_LONG).show();
                }
                drawer.closeDrawers();
                return true;
            }
        });
        ZMUtil.initUserDB(new QB(this));
    }

    public class OnClickUpdateListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            if (last_update + 60000 >= ZMUtil.time()) {
                //Snackbar.make(findViewById(R.id.ConstraintLayout), "你更新得太频繁了！等一会儿吧！", Snackbar.LENGTH_SHORT).show();
                //return;
            }
            last_update = ZMUtil.time();
            Animation circle_anim = AnimationUtils.loadAnimation(SelectBank.this, R.anim.rotate);
            LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
            if (isUpdateActivated) {
                Snackbar.make(findViewById(R.id.ConstraintLayout), "已经在更新了！", Snackbar.LENGTH_SHORT).show();
                return;
            } else {
                isUpdateActivated = true;
            }
            circle_anim.setInterpolator(interpolator);
            v.startAnimation(circle_anim);  //开始动画
            ZMUtil.checkUpdate(SelectBank.this, v, new Runnable() { //检查更新
                @Override
                public void run() {
                    v.clearAnimation();
                    isUpdateActivated = false;
                }
            }, new Runnable() {
                @Override
                public void run() {
                    v.clearAnimation();
                    isUpdateActivated = false;
                    Snackbar.make(findViewById(R.id.ConstraintLayout), "更新失败，请检查你的网络设置！", Snackbar.LENGTH_LONG).show();
                    last_update -= 10;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "再按一次返回退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
