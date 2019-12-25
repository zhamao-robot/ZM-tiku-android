package xin.zhamao.zm_tiku.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import xin.zhamao.zm_tiku.object.QBSection;

public class DBHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "qb";
    private SQLiteDatabase db = null;
    private Context context;

    DBHelper(Context context) {
        super(context, DB_NAME, null, 3);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_NAME + "(" +
                "`user_id` varchar(128) DEFAULT NULL, " +       //0
                "  `qb_name` varchar(25) DEFAULT NULL," +       //1
                "  `doing` int(11) DEFAULT NULL," +             //2
                "  `wrong` varchar(4096) DEFAULT NULL," +       //3
                "  `doing_list` varchar(4096) DEFAULT NULL," +  //4
                "  `current_ans` int(11) DEFAULT NULL," +       //5
                "  `answer_count` int(11) DEFAULT NULL," +      //6
                "  `right_count` int(11) DEFAULT NULL," +       //7
                "  `qb_mode` int(11) DEFAULT NULL)");           //8
        db.execSQL("CREATE TABLE user_data (" +
                "`id` varchar(64) primary key, " +
                "`user_name` varchar(256), " +
                "`qb_shuffle` varchar(256), " +
                "`user_type` varchar(256))");
        db.rawQuery("INSERT INTO user_data VALUES (?,?,?,?)", new String[]{
                "7cf10d37-ee8d-437b-b2a2-7b6a4c97ab5a",
                "本地用户",
                "",
                "0"
        }).close();
        //Toast.makeText(context, "创建数据库中", Toast.LENGTH_SHORT).show();
    }

    SQLiteDatabase get() {
        return getWritableDatabase();
    }

    public List<QBSection> queryQB(String sql, String[] param) {
        if (db == null) db = getWritableDatabase();
        Cursor result = db.rawQuery(sql, param);
        result.moveToFirst();
        List<QBSection> list = new ArrayList<>();
        while (!result.isAfterLast()) {
            QBSection section = new QBSection();
            section.user_id = result.getString(0);
            section.qb_name = result.getString(1);
            section.doing = result.getInt(2);
            List<Integer> wrong = (new Gson()).fromJson(result.getString(3), new TypeToken<List<Integer>>() {
            }.getType());
            List<Integer> doing_list = (new Gson()).fromJson(result.getString(4), new TypeToken<List<Integer>>() {
            }.getType());
            section.wrong = wrong;
            section.doing_list = doing_list;
            section.current_ans = result.getInt(5);
            section.answer_count = result.getInt(6);
            section.right_count = result.getInt(7);
            section.qb_mode = result.getInt(8);
            list.add(section);
            // move to next
            result.moveToNext();
        }
        result.close();
        return list;
    }

    Map<String, String> getUserData(String user_id) {
        if (db == null) db = getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM user_data WHERE id = ?", new String[]{user_id});
        result.moveToFirst();
        if (!result.isAfterLast()) {
            Map<String, String> section = new LinkedHashMap<>();
            section.put("id", result.getString(0));
            section.put("user_name", result.getString(1));
            section.put("qb_shuffle", result.getString(2));
            section.put("user_type", result.getString(3));
            result.close();
            return section;
        } else {
            result.close();
            return new LinkedHashMap<>();
        }
    }

    void setUserShuffle(String user_id, List<String> shuffle) {
        if (db == null) db = getWritableDatabase();
        queryQB("UPDATE user_data SET qb_shuffle = ? WHERE id = ?", new String[]{(new Gson()).toJson(shuffle), user_id});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
