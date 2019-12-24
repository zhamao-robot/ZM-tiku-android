package dhu.cst.zhamao.zm_tiku.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;

import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import dhu.cst.zhamao.zm_tiku.R;
import dhu.cst.zhamao.zm_tiku.object.JudgeResult;
import dhu.cst.zhamao.zm_tiku.object.QBCacheSection;
import dhu.cst.zhamao.zm_tiku.object.QBSection;
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

    private LinearLayout layout1, layout2, layout3, layout4, layout5, bottom_sheet_layout;
    private TextView last_question_text, current_progress_text, next_question_text;
    private TextView question_view;
    private RecyclerView answer_sheet;

    private Map<String, Boolean> key_down;
    private QB qb;
    private Map<String, TextView> bind_view;
    private Map<Integer, String> bind_ans;
    private Button submit_btn;
    private TikuDisplaySection section;
    private String last_error = "";

    private int view_id;
    private int questions_count;

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
        bottom_sheet_layout = findViewById(R.id.bottom_sheet_layout);
        last_question_text = findViewById(R.id.last_question_text);
        next_question_text = findViewById(R.id.next_question_text);
        current_progress_text = findViewById(R.id.current_progress_text);
        answer_sheet = findViewById(R.id.answer_sheet);
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

        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_layout);
        final View background_mask = findViewById(R.id.background_mask);
        background_mask.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            }
        });
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    background_mask.setVisibility(View.GONE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                background_mask.setVisibility(View.VISIBLE);
                background_mask.bringToFront();
                background_mask.setAlpha(slideOffset);
            }
        });

        current_progress_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        UserInfo info;
        info = qb.getInfo(qb.getUserId(), qb_name);
        questions_count = info.count;

        // 设置答题卡
        int[] answers = new int[questions_count];
        QBSection qbSection = new QBSection(qb, qb.getUserId(), qb_name);
        int i;
        for(i = 0;i < qbSection.current_ans;i++){
            int doing = qbSection.doing_list.get(i);
            if(qbSection.wrong.indexOf(doing) >= 0){
                answers[i] = 1;
            }else{
                answers[i] = 2;
            }
        }
        answer_sheet.setLayoutManager(new GridLayoutManager(this, 5));
        AnswerSheetAdapter adapter = new AnswerSheetAdapter(this, answers);
        answer_sheet.setAdapter(adapter);

        updateDisplayQuestion(section);
    }

    /**
     * 显示上一题或者下一题，就是显示历史题目的
     *
     * @param section section
     */
    private void updateLastDisplayQuestion(QBCacheSection section) {
        UserInfo info = qb.getInfo(qb.getUserId(), qb_name);
        updateDisplayQuestionAnswer(
                section.answer_type,
                section.question,
                "回到当前",
                section.key,
                "当前 " + section.id + "/" + info.count,
                false
        );
        for (String a : section.user_choice.split("")) {
            if (a.equals("")) continue;
            setAnswerColor((MaterialTextView) Objects.requireNonNull(bind_view.get(a)), R.color.white, R.drawable.circle_red);
        }

        String[] split = section.real_choice.split("");
        for (String s : split) {
            if (s.equals("")) continue;
            setAnswerColor((MaterialTextView) Objects.requireNonNull(bind_view.get(s)), R.color.white, R.drawable.circle_green);
        }
        view_id = section.id;
    }

    private void updateDisplayQuestionAnswer(int answer_type, String question, String btn_text, Map<String, String> answer_list, String progress_text, boolean choice_enable) {
        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(qb.getQuestionTypeCH(answer_type));
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.WHITE);
        spannableStringBuilder2.setSpan(foregroundColorSpan, 0, spannableStringBuilder2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        RelativeSizeSpan span = new RelativeSizeSpan(0.9f);
        spannableStringBuilder2.setSpan(span, 0, spannableStringBuilder2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        String color_str = answer_type == 0 ? "#20a0ff" : "#512DA8";
        RoundBackgroundColorSpan spans = new RoundBackgroundColorSpan(Color.parseColor(color_str), Color.WHITE, this);
        spannableStringBuilder2.setSpan(spans, 0, spannableStringBuilder2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder2.insert(0, question + "     ");
        question_view.setText(spannableStringBuilder2);
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);
        layout4.setVisibility(View.GONE);
        layout5.setVisibility(View.GONE);
        layout1.setEnabled(choice_enable);
        layout2.setEnabled(choice_enable);
        layout3.setEnabled(choice_enable);
        layout4.setEnabled(choice_enable);
        layout5.setEnabled(choice_enable);

        submit_btn.setText(btn_text);
        current_progress_text.setText(progress_text);
        setChoiceStatus(layout1, R.id.answerLayout1, false, true);


        current_progress_text.post(new Runnable() {
            @Override
            public void run() {
                LayerDrawable ld = (LayerDrawable) getResources().getDrawable(R.drawable.bottom_sheet_background);
                int widthPixels = findViewById(R.id.bottom_sheet_banner).getMeasuredWidth();
                int right = (int)(widthPixels * (1 - section.list_id/(double)questions_count));
                ld.setLayerInset(1,0,0,right,0);
                findViewById(R.id.bottom_sheet_banner).setBackground(ld);
            }
        });

        for (Map.Entry<String, String> entry : answer_list.entrySet()) {
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

    /**
     * 真的显示正在做的下一题
     *
     * @param section section
     */
    private void updateDisplayQuestion(TikuDisplaySection section) {
        this.section = section;
        UserInfo info;
        info = qb.getInfo(qb.getUserId(), qb_name);
        String progress_text = "当前 " + info.progress + "/" + info.count;
        updateDisplayQuestionAnswer(
                section.question.answer_type,
                section.question.question,
                "提交",
                section.question.answer,
                progress_text,
                true
        );
        view_id = info.progress;
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
            case R.id.last_question_text:
                showLastQuestion();
                break;
            case R.id.next_question_text:
                showNextQuestion();
                break;
        }
    }

    public void showNextQuestion() {
        if (next_question_text.getText().toString().equals("下一题")) {
            int list_id = view_id;
            TikuDisplaySection doing_section = qb.next(qb.getUserId(), qb_name, shuffle);
            if (list_id < doing_section.list_id - 1) {
                SharedPreferences p = getSharedPreferences("qb_cache_" + qb_name, Context.MODE_PRIVATE);
                String json = p.getString(Integer.toString(list_id + 1), "");
                if (json.equals(""))
                    Snackbar.make(findViewById(R.id.doExamCoordinatorLayout), "内部出错啦！记得反馈题号题库名称！", Snackbar.LENGTH_LONG).show();
                else {
                    try {
                        Gson gson = new Gson();
                        QBCacheSection section = gson.fromJson(json, QBCacheSection.class);
                        updateLastDisplayQuestion(section);
                    } catch (JsonSyntaxException e) {
                        Snackbar.make(findViewById(R.id.doExamCoordinatorLayout), "出错啦！记得反馈一下此问题哦！", Snackbar.LENGTH_LONG).show();
                        last_error = e.getMessage() + "\n缓存的json：" + json;
                    }
                }
            } else {
                next_question_text.setText("反馈");
                updateDisplayQuestion(section);
            }
        }
    }

    public void showLastQuestion() {
        int list_id = view_id;
        if (list_id == 0) {
            Snackbar.make(findViewById(R.id.doExamCoordinatorLayout), "没有上一题啦！", Snackbar.LENGTH_SHORT).show();
        } else {

            SharedPreferences p = getSharedPreferences("qb_cache_" + qb_name, Context.MODE_PRIVATE);
            String json = p.getString(Integer.toString(list_id - 1), "");
            Log.e("last_question", json);
            if (json.equals("")) {
                QBSection section = qb.getQBData(qb.getUserId(), qb_name);
                section.current_ans = list_id - 1;
                section.commitChange(qb.getDB());
                this.section = qb.next(user_id, qb_name, shuffle);
                updateDisplayQuestion(this.section);
            } else {
                try {
                    Gson gson = new Gson();
                    QBCacheSection section = gson.fromJson(json, QBCacheSection.class);
                    submit_btn.setText("回到当前");
                    next_question_text.setText("下一题");
                    updateLastDisplayQuestion(section);
                } catch (Exception e) {
                    Snackbar.make(findViewById(R.id.doExamCoordinatorLayout), "出错啦！记得反馈一下此问题哦！", Snackbar.LENGTH_LONG).show();
                    last_error = e.getMessage() + "\n缓存的json：" + json;
                }
            }
        }
    }

    public void onSubmit() {
        if (submit_btn.getText().equals("提交")) {
            StringBuilder answer = new StringBuilder();
            for (Map.Entry<String, Boolean> entry : key_down.entrySet()) {
                if (entry.getValue()) answer.append(entry.getKey());
            }
            if (answer.toString().equals("")) {
                Snackbar.make(findViewById(R.id.doExamCoordinatorLayout), "请选择至少一个选项", Snackbar.LENGTH_SHORT).show();
                return;
            }
            String current_question = section.question.question;
            Map<String, String> current_ans_list = section.question.answer;
            int type = section.question.answer_type;
            JudgeResult result = qb.judge(user_id, answer.toString());
            //将题目写入缓存，以便看错题本
            setQBCache(qb_name, result.list_id, type, current_question, answer.toString(), result.right_answer, current_ans_list);
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

            ((AnswerSheetAdapter)answer_sheet.getAdapter()).setItem(section.list_id,result.status?2:1);

            if(result.is_end) {
                ZMUtil.showDialog(this, result.res_message.get("title"), result.res_message.get("content"), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAfterTransition();
                    }
                });
                //完成本轮题目后清除做题缓存。
                qb.getQBCacheEditor(qb_name).clear().apply();
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
                                updateDisplayQuestion(qb.next(user_id, qb_name, shuffle));
                            }
                        });
                    }
                };
                updateResourceTimer.schedule(mTimerTask, 500);
                //updateResourceTimer.cancel();
            }
        } else if (submit_btn.getText().equals("下一题") || submit_btn.getText().equals("回到当前")) {
            updateDisplayQuestion(qb.next(user_id, qb_name, shuffle));
            next_question_text.setText("反馈");
        }
    }

    private void setQBCache(String qb_name, int progress, int answer_type, String question, String user_choice, String real_choice, Map<String, String> key) {
        SharedPreferences.Editor editor = qb.getQBCacheEditor(qb_name);
        QBCacheSection cache = new QBCacheSection();
        cache.id = progress;
        cache.answer_type = answer_type;
        cache.question = question;
        cache.user_choice = user_choice;
        cache.real_choice = real_choice;
        cache.key = key;
        Gson gson = new Gson();
        String json = gson.toJson(cache);
        editor.putString(Integer.toString(progress), json);
        editor.apply();
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

    class AnswerSheetAdapter extends RecyclerView.Adapter<AnswerSheetAdapter.ViewHolder> {

        private int[] mData;
        private LayoutInflater mInflater;

        AnswerSheetAdapter(Context context, int[] data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.answer_sheet_item, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the TextView in each cell
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.myTextView.setText(String.valueOf(position+1));
            if(mData[position] == 1){ // 错误
                holder.myTextView.setTextColor(getResources().getColor(R.color.white));
                holder.myTextView.setBackground(getDrawable(R.drawable.circle_red));
            }else if(mData[position] == 2){ // 正确
                holder.myTextView.setTextColor(getResources().getColor(R.color.white));
                holder.myTextView.setBackground(getDrawable(R.drawable.circle_green));
            }else{
                holder.myTextView.setTextColor(getResources().getColor(R.color.primary_text));
                holder.myTextView.setBackground(getDrawable(R.drawable.circle));
            }
        }

        @Override
        public int getItemCount() {
            return mData.length;
        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView myTextView;

            ViewHolder(View itemView) {
                super(itemView);
                myTextView = itemView.findViewById(R.id.info_text);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = Integer.valueOf(myTextView.getText().toString()) - 1;
                if(mData[position] != 0){
                    SharedPreferences p = getSharedPreferences("qb_cache_" + qb_name, Context.MODE_PRIVATE);
                    String json = p.getString(Integer.toString(position), "");
                    if (json.equals(""))
                        Snackbar.make(findViewById(R.id.doExamCoordinatorLayout), "内部出错啦！记得反馈题号题库名称！", Snackbar.LENGTH_LONG).show();
                    else {
                        try {
                            Gson gson = new Gson();
                            QBCacheSection section = gson.fromJson(json, QBCacheSection.class);
                            submit_btn.setText("回到当前");
                            next_question_text.setText("下一题");
                            updateLastDisplayQuestion(section);
                        } catch (JsonSyntaxException e) {
                            Snackbar.make(findViewById(R.id.doExamCoordinatorLayout), "出错啦！记得反馈一下此问题哦！", Snackbar.LENGTH_LONG).show();
                            last_error = e.getMessage() + "\n缓存的json：" + json;
                        }
                    }
                    BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_layout);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        }

        int getItem(int position) {
            return mData[position];
        }
        void setItem(int position,int value){
            mData[position] = value;
            this.notifyItemChanged(position);
        }
    }

    private void showNext(String user_id, String qb_name, boolean shuffle) {
        section = qb.next(user_id, qb_name, shuffle);
        updateDisplayQuestion(section);
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
