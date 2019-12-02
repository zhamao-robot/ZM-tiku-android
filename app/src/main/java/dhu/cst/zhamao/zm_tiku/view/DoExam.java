package dhu.cst.zhamao.zm_tiku.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import dhu.cst.zhamao.zm_tiku.R;
import dhu.cst.zhamao.zm_tiku.utils.QB;

public class DoExam extends AppCompatActivity {

    private QB qb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.do_exam);
        this.qb = new QB(this);
    }

    public QB getQB() {
        return qb;
    }
}
