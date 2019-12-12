package dhu.cst.zhamao.zm_tiku.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

import dhu.cst.zhamao.zm_tiku.R;
import dhu.cst.zhamao.zm_tiku.object.UserInfo;
import dhu.cst.zhamao.zm_tiku.utils.QB;

public class SelectMode extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private int mode_selected_tmp = 0, mode_selected = 0;

    private String qb_name;

    private QB qb = null;

    SwitchMaterial shuffleSwitch;
    SwitchMaterial autoNextSwitch;

    final String[] mode_list = {"顺序做题", "只做单选", "只做多选", "错题练习", "随机做题"};

    private UserInfo info;

    /**
     * 当从其他页面返回到这个页面时，更新页面的内容
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        updatePageInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_mode);


        //初始化QB
        qb = new QB(this);
        qb_name = QB.getTikuName(Objects.requireNonNull(getIntent().getStringExtra("qb_name")));

        (shuffleSwitch = findViewById(R.id.shuffleSwitch)).setOnCheckedChangeListener(this);
        (autoNextSwitch = findViewById(R.id.autoNextSwitch)).setOnCheckedChangeListener(this);

        findViewById(R.id.arrowImage).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(SelectMode.this, TestBank.class);
                intent.putExtra("user_id", qb.getUserId());
                intent.putExtra("qb_name", qb_name);
                startActivity(intent);
                return true;
            }
        });
        if (qb.getQBData(qb.getUserId(), qb_name) == null) {
            qb.insertQBData(qb.getUserId(), qb_name);
            Toast.makeText(SelectMode.this, "插入数据中", Toast.LENGTH_LONG).show();
        }

        //设置 toolbar
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(QB.getTikuName(qb_name));
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

        //更新页面内容 通过 database
        updatePageInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.doExamButton:

                Intent intent = new Intent(SelectMode.this, DoExam.class);
                intent.putExtra("qb_name", qb_name); //题库名称
                intent.putExtra("shuffle", shuffleSwitch.isActivated());
                intent.putExtra("auto_skip", autoNextSwitch.isChecked());
                intent.putExtra("qb_mode", info.mode);
                intent.putExtra("user_id", qb.getUserId());
                if(((Button)v).getText().equals("重新做题")) {
                    intent.putExtra("change_mode", mode_selected);
                }

                if (android.os.Build.VERSION.SDK_INT < 26) {
                    startActivity(intent);
                } else {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SelectMode.this).toBundle());
                }
                break;
            case R.id.selectBankButton:
                if (android.os.Build.VERSION.SDK_INT >= 26) {
                    finishAfterTransition();
                } else {
                    finish();
                }
                break;
            case R.id.switchModeLayout:
                new MaterialAlertDialogBuilder(SelectMode.this)
                        .setTitle("选择做题模式")
                        .setSingleChoiceItems(mode_list, mode_selected, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mode_selected_tmp = which;
                            }
                        })
                        .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(mode_selected != mode_selected_tmp) {
                                    Toast.makeText(SelectMode.this, "你切换了模式，这将刷新你的做题进度！", Toast.LENGTH_LONG).show();
                                    Button btn = findViewById(R.id.doExamButton);
                                    btn.setText("重新做题");
                                }
                                mode_selected = mode_selected_tmp;
                                TextView switchModeNameText = findViewById(R.id.switchModeNameText);
                                switchModeNameText.setText(mode_list[mode_selected]);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.autoNextSwitch:
                Snackbar.make(findViewById(R.id.ConstraintLayout), (isChecked ? "自动跳过已打开" : "自动跳过已关闭"), Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.shuffleSwitch:
                Snackbar.make(findViewById(R.id.ConstraintLayout), (isChecked ? "打乱顺序已打开" : "打乱顺序已关闭"), Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    public void updatePageInfo() {

        info = qb.getInfo(qb.getUserId(), qb_name);
        //更新progress进度显示
        TextView progressText = findViewById(R.id.progressText);
        String progress = info.progress + " / " + info.count;
        progressText.setText(progress);
        //更新按钮
        if(info.progress != 0) {
            Button btn = findViewById(R.id.doExamButton);
            btn.setText("继续做题");
        }
        //更新打乱选项的开关状态
        shuffleSwitch.setActivated(info.shuffle);
        mode_selected = info.mode;
        //更新做题模式的TextView
        TextView currentModeText = findViewById(R.id.currentModeText);
        currentModeText.setText(mode_list[info.mode]);
        TextView switchModeNameText = findViewById(R.id.switchModeNameText);
        switchModeNameText.setText(mode_list[info.mode]);
    }
}
