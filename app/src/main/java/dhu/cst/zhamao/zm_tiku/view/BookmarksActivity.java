package dhu.cst.zhamao.zm_tiku.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

import dhu.cst.zhamao.zm_tiku.R;
import dhu.cst.zhamao.zm_tiku.utils.QB;

public class BookmarksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        //设置 toolbar
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("错题本");
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
}
