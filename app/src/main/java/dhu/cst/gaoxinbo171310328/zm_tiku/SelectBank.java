package dhu.cst.gaoxinbo171310328.zm_tiku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;

public class SelectBank extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_bank);
        getSupportActionBar().setTitle("选择题库");

        MaterialCardView materialCardView = findViewById(R.id.materialCardView1);
        materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectBank.this,SelectMode.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
