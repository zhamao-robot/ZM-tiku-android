package dhu.cst.zhamao.zm_tiku.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dhu.cst.zhamao.zm_tiku.R;

public class DoExam extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.do_exam);
        ImageView backImage = findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });

        LinearLayout linearLayout1 = findViewById(R.id.answerLayout1);
        linearLayout1.setOnClickListener(new onClickAnswer());
        LinearLayout linearLayout2 = findViewById(R.id.answerLayout2);
        linearLayout2.setOnClickListener(new onClickAnswer());
        LinearLayout linearLayout3 = findViewById(R.id.answerLayout3);
        linearLayout3.setOnClickListener(new onClickAnswer());
        LinearLayout linearLayout4 = findViewById(R.id.answerLayout4);
        linearLayout4.setOnClickListener(new onClickAnswer());
        LinearLayout linearLayout5 = findViewById(R.id.answerLayout5);
        linearLayout5.setOnClickListener(new onClickAnswer());


    }

    public class onClickAnswer implements View.OnClickListener{
        @Override
        public void onClick(View v){
            LinearLayout layout  = (LinearLayout)v;
            TextView cur = (TextView)layout.getChildAt(0);
            if(cur.getCurrentTextColor() != getResources().getColor(R.color.white)){
                cur.setTextColor(getResources().getColor(R.color.white));
                cur.setBackground(getDrawable(R.drawable.circle_solid));
            }else{
                cur.setTextColor(getResources().getColor(R.color.primary));
                cur.setBackground(getDrawable(R.drawable.circle));
            }
        }
    }
}
