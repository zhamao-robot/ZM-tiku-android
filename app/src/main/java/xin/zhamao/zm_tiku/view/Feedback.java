package xin.zhamao.zm_tiku.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedHashMap;
import java.util.Map;

import xin.zhamao.zhamao.zm_tiku.R;
import xin.zhamao.zm_tiku.utils.ZMUtil;


public class Feedback extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        //设置 toolbar
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("反馈");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        final Map<String, String> extra = new LinkedHashMap<>();
        if(getIntent().getStringExtra("qb_name") != null) extra.put("qb_name", getIntent().getStringExtra("qb_name"));
        if(getIntent().getStringExtra("tiku_id") != null) extra.put("tiku_id", getIntent().getStringExtra("tiku_id"));
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
        Button btn = findViewById(R.id.feedback_submit);
        final EditText contact = findViewById(R.id.contact_input);
        final EditText title = findViewById(R.id.title_input);
        final EditText content = findViewById(R.id.content_input);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contact.getText().toString().equals("")) Snackbar.make(findViewById(R.id.activity_feedback), "请填写你的联系方式", Snackbar.LENGTH_SHORT).show();
                else if(title.getText().toString().equals("")) Snackbar.make(findViewById(R.id.activity_feedback), "请填写标题", Snackbar.LENGTH_SHORT).show();
                else if(content.getText().toString().equals("")) Snackbar.make(findViewById(R.id.activity_feedback), "请填写你的反馈内容", Snackbar.LENGTH_SHORT).show();
                else ZMUtil.submitFeedback(
                        Feedback.this,
                        contact.getText().toString(),
                        title.getText().toString(),
                        content.getText().toString(),
                        extra,
                        new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Feedback.this, "成功提交！", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Feedback.this, "提交失败！", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                );
            }
        });
    }
}
