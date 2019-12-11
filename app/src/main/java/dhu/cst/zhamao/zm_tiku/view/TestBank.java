package dhu.cst.zhamao.zm_tiku.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dhu.cst.zhamao.zm_tiku.R;
import dhu.cst.zhamao.zm_tiku.object.QBSection;
import dhu.cst.zhamao.zm_tiku.utils.QB;
import dhu.cst.zhamao.zm_tiku.utils.ZMUtil;

public class TestBank extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_bank);
        updateView();
    }

    private void updateView() {
        ListView view = findViewById(R.id.sqlList);
        QB qb = new QB(this);
        //Toast.makeText(TestBank.this, "Database information listed", Toast.LENGTH_LONG).show();
        List<QBSection> list = qb.getDB().queryQB("SELECT * FROM qb", new String[]{});
        if (list.isEmpty()) Toast.makeText(TestBank.this, "Nothing", Toast.LENGTH_SHORT).show();
        List<String> p = new ArrayList<>();
        for (QBSection s : list) {
            p.add(s.user_id +
                    "\nqb_name: " + s.qb_name + "\n" +
                    "doing: " + s.doing + "\n" +
                    "wrong: " + ZMUtil.implode(",", (ArrayList<Integer>) s.wrong) + "\n" +
                    "doing_list: " + ZMUtil.implode(",", (ArrayList<Integer>) s.doing_list) + "\n" +
                    "current_ans: " + s.current_ans + "\n" +
                    "qb_mode: " + s.qb_mode
            );
        }
        String[] s = new String[p.size()];
        s = p.toArray(s);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, s);
        view.setAdapter(itemsAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.execute_sql) {
            EditText txt = findViewById(R.id.sql_input);
            QB qb = new QB(this);
            qb.getDB().queryQB(txt.getText().toString(), new String[]{});
            Toast.makeText(this, "query: " + txt.getText().toString(), Toast.LENGTH_LONG).show();
            updateView();
        }
    }
}
