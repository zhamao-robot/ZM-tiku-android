package dhu.cst.zhamao.zm_tiku.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Timer;
import java.util.TimerTask;

import dhu.cst.zhamao.zm_tiku.R;

public class SelectBank extends AppCompatActivity implements View.OnClickListener {

    MaterialCardView materialCardView1, materialCardView2, materialCardView3, materialCardView4;

    boolean isUpdateActivated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_bank);
        final FloatingActionButton updateButton = findViewById(R.id.upateButton);
        updateButton.setOnClickListener(new OnClickUpdateListener());
        (materialCardView1 = findViewById(R.id.materialCardView1)).setOnClickListener(this);
        (materialCardView2 = findViewById(R.id.materialCardView2)).setOnClickListener(this);
        (materialCardView3 = findViewById(R.id.materialCardView3)).setOnClickListener(this);
        (materialCardView4 = findViewById(R.id.materialCardView4)).setOnClickListener(this);
    }

    public class OnClickUpdateListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            Animation circle_anim = AnimationUtils.loadAnimation(SelectBank.this, R.anim.rotate);
            LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
            if(isUpdateActivated) {
                Snackbar.make(findViewById(R.id.ConstraintLayout), "已经在更新了！", Snackbar.LENGTH_SHORT).show();
                return;
            } else {
                isUpdateActivated = true;
            }
            circle_anim.setInterpolator(interpolator);
            v.startAnimation(circle_anim);  //开始动画
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
            updateResourceTimer.schedule(mTimerTask, 3000);
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
}
