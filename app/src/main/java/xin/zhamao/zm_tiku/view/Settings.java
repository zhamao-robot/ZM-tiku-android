package xin.zhamao.zm_tiku.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import xin.zhamao.zhamao.zm_tiku.R;

public class Settings extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ((CompoundButton) findViewById(R.id.autoCheckSetting)).setOnCheckedChangeListener(this);
        SharedPreferences pref = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SwitchMaterial button = findViewById(R.id.autoCheckSetting);
        button.setChecked(pref.getBoolean("check_update", true));

        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("设置");
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.autoCheckSetting) {
            Snackbar.make(findViewById(R.id.setting_layout), (isChecked ? "自动检查更新已打开" : "自动检查更新已关闭"), Snackbar.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
            editor.putBoolean("check_update", isChecked);
            editor.apply();
        }
    }
}
