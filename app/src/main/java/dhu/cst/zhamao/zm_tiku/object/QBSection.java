package dhu.cst.zhamao.zm_tiku.object;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dhu.cst.zhamao.zm_tiku.utils.DBHelper;
import dhu.cst.zhamao.zm_tiku.utils.QB;
import dhu.cst.zhamao.zm_tiku.utils.ZMUtil;

public class QBSection {
    private boolean is_valid = false;
    public String user_id;
    public String qb_name;
    public int doing;
    public List<Integer> wrong;
    public List<Integer> doing_list;
    public int current_ans;
    public int answer_count;
    public int right_count;
    public int qb_mode;
    private QB qb = null;

    public List<Integer> getDoingList() {
        return doing_list;
    }

    public QBSection(){}

    public QBSection(QB qb, String user_id, String qb_name) {
        List<QBSection> p = qb.getDB().queryQB("SELECT * FROM qb WHERE user_id = ? AND qb_name = ?", new String[]{user_id, qb_name});
        if(p.isEmpty()) is_valid = false;
        else {
            QBSection section = p.get(0);
            this.user_id = user_id;
            this.qb_name = qb_name;
            this.doing = section.doing;
            this.wrong = section.wrong;
            this.doing_list = section.doing_list;
            this.current_ans = section.current_ans;
            this.answer_count = section.answer_count;
            this.right_count = section.right_count;
            this.qb_mode = section.qb_mode;
            this.qb = qb;
            is_valid = true;
        }
    }

    public List<Integer> getWrong(){
        return wrong;
    }

    public boolean commitChange(){
        if(qb != null) return commitChange(qb.getDB());
        else return false;
    }

    public boolean commitChange(DBHelper helper){
        helper.queryQB("UPDATE qb SET doing = ?, wrong = ?, doing_list = ?, current_ans = ?, answer_count = ?, right_count = ?, qb_mode = ? WHERE user_id = ? AND qb_name = ?", new String[]{
                Integer.toString(doing),
                (new Gson()).toJson(wrong),
                (new Gson()).toJson(doing_list),
                Integer.toString(current_ans),
                Integer.toString(answer_count),
                Integer.toString(right_count),
                Integer.toString(qb_mode),
                user_id,
                qb_name
        });
        return true;
    }

    public JudgeResult judge(QB qb, TikuSection question, String answer) {
        String da_an = question.key;
        List<String> shuffle_list = qb.getShuffleList(user_id);
        Map<String, String> oj = new LinkedHashMap<>();
        if (shuffle_list.size() != 0) {
            String s_tmp = qb.getTrueAnswer(ZMUtil.implode("", shuffle_list), 0);
            String[] s_tmp2 = s_tmp.split("");
            for (String v : s_tmp2) {
                if(v.equals("")) continue;
                oj.put(v, shuffle_list.remove(0));
            }
            StringBuilder a = new StringBuilder();
            for (String v : answer.split("")) {
                if(v.equals("")) continue;
                a.append(oj.get(v));
            }
            answer = qb.getTrueAnswer(a.toString(), 0);//用户的答案（转换为原始答案）
        }
        JudgeResult result = new JudgeResult();
        result.status = answer.equals(da_an);//返回题目对错情况
        result.id = doing_list.get(current_ans);
        shuffle_list = qb.getShuffleList(user_id);
        if (shuffle_list.size() != 0) {
            String origin_key = question.key;
            String s_tmp = qb.getTrueAnswer(ZMUtil.implode("", shuffle_list), 0);
            String[] s_tmp2 = s_tmp.split("");
            String[] origin_key2 = origin_key.split("");
            StringBuilder question_key = new StringBuilder();
            for (String v : origin_key2) {
                if(v.equals("")) continue;
                for(Map.Entry<String, String> entry : oj.entrySet()) {
                    if(entry.getValue().equals(v)) question_key.append(entry.getKey());
                }
            }

            question.key = qb.getTrueAnswer(question_key.toString(), 0);
        }
        result.right_answer = question.key;
        if (qb_mode != 3) {
            ++answer_count;
            if(result.status)
                ++right_count;
            else if(!wrong.contains(doing_list.get(current_ans)))
                wrong.add(doing_list.get(current_ans));
        }
        return result;
    }
}
