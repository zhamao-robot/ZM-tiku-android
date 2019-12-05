package dhu.cst.zhamao.zm_tiku.object;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class QBSection {
    public String user_id;
    public String qb_name;
    public int doing;
    public List<Integer> wrong;
    public List<Integer> doing_list;
    public int current_ans;
    public int answer_count;
    public int right_count;
    public int qb_mode;

    public List<Integer> getDoingList() {
        return doing_list;
    }

    public List<Integer> getWrong(){
        return wrong;
    }
}
