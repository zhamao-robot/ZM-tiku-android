package xin.zhamao.zm_tiku.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import xin.zhamao.zm_tiku.object.JudgeResult;
import xin.zhamao.zm_tiku.object.QBSection;
import xin.zhamao.zm_tiku.object.TikuDisplaySection;
import xin.zhamao.zm_tiku.object.TikuMeta;
import xin.zhamao.zm_tiku.object.TikuSection;
import xin.zhamao.zm_tiku.object.UserInfo;
import xin.zhamao.zm_tiku.value.StatusCode;

public class QB {

    private final Context context;
    private DBHelper db;

    public QB(Context context) {
        this.context = context;
        this.db = new DBHelper(context);
    }

    public DBHelper getDB() {
        return this.db;
    }

    public String getUserId() {
        return "7cf10d37-ee8d-437b-b2a2-7b6a4c97ab5a";
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

    public Map<String, TikuSection> getTikuData(TikuMeta qb_meta) {
        Map<String, TikuSection> list = new LinkedHashMap<>();
        Gson gson = new Gson();
        String tiku_data = FileSystem.loadInternalFile(context,  qb_meta.tiku_file);
        System.out.println("Getting " + qb_meta.tiku_file);
        //System.out.print(tiku_data);
        JsonObject obj = (JsonObject) (new JsonParser()).parse(tiku_data);
        for (Map.Entry<String, JsonElement> map : obj.entrySet()) {
            JsonElement element = map.getValue();
            TikuSection a = gson.fromJson(element, TikuSection.class);
            list.put(map.getKey(), a);
        }
        return list;
    }

    public QBSection getQBData(String user_id, String qb_name) {
        List<QBSection> r = db.queryQB("SELECT * FROM qb WHERE user_id = ? AND qb_name = ?", new String[]{user_id, qb_name});
        if (!r.isEmpty())
            return r.get(0);
        else return null;
    }

    public void insertQBData(String user_id, String qb_name) {
        db.queryQB("INSERT INTO qb VALUES(?,?,?,?,?,?,?,?,?)", new String[]{
                user_id,
                qb_name,
                "0",
                "[]",
                "[]",
                "0",
                "0",
                "0",
                "0"
        });
    }

    private List<Integer> generateDoingList(TikuMeta meta, String rules) {
        return generateDoingList(meta, rules, null);
    }

    private List<Integer> generateDoingList(TikuMeta meta, String rules, String user_id) {
        Map<String, TikuSection> qb = getTikuData(meta);
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
                QBSection userdata = getQBData(user_id, meta.name);
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
            case "模拟考场":
            case "simulate":
                List<Integer> single = new ArrayList<>();
                List<Integer> multi = new ArrayList<>();
                for(Map.Entry<String, TikuSection> entry : qb.entrySet()) {
                    if(entry.getValue().answer_type == 0) {
                        single.add(Integer.parseInt(entry.getKey()));
                    } else if(entry.getValue().answer_type == 1) {
                        multi.add(Integer.parseInt(entry.getKey()));
                    }
                }
                Collections.shuffle(single);
                Collections.shuffle(multi);
                if (meta.name.equals("maogai2")) {
                    single = single.subList(0, 10);
                } else {
                    single = single.subList(0, 60);
                }
                multi = multi.subList(0, 20);
                single.addAll(multi);
                ls = single;
                break;
            //TODO: 多选随机，单选随机，高频，跳转
        }
        return ls;
    }

    public String getQuestionTypeCH(int id) {
        List<String> ls = Arrays.asList("单选题", "多选题", "填空题");
        try {
            return ls.get(id);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public String getQuestionTypeCH(TikuSection q) {
        return getQuestionTypeCH(q.answer_type);
    }

    /**
     * @param qb_meta     题库名称
     * @param doing_list  正在做的列表
     * @param current_ans 当前的答案
     * @param shuffle     是否打乱选项顺序
     * @return TikuSection
     */
    private TikuSection getQuestion(TikuMeta qb_meta, List<Integer> doing_list, int current_ans, boolean shuffle) {
        try {
            Map<String, TikuSection> qb = getTikuData(qb_meta);
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
            //Log.e("QB", "getQuestion获取Map错误！");
            return new TikuSection();
        }
    }

    public void pass() {
    }

    public List<String> getShuffleList(String user_id) {
        Map<String, String> user = getDB().getUserData(user_id);
        if (user.isEmpty()) return new ArrayList<>();
        if (user.containsKey("qb_shuffle")) {
            if(Objects.equals(user.get("qb_shuffle"), "")) return new ArrayList<>();
            return (new Gson()).fromJson(user.get("qb_shuffle"), new TypeToken<List<String>>() {}.getType());
        }
        return new ArrayList<>();
    }

    public UserInfo getInfo(String user_id, TikuMeta meta) {
        UserInfo res = new UserInfo();
        List<QBSection> r = db.queryQB("SELECT * FROM qb WHERE user_id = ? AND qb_name = ?", new String[]{user_id, meta.name});

        if (r.isEmpty()) {
            res.status = 0;
            res.mode = 0;
            res.shuffle = false;
            res.count = getTikuData(meta).size();
            res.progress = 0;
        } else {
            QBSection rs = r.get(0);
            Map<String, String> user = db.getUserData(user_id);
            res.status = 1;
            res.mode = rs.qb_mode;
            if (user.isEmpty()) res.shuffle = false;
            String shuffle = user.get("qb_shuffle");
            if (shuffle != null && !Objects.equals(shuffle, "")) res.shuffle = true;
            res.count = rs.doing_list.size() == 0 ? getTikuData(meta).size() : rs.doing_list.size();
            res.progress = rs.current_ans;
        }
        return res;
    }

    ////////////// non internal API part

    public JudgeResult judge(String user_id, String answer) {
        List<QBSection> section_list = db.queryQB("SELECT * FROM qb WHERE user_id = ? AND doing = 1", new String[]{user_id});
        if (section_list.isEmpty()) return null;
        QBSection section = section_list.get(0);
        TikuMeta judgeMeta = TikuManager.findMetaByName(section.qb_name);
        TikuSection current_question = getQuestion(judgeMeta, section.doing_list, section.current_ans, false);
        answer = getTrueAnswer(answer.trim(), current_question.answer_type);
        JudgeResult r = section.judge(this, current_question, answer);
        Log.i("QB", "Android判题：" + user_id + "(" + section.qb_name + ") " + (r.status ? "正确" : "错误"));
        r.is_end = false;
        boolean shuffle = !getShuffleList(user_id).isEmpty();
        int next = section.current_ans + 1;

        if (next < section.doing_list.size()) {
            section.current_ans = next;
            TikuSection question = getQuestion(judgeMeta, section.doing_list, next, shuffle);

            if (shuffle) db.setUserShuffle(user_id, question.shuffle);

            TikuDisplaySection res_next = new TikuDisplaySection();
            res_next.question = question;
            res_next.id = section.doing_list.get(next);
            res_next.type = getQuestionTypeCH(question);
            r.next = res_next;
        } else {
            Map<String, String> res_message = new LinkedHashMap<>();
            res_message.put("title", "你已经做完了本轮题目啦！");
            List<Integer> doing_list = generateDoingList(judgeMeta, "normal");
            String msg = "";
            if (section.qb_mode != 3) {
                Integer right = section.right_count, total = section.answer_count;
                double percent = (right.doubleValue() / total.doubleValue()) * 100.0;
                msg = msg + "正确题数：" + right + "，总共题数：" + total + "\n正确率：" + percent + "%";
                if (right.equals(total)) {
                    msg += "\n恭喜你，已经做对了所有题目！";
                    r.is_end = true;
                } else {
                    msg += "\n上一轮有错题哦，已将你的题库设置为你上一轮的错题集，点击下面的按钮进行下一轮";
                    doing_list = section.wrong;
                    r.is_end = true;
                }
            } else {
                msg = "点击按钮进行返回";
                r.is_end = true;
            }
            res_message.put("content", msg);
            r.res_message = res_message;
            section.doing = 0;
            section.current_ans = 0;
            section.doing_list = doing_list;
            section.answer_count = 0;
            section.right_count = 0;
            section.qb_mode = 0;
            section.wrong = new ArrayList<>();
        }
        r.right_answer = current_question.key;
        section.commitChange(db);
        return r;
    }

    public TikuDisplaySection next(String user_id, TikuMeta qb_meta, boolean shuffle) {
        QBSection status = getQBData(user_id, qb_meta.name);
        if(status == null) insertQBData(user_id, qb_meta.name);
        status = getQBData(user_id, qb_meta.name);
        if(status == null) return null;
        db.queryQB("UPDATE qb SET doing = 0 WHERE user_id = ?", new String[]{user_id});
        if(status.doing_list.isEmpty()) {
            status.doing_list = generateDoingList(qb_meta, "normal");
        }
        status.doing = 1;
        int current_ans = status.current_ans;
        TikuSection question = getQuestion(qb_meta, status.doing_list, current_ans, shuffle);
        if(shuffle)
            db.setUserShuffle(user_id, question.shuffle);
        else if (!getShuffleList(user_id).isEmpty())
            db.setUserShuffle(user_id, new ArrayList<String>());

        TikuDisplaySection res = new TikuDisplaySection();
        res.list_id = current_ans;
        res.question = question;
        res.id = status.doing_list.get(current_ans);
        res.type = getQuestionTypeCH(question);
        status.commitChange(db);
        return res;
    }

    public TikuDisplaySection changeMode(String user_id, TikuMeta qb_meta, int qb_mode, boolean shuffle) {
        if(qb_mode > 7) qb_mode = 0;
        TikuDisplaySection res = new TikuDisplaySection();
        String qb_mode_name = convertModeName(qb_mode);
        if(qb_mode_name == null) return null;
        List<Integer> doing_list = generateDoingList(qb_meta, qb_mode_name, user_id);
        if(doing_list.isEmpty()) {
            res.warning = StatusCode.no_wrong_question;
            return res;
        }
        QBSection section = new QBSection(this, user_id, qb_meta.name);
        section.doing_list = doing_list;
        section.doing = 1;
        section.current_ans = 0;
        section.answer_count = 0;
        section.right_count = 0;
        section.qb_mode = qb_mode;
        section.commitChange();
        TikuSection question = getQuestion(qb_meta, doing_list, 0, shuffle);
        if(shuffle)
            getDB().setUserShuffle(user_id, question.shuffle);
        else if (getShuffleList(user_id).isEmpty())
            getDB().setUserShuffle(user_id, new ArrayList<String>());
        res.mode = qb_mode_name;
        res.question = question;
        res.id = doing_list.get(0);
        res.type = getQuestionTypeCH(question);
        getQBCacheEditor(qb_meta.name).clear().apply();
        return res;
    }

    private String convertModeName(int qb_mode) {
        List<String> ls = Arrays.asList("normal", "单选", "多选", "错题", "随机", "模拟考场", "单选随机", "多选随机");
        try {
            return ls.get(qb_mode);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public SharedPreferences.Editor getQBCacheEditor(String qb_name) {
        return context.getSharedPreferences("qb_cache_" + qb_name, Context.MODE_PRIVATE).edit();
    }
}