package dhu.cst.zhamao.zm_tiku.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import dhu.cst.zhamao.zm_tiku.R;
import dhu.cst.zhamao.zm_tiku.object.QBSection;
import dhu.cst.zhamao.zm_tiku.object.UserInfo;
import dhu.cst.zhamao.zm_tiku.utils.QB;

public class SelectMode extends AppCompatActivity {

    private int mode_selected_tmp = 0, mode_selected = 0;

    private String qb_name;

    private QB qb;

    SwitchMaterial shuffleSwitch;

    final String[] mode_list = {"顺序做题", "只做单选", "只做多选", "错题练习", "随机做题"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_mode);

        //初始化QB
        qb = new QB(this);
        qb_name = QB.getTikuName(Objects.requireNonNull(getIntent().getStringExtra("qb_name")));

        //初始化View的变量
        shuffleSwitch = findViewById(R.id.shuffleSwitch);

        //设置Activity的标题栏为题库名称

        updatePageInfo();
        shuffleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Snackbar.make(findViewById(R.id.ConstraintLayout), "打乱顺序已打开", Snackbar.LENGTH_SHORT).show();
                else
                    Snackbar.make(findViewById(R.id.ConstraintLayout), "打乱顺序已关闭", Snackbar.LENGTH_SHORT).show();
            }
        });

        SwitchMaterial autoNextSwitch = findViewById(R.id.autoNextSwitch);
        autoNextSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Snackbar.make(findViewById(R.id.ConstraintLayout), "自动跳过已打开", Snackbar.LENGTH_SHORT).show();
                else
                    Snackbar.make(findViewById(R.id.ConstraintLayout), "自动跳过已关闭", Snackbar.LENGTH_SHORT).show();
            }
        });

        Button doExam = findViewById(R.id.doExamButton);
        doExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SelectMode.this, DoExam.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SelectMode.this).toBundle());
            }
        });

        Button selectMode = findViewById(R.id.selectBankButton);
        selectMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });

        ImageView backImage = findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });

        LinearLayout switchModeLayout = findViewById(R.id.switchModeLayout);
        switchModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



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
                                mode_selected = mode_selected_tmp;
                                TextView switchModeNameText = findViewById(R.id.switchModeNameText);
                                switchModeNameText.setText(mode_list[mode_selected]);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
    }

    private void updatePageInfo(){
        //设置Activity的标题栏为题库名称
        ((TextView) findViewById(R.id.select_mode_name)).setText(QB.getTikuName(qb_name));

        UserInfo info = qb.getInfo(qb.getUserId(), qb_name);
        //更新progress进度显示
        TextView progressText = findViewById(R.id.progressText);
        String progress = info.progress + " / " + info.count;
        progressText.setText(progress);
        //更新打乱选项的开关状态
        shuffleSwitch.setActivated(info.shuffle);
        //更新做题模式的TextView
        TextView currentModeText = findViewById(R.id.currentModeText);
        currentModeText.setText(mode_list[info.mode]);
        TextView switchModeNameText = findViewById(R.id.switchModeNameText);
        switchModeNameText.setText(mode_list[info.mode]);
    }
}
