package dhu.cst.zhamao.zm_tiku.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import dhu.cst.zhamao.zm_tiku.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler startMainActivity = new Handler();
        startMainActivity.postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, SelectBank.class);
                startActivity(intent);
                finish();
            }
        },1000);
    }
}
