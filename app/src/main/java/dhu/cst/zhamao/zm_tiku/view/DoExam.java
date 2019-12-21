package dhu.cst.zhamao.zm_tiku.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;

import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import dhu.cst.zhamao.zm_tiku.R;
import dhu.cst.zhamao.zm_tiku.object.JudgeResult;
import dhu.cst.zhamao.zm_tiku.object.TikuDisplaySection;
import dhu.cst.zhamao.zm_tiku.object.UserInfo;
import dhu.cst.zhamao.zm_tiku.utils.QB;
import dhu.cst.zhamao.zm_tiku.utils.ZMUtil;
import dhu.cst.zhamao.zm_tiku.value.RoundBackgroundColorSpan;
import dhu.cst.zhamao.zm_tiku.value.StatusCode;

public class DoExam extends AppCompatActivity implements View.OnClickListener {

    private String qb_name;
    private String user_id;
    private boolean shuffle;
    private boolean auto_skip;

    private LinearLayout layout1, layout2, layout3, layout4, layout5;
    private TextView last_question_text,current_progress_text,next_question_text;
    private TextView question_view;

    private Map<String, Boolean> key_down;

    private QB qb;

    private Map<String, TextView> bind_view;

    private Map<Integer, String> bind_ans;

    private Button submit_btn;

    private TikuDisplaySection section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.do_exam);

        bind_ans = new LinkedHashMap<>();
        bind_ans.put(R.id.answerLayout1, "A");
        bind_ans.put(R.id.answerLayout2, "B");
        bind_ans.put(R.id.answerLayout3, "C");
        bind_ans.put(R.id.answerLayout4, "D");
        bind_ans.put(R.id.answerLayout5, "E");

        submit_btn = findViewById(R.id.submitButton);

        qb = new QB(this);
        key_down = new LinkedHashMap<>();

        layout1 = findViewById(R.id.answerLayout1);
        layout2 = findViewById(R.id.answerLayout2);
        layout3 = findViewById(R.id.answerLayout3);
        layout4 = findViewById(R.id.answerLayout4);
        layout5 = findViewById(R.id.answerLayout5);
        //linearLayout5.setVisibility(View.GONE);
        question_view = findViewById(R.id.questionView);
        LinearLayout bottom_sheet_layout = findViewById(R.id.bottom_sheet_layout);
        last_question_text = findViewById(R.id.last_question_text);
        next_question_text = findViewById(R.id.next_question_text);
        current_progress_text = findViewById(R.id.current_progress_text);
        bind_view = new LinkedHashMap<>();

        this.qb_name = this.getIntent().getStringExtra("qb_name");
        this.user_id = this.getIntent().getStringExtra("user_id");
        this.shuffle = this.getIntent().getBooleanExtra("shuffle", false);
        int qb_mode = this.getIntent().getIntExtra("qb_mode", 0);
        this.auto_skip = this.getIntent().getBooleanExtra("auto_skip", false);
        //Toast.makeText(this, "shuffle: " + this.shuffle + ", mode: " + qb_mode + ", skip: " + this.auto_skip, Toast.LENGTH_LONG).show();
        int change_mode = this.getIntent().getIntExtra("change_mode", -1);
        if (change_mode != -1) {
            section = qb.changeMode(user_id, qb_name, change_mode, shuffle);
        } else {
            section = qb.next(user_id, qb_name, shuffle);
        }
        if (section.warning == StatusCode.no_wrong_question) {
            final MaterialAlertDialogBuilder normalDialog =
                    new MaterialAlertDialogBuilder(DoExam.this);
            normalDialog.setMessage("你还没有错题哦！");
            normalDialog.setNegativeButton("返回",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAfterTransition();
                        }
                    });
            // 显示
            normalDialog.show();
            return;
        }

        //设置 toolbar
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(QB.getTikuName(qb_name));
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

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_layout);

        updateDisplayQuestion(section);
    }

    private void updateDisplayQuestion(TikuDisplaySection section) {
        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(qb.getQuestionTypeCH(section.question));
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.WHITE);
        spannableStringBuilder2.setSpan(foregroundColorSpan, 0, spannableStringBuilder2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        /*
        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.parseColor("#20a0ff"));
        spannableStringBuilder2.setSpan(backgroundColorSpan, 0, spannableStringBuilder2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
         */
        RelativeSizeSpan span = new RelativeSizeSpan(0.9f);
        spannableStringBuilder2.setSpan(span, 0, spannableStringBuilder2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        String color_str = section.question.answer_type == 0 ? "#20a0ff" : "#512DA8";
        RoundBackgroundColorSpan spans = new RoundBackgroundColorSpan(Color.parseColor(color_str),Color.WHITE, this);
        spannableStringBuilder2.setSpan(spans, 0, spannableStringBuilder2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder2.insert(0, section.question.question + "     ");
        question_view.setText(spannableStringBuilder2);

        //question_view.setText(section.question.question);
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);
        layout4.setVisibility(View.GONE);
        layout5.setVisibility(View.GONE);
        layout1.setEnabled(true);
        layout2.setEnabled(true);
        layout3.setEnabled(true);
        layout4.setEnabled(true);
        layout5.setEnabled(true);
        setChoiceStatus(layout1, R.id.answerLayout1, false, true);
        submit_btn.setText("提交");

        UserInfo info;
        info = qb.getInfo(qb.getUserId(), qb_name);
        String progress_text = "当前 "+info.progress+"/"+info.count;
        current_progress_text.setText(progress_text);

        for (Map.Entry<String, String> entry : section.question.answer.entrySet()) {
            switch (entry.getKey()) {
                case "A":
                    layout1.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.answerText1)).setText(entry.getValue());
                    bind_view.put(entry.getKey(), (TextView) findViewById(R.id.answerLabel1));
                    break;
                case "B":
                    layout2.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.answerText2)).setText(entry.getValue());
                    bind_view.put(entry.getKey(), (TextView) findViewById(R.id.answerLabel2));
                    break;
                case "C":
                    layout3.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.answerText3)).setText(entry.getValue());
                    bind_view.put(entry.getKey(), (TextView) findViewById(R.id.answerLabel3));
                    break;
                case "D":
                    layout4.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.answerText4)).setText(entry.getValue());
                    bind_view.put(entry.getKey(), (TextView) findViewById(R.id.answerLabel4));
                    break;
                case "E":
                    layout5.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.answerText5)).setText(entry.getValue());
                    bind_view.put(entry.getKey(), (TextView) findViewById(R.id.answerLabel5));
                    break;
            }
            key_down.put(entry.getKey(), false);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submitButton:
                onSubmit();
                break;
            case R.id.answerLayout1:
            case R.id.answerLayout2:
            case R.id.answerLayout3:
            case R.id.answerLayout4:
            case R.id.answerLayout5:
                onChoiceClick((LinearLayout) view, view.getId());
                break;
        }
    }

    public void onSubmit() {
        if (submit_btn.getText().equals("提交")) {
            StringBuilder answer = new StringBuilder();
            for (Map.Entry<String, Boolean> entry : key_down.entrySet()) {
                if (entry.getValue()) answer.append(entry.getKey());
            }
            if(answer.toString().equals("")) {
                Snackbar.make(findViewById(R.id.doExamLayout), "请选择至少一个选项", Snackbar.LENGTH_SHORT).show();
                return;
            }
            JudgeResult result = qb.judge(user_id, answer.toString());
            for (Map.Entry<String, Boolean> entry : key_down.entrySet()) {
                if (entry.getValue())
                    setAnswerColor((MaterialTextView) Objects.requireNonNull(bind_view.get(entry.getKey())), R.color.white, R.drawable.circle_red);
            }
            String[] split = result.right_answer.split("");
            for (String s : split) {
                if (s.equals("")) continue;
                setAnswerColor((MaterialTextView) Objects.requireNonNull(bind_view.get(s)), R.color.white, R.drawable.circle_green);
            }
            this.submit_btn.setText("下一题");
            layout1.setEnabled(false);
            layout2.setEnabled(false);
            layout3.setEnabled(false);
            layout4.setEnabled(false);
            layout5.setEnabled(false);
            if(result.is_end) {
                ZMUtil.showDialog(this, result.res_message.get("title"), result.res_message.get("content"), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAfterTransition();
                    }
                });
                return;
            }
            if (result.status && auto_skip) {
                Timer updateResourceTimer = new Timer();
                TimerTask mTimerTask = new TimerTask() {//创建一个线程来执行run方法中的代码
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                section = qb.next(user_id, qb_name, shuffle);
                                updateDisplayQuestion(section);
                            }
                        });
                    }
                };
                updateResourceTimer.schedule(mTimerTask, 800);
            }

        } else if (submit_btn.getText().equals("下一题")) {
            section = qb.next(user_id, qb_name, shuffle);
            updateDisplayQuestion(section);
        }
    }

    public void onChoiceClick(LinearLayout view, int id) {
        try {
            String ans = bind_ans.get(id);
            //Log.e("DoExam", "Choice Clicked: " + ans);
            Boolean key_stat = key_down.get(ans);
            if (key_stat == null) throw new NullPointerException("Null Pointer");
            setChoiceStatus(view, id, !key_stat, section.question.answer_type == 0);
            if (this.auto_skip && section.question.answer_type == 0) {
                onSubmit();
            }
        } catch (NullPointerException e) {
            Log.e("DoExam", "NullPointerException");
        }
    }

    private void setChoiceStatus(LinearLayout view, int id, boolean status, boolean reset_others) {
        MaterialTextView cur;
        if (reset_others) {
            for (Map.Entry<Integer, String> entry : bind_ans.entrySet()) {
                if (entry.getKey() != id) {
                    key_down.put(entry.getValue(), false);
                    cur = (MaterialTextView) bind_view.get(bind_ans.get(entry.getKey()));
                    if (cur != null) {
                        setAnswerColor(cur, R.color.primary, R.drawable.circle);
                    }
                }
            }
        }
        key_down.put(bind_ans.get(id), status);
        cur = (MaterialTextView) view.getChildAt(0);
        setAnswerColor(cur, status ? R.color.white : R.color.primary, status ? R.drawable.circle_solid : R.drawable.circle);
        qb.pass();
    }

    /**
     * 点击Android手机上的返回按钮和界面上的返回按钮响应的函数
     * 弹一个提示框
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // your code.
    }

    private void setAnswerColor(MaterialTextView cur, int text_color, int bg_color) {
        cur.setTextColor(getResources().getColor(text_color));
        cur.setBackground(getDrawable(bg_color));
    }
}
