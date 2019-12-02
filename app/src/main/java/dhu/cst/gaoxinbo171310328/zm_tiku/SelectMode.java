package dhu.cst.gaoxinbo171310328.zm_tiku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class SelectMode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_mode);

        String[] COUNTRIES = new String[] {"循序做题", "练习错题", "只做单选", "只做多选", "随机做题"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        COUNTRIES);
        AutoCompleteTextView editTextFilledExposedDropdown = findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);

        Button doExam = findViewById(R.id.doExamButton);
        doExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMode.this,DoExam.class);
                startActivity(intent);
                finish();
            }
        });

        Button selectMode = findViewById(R.id.selectBankButton);
        selectMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMode.this,SelectBank.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
