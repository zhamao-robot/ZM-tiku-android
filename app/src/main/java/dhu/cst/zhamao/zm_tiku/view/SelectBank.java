package dhu.cst.zhamao.zm_tiku.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
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

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

import dhu.cst.zhamao.zm_tiku.R;
import dhu.cst.zhamao.zm_tiku.object.TikuVersion;
import dhu.cst.zhamao.zm_tiku.utils.QB;
import dhu.cst.zhamao.zm_tiku.utils.ZMUtil;

public class SelectBank extends AppCompatActivity implements View.OnClickListener {

    MaterialCardView materialCardView1, materialCardView2, materialCardView3, materialCardView4;

    boolean isUpdateActivated = false;

    private long mExitTime;

    private long last_update = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        final FloatingActionButton updateButton = findViewById(R.id.upateButton);
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

        updateButton.setOnClickListener(new OnClickUpdateListener());
        updateButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TikuVersion ver = ZMUtil.getTikuVersion(SelectBank.this);
                Snackbar.make(findViewById(R.id.ConstraintLayout), "题库当前版本：" +
                                ver.version_name,
                        Snackbar.LENGTH_SHORT).show();
                return true;
            }
        });
        (materialCardView1 = findViewById(R.id.materialCardView1)).setOnClickListener(this);
        (materialCardView2 = findViewById(R.id.materialCardView2)).setOnClickListener(this);
        (materialCardView3 = findViewById(R.id.materialCardView3)).setOnClickListener(this);
        (materialCardView4 = findViewById(R.id.materialCardView4)).setOnClickListener(this);

        materialCardView1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String item_name = item.getTitle().toString();
                if(item_name.equals("关于")) {
                    Intent intent = new Intent(SelectBank.this, AboutMe.class);
                    if (android.os.Build.VERSION.SDK_INT < 26) {
                        startActivity(intent);
                    } else {
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SelectBank.this).toBundle());
                    }
                } else {
                    Snackbar.make(findViewById(R.id.ConstraintLayout), "这个功能下个版本就有啦！", Snackbar.LENGTH_LONG).show();
                }
                /*else {
                    Snackbar.make(findViewById(R.id.ConstraintLayout), item.getTitle().toString(), Snackbar.LENGTH_SHORT).show();
                    //item.setChecked(true);
                }*/
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
                Snackbar.make(findViewById(R.id.ConstraintLayout), "你更新得太频繁了！等一会儿吧！", Snackbar.LENGTH_SHORT).show();
                return;
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
            });
            /*
            Timer updateResourceTimer = new Timer();
            TimerTask mTimerTask = new TimerTask() {//创建一个线程来执行run方法中的代码
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            v.clearAnimation();
                            Snackbar.make(findViewById(R.id.ConstraintLayout), "成功更新题库！", Snackbar.LENGTH_SHORT).show();
                            isUpdateActivated = false;
                        }
                    });
                }
            };
            updateResourceTimer.schedule(mTimerTask, 3000);*/
        }
    }

    @Override
    public void onClick(View v) {
        String qb_name = "";
        switch (v.getId()) {
            case R.id.materialCardView1:
                qb_name = "近代史题库";
                break;
            case R.id.materialCardView2:
                qb_name = "马克思题库";
                break;
            case R.id.materialCardView3:
                qb_name = "毛概题库";
                break;
            case R.id.materialCardView4:
                qb_name = "思修题库";
                break;
        }
        Intent intent = new Intent(SelectBank.this, SelectMode.class);
        intent.putExtra("qb_name", qb_name);
        if (android.os.Build.VERSION.SDK_INT < 26) {
            startActivity(intent);
        } else {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SelectBank.this).toBundle());
        }
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
