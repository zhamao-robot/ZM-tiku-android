package dhu.cst.zhamao.zm_tiku.object;


import java.util.Map;

public class JudgeResult {
    public boolean status;
    public int id;
    public String right_answer;

    public boolean is_end = false;
    public TikuDisplaySecion next = null;
    public String message;
    public Map<String, String> res_message;

    public JudgeResult() {
        //TODO: 判题结果对象构建
    }
}
