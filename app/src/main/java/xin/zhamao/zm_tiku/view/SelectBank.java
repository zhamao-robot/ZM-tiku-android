package xin.zhamao.zm_tiku.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import xin.zhamao.zm_tiku.BuildConfig;
import xin.zhamao.zm_tiku.R;
import xin.zhamao.zm_tiku.components.DialogUI;
import xin.zhamao.zm_tiku.utils.FileSystem;
import xin.zhamao.zm_tiku.utils.QB;
import xin.zhamao.zm_tiku.utils.TikuManager;
import xin.zhamao.zm_tiku.utils.ZMUtil;

public class SelectBank extends AppCompatActivity {

    public static boolean isUpdateActivated = false;
    private long mExitTime;
    private long last_update = 0;
    private DialogUI dialogUI;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        Window window = getWindow();
        this.dialogUI = new DialogUI(this);

        int statusColor = R.color.green;

        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // 设置悬浮的更新按钮
        final FloatingActionButton updateButton = findViewById(R.id.upateButton);
        updateButton.show();

        // 设置抽屉
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // 存在 version.json，表明是1.6.x及以下版本的，存在问题，需要重置所有数据再启动
        if (FileSystem.isInternalFileExists(this, "version.json")) {
            dialogUI.showRestoreConfirmDialog("此次覆盖更新需要重置题库数据，且不可恢复！", "点击确定将重置，并退出 App。否则请立即退出 App 并安装旧版。", false);
            return;
        }

        // 初始化题库
        TikuManager tikuManager = new TikuManager(this);
        int importCnt = tikuManager.initTiku();
        if (importCnt != 0) {
            Snackbar.make(findViewById(R.id.appBar), "已加载 " + importCnt + " 个题库！", Snackbar.LENGTH_LONG).show();
        }

        // 如果有题库已经存在，且版本不一致，就弹出询问是否更新
        if (tikuManager.isNeedUpdate() && !getSharedPreferences("settings", 0).getBoolean("no_update_dialog", false)) {
            dialogUI.showTikuUpdateDialog("以下题库有更新，请根据需求进行更新，更新后请重启一次应用", dialogUI.makeUpdateListLayout(tikuManager));
        }

        if (!BuildConfig.DEBUG) {
            baseCheckUpdate();
        }

        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("选择题库" + (BuildConfig.DEBUG ? "(调试模式)" : ""));
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
        fragmentTransaction.add(R.id.fragment_container, fragment, "main");
        fragmentTransaction.commit();

        updateButton.setOnClickListener(new OnClickUpdateListener());

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_do_exam);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String item_name = item.getTitle().toString();
                switch (item_name) {
                    case "关于": {
                        Intent intent = new Intent(SelectBank.this, AboutMe.class);
                        if (android.os.Build.VERSION.SDK_INT < 26) {
                            startActivity(intent);
                        } else {
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SelectBank.this).toBundle());
                        }
                        break;
                    }
                    case "开始练习": {
                        item.setChecked(true);
                        updateButton.setVisibility(View.VISIBLE);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        if (!(fragmentManager.findFragmentByTag("main") instanceof SelectBankFragment)) {
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            SelectBankFragment fragment = new SelectBankFragment();
                            fragmentTransaction.replace(R.id.fragment_container, fragment, "main");
                            fragmentTransaction.commit();
                            updateButton.show();
                        }
                        break;
                    }
                    case "查看错题本": {
                        item.setChecked(true);
                        updateButton.setVisibility(View.GONE);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        if (!(fragmentManager.findFragmentByTag("main") instanceof BookmarkFragment)) {
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            BookmarkFragment fragment = new BookmarkFragment();
                            fragmentTransaction.replace(R.id.fragment_container, fragment, "main");
                            fragmentTransaction.commit();
                            updateButton.hide();
                        }
                        break;
                    }
                    case "反馈": {
                        Intent intent = new Intent(SelectBank.this, Feedback.class);
                        if (android.os.Build.VERSION.SDK_INT < 26) {
                            startActivity(intent);
                        } else {
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SelectBank.this).toBundle());
                        }
                        break;
                    }
                    case "设置":
                        Intent intent = new Intent(SelectBank.this, Settings.class);
                        if (android.os.Build.VERSION.SDK_INT < 26) {
                            startActivity(intent);
                        } else {
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SelectBank.this).toBundle());
                        }
                        break;
                    case "查看公告":
                        SharedPreferences title = getSharedPreferences("notify", Context.MODE_PRIVATE);
                        String rTitle = title.getString("title", "暂无公告");
                        String rContent = title.getString("content", "暂无公告");
                        ZMUtil.showDialog(SelectBank.this, rTitle, rContent, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        break;
                    default:
                        Snackbar.make(findViewById(R.id.fragment_container), "这个功能下个版本就有啦！", Snackbar.LENGTH_LONG).show();
                        break;
                }
                drawer.closeDrawers();
                return true;
            }
        });
        ZMUtil.initUserDB(new QB(this));
    }

    private void baseCheckUpdate() {
        boolean auto_check = getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("check_update", true);
        if(auto_check) {
            ZMUtil.checkUpdate(this, new Runnable() {
                @Override
                public void run() {
                }
            }, new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    public class OnClickUpdateListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            if (last_update + 60000 >= ZMUtil.time()) {
                Snackbar.make(findViewById(R.id.fragment_container), "你更新得太频繁了！等一会儿吧！", Snackbar.LENGTH_SHORT).show();
                return;
            }
            last_update = ZMUtil.time();
            Animation circle_anim = AnimationUtils.loadAnimation(SelectBank.this, R.anim.rotate);
            LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
            if (isUpdateActivated) {
                Snackbar.make(findViewById(R.id.fragment_container), "已经在更新了！", Snackbar.LENGTH_SHORT).show();
                return;
            } else {
                isUpdateActivated = true;
            }
            circle_anim.setInterpolator(interpolator);
            v.startAnimation(circle_anim);  //开始动画
            ZMUtil.checkUpdate(SelectBank.this, new Runnable() { //检查更新
                @Override
                public void run() {
                    v.clearAnimation();
                    isUpdateActivated = false;
                    Snackbar.make(findViewById(R.id.appBar), "检查更新完毕", Snackbar.LENGTH_LONG).show();
                }
            }, new Runnable() {
                @Override
                public void run() {
                    v.clearAnimation();
                    isUpdateActivated = false;
                    Snackbar.make(findViewById(R.id.appBar), "更新失败，请检查你的网络设置！", Snackbar.LENGTH_LONG).show();
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
