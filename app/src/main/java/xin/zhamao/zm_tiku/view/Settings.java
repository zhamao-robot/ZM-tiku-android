package xin.zhamao.zm_tiku.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.google.android.material.switchmaterial.SwitchMaterial;

import xin.zhamao.zm_tiku.R;
import xin.zhamao.zm_tiku.components.DialogUI;

public class Settings extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ((CompoundButton) findViewById(R.id.autoCheckSetting)).setOnCheckedChangeListener(this);
        SharedPreferences pref = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SwitchMaterial button = findViewById(R.id.autoCheckSetting);
        button.setChecked(pref.getBoolean("check_update", true));

        ((CompoundButton) findViewById(R.id.ignoreUpdateSetting)).setOnCheckedChangeListener(this);
        SharedPreferences pref2 = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SwitchMaterial button2 = findViewById(R.id.ignoreUpdateSetting);
        button2.setChecked(pref2.getBoolean("no_update_dialog", false));

        ((Button) findViewById(R.id.restoreAll)).setOnClickListener(this);

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

        // 设置
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences.Editor editor = getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        if (buttonView.getId() == R.id.autoCheckSetting) {
            editor.putBoolean("check_update", isChecked);
        } else if (buttonView.getId() == R.id.ignoreUpdateSetting) {
            editor.putBoolean("no_update_dialog", isChecked);
        }
        editor.apply();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.restoreAll) {
            // 删除自己App的所有内部存储的数据并退出App
            DialogUI dialogUI = new DialogUI(this);
            dialogUI.showRestoreConfirmDialog("你确定要清空所有的数据吗，做题进度、错题进度均不可恢复哟！");
        }
    }
}
