package dhu.cst.zhamao.zm_tiku.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import dhu.cst.zhamao.zm_tiku.R;

public class SelectMode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_mode);

        String[] COUNTRIES = new String[] {"循序做题", "练习错题", "只做单选", "只做多选", "随机做题"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, COUNTRIES);
        AutoCompleteTextView editTextFilledExposedDropdown = findViewById(R.id.selectModeDropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);

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
    }
}
