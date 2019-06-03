package site.isleti.triwake.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/***
*日记数据库创建及增删查改
*@author islet
*@time 2019/5/9 0009 15:14
***/
public class DiaryDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DiaryDBHelper";

    private Context mContext;
    /*** 建表语句 CREATE_DBName ***@author：IsLeti***/
    private static final String CREATE_DIARY = "create table Diary("
            + "id integer primary key autoincrement, "
            + "date text, "
            + "title text, "
            + "tag text, "
            + "content text)";

    /*** 初始化DBHelper ***@author：IsLeti***/
    public DiaryDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DIARY); //执行建表语句
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Diary");
        onCreate(db);
    }

    public void  removeDiary(String tag){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Diary","tag = ?",new String[]{tag});
        Log.d(TAG, "removeDiary: 删除了" + tag);
    }
}
