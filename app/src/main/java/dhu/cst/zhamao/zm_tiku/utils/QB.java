package dhu.cst.zhamao.zm_tiku.utils;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class QB {

    private final Context context;

    public QB(Context context) {
        this.context = context;

    }

    /**
     * 获取题库中文/英文名字
     *
     * @param name 题库名字
     * @return String|null
     */
    public static String getTikuName(String name) {
        switch (name) {
            case "history":
                return "近代史题库";
            case "politics":
                return "思修题库";
            case "maogai":
                return "毛概题库";
            case "makesi":
                return "马克思题库";
            case "test":
                return "测试题库";
            case "测试题库":
                return "test";
            case "近代史题库":
            case "近代史":
                return "history";
            case "思修题库":
            case "思修":
                return "politics";
            case "毛概题库":
            case "毛概":
                return "maogai";
            case "马克思题库":
            case "马克思":
                return "makesi";
            default:
                return null;
        }
    }

    /**
     * 转换字符串"BAdC" 为 "ABCD"
     *
     * @param str         答案字符串
     * @param answer_type answer_type
     * @return String
     */
    public String getTrueAnswer(String str, int answer_type) {
        if (answer_type == 2) return str.trim();
        char[] char_arr = str.toCharArray();
        Arrays.sort(char_arr);
        String sorted = new String(char_arr);
        return sorted.toUpperCase();
    }

    public Map<String, TikuSection> getTikuData(String qb_name) {
        Map<String, TikuSection> list = new LinkedHashMap<>();
        Gson gson = new Gson();
        String tiku_data = ZMUtil.loadResource(context, "tiku/" + qb_name + ".json");
        JsonObject obj = (JsonObject) (new JsonParser()).parse(tiku_data);
        for (Map.Entry<String, JsonElement> map : obj.entrySet()) {
            JsonElement element = map.getValue();
            TikuSection a = gson.fromJson(element, TikuSection.class);
            list.put(map.getKey(), a);
        }
        return list;
    }

    public QBSection getQBData(String user_id, String qb_name) {
        //TODO: 从数据库中读取用户做题进度
        return new QBSection();
    }

    public List<Integer> generateDoingList(String qb_name, String rules) {
        return generateDoingList(qb_name, rules, null);
    }

    public List<Integer> generateDoingList(String qb_name, String rules, String user_id) {
        Map<String, TikuSection> qb = getTikuData(qb_name);
        List<Integer> ls = new ArrayList<>();
        switch (rules) {
            case "normal":
                for (Map.Entry<String, TikuSection> entry : qb.entrySet()) {
                    ls.add(Integer.parseInt(entry.getKey()));
                }
                break;
            case "wrong":
            case "错题":
                if (user_id == null) break;
                QBSection userdata = getQBData(user_id, qb_name);
                if (userdata == null) break;
                ls = userdata.getWrong();
                break;
            case "random":
            case "随机":
                for (Map.Entry<String, TikuSection> entry : qb.entrySet()) {
                    ls.add(Integer.parseInt(entry.getKey()));
                }
                Collections.shuffle(ls);
                break;
            case "single":
            case "单选":
                for (Map.Entry<String, TikuSection> entry : qb.entrySet()) {
                    if (entry.getValue().answer_type == 0) {
                        ls.add(Integer.parseInt(entry.getKey()));
                    }
                }
                break;
            case "multi":
            case "多选":
                for (Map.Entry<String, TikuSection> entry : qb.entrySet()) {
                    if (entry.getValue().answer_type == 1) {
                        ls.add(Integer.parseInt(entry.getKey()));
                    }
                }
                break;
            //TODO: 多选随机，单选随机，高频，跳转
        }
        return ls;
    }

    public void setDoingList(String user_id, String qb_name, List<Integer> list) {
        //TODO: 更新数据库的doing列表
    }

    public void setWrongList(String user_id, String qb_name, List<Integer> list) {
        //TODO: 更新数据库的wrong列表
    }

    public void setDoing(String user_id, String qb_name, boolean status) {
        //TODO: 更新数据库的doing选项
    }

    public String getQuestionTypeCH(TikuSection q) {
        List<String> ls = Arrays.asList("单选题", "多选题", "填空题");
        try {
            return ls.get(q.answer_type);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * @param qb_name 题库名称
     * @param doing_list 正在做的列表
     * @param current_ans 当前的答案
     * @param shuffle 是否打乱选项顺序
     * @return TikuSection|null
     */
    public TikuSection getQuestion(String qb_name, List<Integer> doing_list, int current_ans, boolean shuffle) {
        try {
            Map<String, TikuSection> qb = getTikuData(qb_name);
            int t = doing_list.get(current_ans);
            TikuSection section = qb.get(Integer.toString(t));
            if (shuffle) { //打乱选项
                if (section != null && section.answer_type != 2) {
                    List<String> ls = new ArrayList<>();
                    for (Map.Entry<String, String> entry : section.answer.entrySet()) {
                        ls.add(entry.getKey());
                    }
                    // List<String> new_ans = new ArrayList<>();
                    Map<String, String> new_ans = new LinkedHashMap<>();
                    List<String> origin = new ArrayList<>(ls);
                    Collections.shuffle(ls);
                    int cnt = 0;
                    for (int i = 0; i < ls.size(); ++i) {
                        new_ans.put(origin.get(cnt++), section.answer.get(ls.get(i)));
                    }
                    section.answer = new_ans;
                    section.shuffle = ls;
                }
            }
            return section;
        } catch (NullPointerException e) {
            Log.e("QB", "getQuestion获取Map错误！");
            return null;
        }
    }

    public void setCurrentAns(String user_id, String qb_name, int num) {
        //TODO: 设置当前的ans
    }
}