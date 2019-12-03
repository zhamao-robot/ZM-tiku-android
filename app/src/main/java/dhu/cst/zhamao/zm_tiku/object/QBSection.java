package dhu.cst.zhamao.zm_tiku.object;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class QBSection {
    public String user_id;
    public String qb_name;
    public int doing;
    public String wrong;
    public String doing_list;
    public int current_ans;
    public int answer_count;
    public int right_count;
    public int qb_mode;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getQb_name() {
        return qb_name;
    }

    public void setQb_name(String qb_name) {
        this.qb_name = qb_name;
    }

    public int getDoing() {
        return doing;
    }

    public void setDoing(int doing) {
        this.doing = doing;
    }

    public List<Integer> getWrong() {
        Gson gson = new Gson();
        return gson.fromJson(wrong, new TypeToken<List<Integer>>() {
        }.getType());
    }

    public void setWrong(String wrong) {
        this.wrong = wrong;
    }

    public List<Integer> getDoingList() {
        Gson gson = new Gson();
        return gson.fromJson(doing_list, new TypeToken<List<Integer>>() {
        }.getType());
    }

    public void setDoing_list(String doing_list) {
        this.doing_list = doing_list;
    }

    public int getCurrent_ans() {
        return current_ans;
    }

    public void setCurrent_ans(int current_ans) {
        this.current_ans = current_ans;
    }

    public int getAnswer_count() {
        return answer_count;
    }

    public void setAnswer_count(int answer_count) {
        this.answer_count = answer_count;
    }

    public int getRight_count() {
        return right_count;
    }

    public void setRight_count(int right_count) {
        this.right_count = right_count;
    }

    public int getQb_mode() {
        return qb_mode;
    }

    public void setQb_mode(int qb_mode) {
        this.qb_mode = qb_mode;
    }
}
