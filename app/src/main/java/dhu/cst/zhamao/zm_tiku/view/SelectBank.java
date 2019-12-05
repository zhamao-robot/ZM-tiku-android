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

public class SelectBank extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_bank);
        MaterialCardView materialCardView1 = findViewById(R.id.materialCardView1);
        startSelectModeView(materialCardView1, "近代史题库");

        MaterialCardView materialCardView2 = findViewById(R.id.materialCardView2);
        startSelectModeView(materialCardView2, "马克思题库");

        MaterialCardView materialCardView3 = findViewById(R.id.materialCardView3);
        startSelectModeView(materialCardView3, "毛概题库");

        MaterialCardView materialCardView4 = findViewById(R.id.materialCardView4);
        startSelectModeView(materialCardView4, "思修题库");

        final FloatingActionButton updateButton = findViewById(R.id.upateButton);
        updateButton.setOnClickListener(new OnClickUpdateListener());

    }

    public class OnClickUpdateListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            Animation circle_anim = AnimationUtils.loadAnimation(SelectBank.this, R.anim.rotate);
            LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
            circle_anim.setInterpolator(interpolator);
            v.startAnimation(circle_anim);  //开始动画
            Timer updateResourceTimer = new Timer();
            TimerTask mTimerTask = new TimerTask() {//创建一个线程来执行run方法中的代码
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            v.clearAnimation();
                            Snackbar.make(findViewById(R.id.ConstraintLayout),"成功更新题库",Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            };
            updateResourceTimer.schedule(mTimerTask, 3000);
        }
    }

    private void startSelectModeView(MaterialCardView view, final String qb_name) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectBank.this,SelectMode.class);
                intent.putExtra("qb_name", qb_name);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SelectBank.this).toBundle());
            }
        });
    }
}
