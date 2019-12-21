package dhu.cst.zhamao.zm_tiku.object;


import java.util.Map;

public class JudgeResult {
    public boolean status;
    public int id;
    public String right_answer;

    public boolean is_end = false;
    public TikuDisplaySection next = null;
    public Map<String, String> res_message;
    public int answer_type;
    public int list_id;

    JudgeResult() {
        //TODO: 判题结果对象构建
    }
}
