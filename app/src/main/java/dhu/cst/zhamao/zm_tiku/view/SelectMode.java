package dhu.cst.zhamao.zm_tiku.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import dhu.cst.zhamao.zm_tiku.R;

public class SelectMode extends AppCompatActivity {

    private int mode_selected_tmp = 0,mode_selected = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_mode);

        SwitchMaterial shuffleSwitch = findViewById(R.id.shuffleSwitch);
        shuffleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) Snackbar.make(findViewById(R.id.ConstraintLayout),"打乱顺序已打开",Snackbar.LENGTH_SHORT).show();
                else Snackbar.make(findViewById(R.id.ConstraintLayout),"打乱顺序已关闭",Snackbar.LENGTH_SHORT).show();
            }
        });

        SwitchMaterial autoNextSwitch = findViewById(R.id.autoNextSwitch);
        autoNextSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) Snackbar.make(findViewById(R.id.ConstraintLayout),"自动跳过已打开",Snackbar.LENGTH_SHORT).show();
                else Snackbar.make(findViewById(R.id.ConstraintLayout),"自动跳过已关闭",Snackbar.LENGTH_SHORT).show();
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
                final String[] mode_list = {"顺序做题", "错题练习", "只做单选", "只做多选", "随机做题"};

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
                        .setNegativeButton("取消",null)
                        .show();
            }
        });
    }
}
