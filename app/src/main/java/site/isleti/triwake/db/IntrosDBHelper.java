package site.isleti.triwake.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.LinkedList;
import site.isleti.triwake.model.IntrosBean;

public class IntrosDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "IntrosDBHelper";

    private Context mContext;
    private IntrosDBHelper mIntrosDBHelper;
    private static final String DB_NAME = "Introspect";
    private static final String CREATE_INTROSPECT = "create table Introspect("
            + "id integer primary key autoincrement, "
            + "uuid text, "
            + "date text, "
            + "time text, "
            + "tag integer, "
            + "content text)";

    public IntrosDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
        mIntrosDBHelper = new IntrosDBHelper(context, IntrosDBHelper.DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_INTROSPECT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Introspect");
        onCreate(db);
    }
    /*** 添加记录 ***@author：IsLeti***/
    public void addIntro(IntrosBean introspect) {
        SQLiteDatabase db = this.getWritableDatabase();//获得数据库读取权限
        ContentValues values = new ContentValues();//使用ContentValues类存储名值对
        //通过put方法将条目携带的数据放进ContentValues对象中
        values.put("uuid", introspect.getUuid());//
        values.put("content", introspect.getContent());
        values.put("date", introspect.getDate());
        values.put("time", introspect.getTime());
        values.put("tag", introspect.getTag());
        db.insert(DB_NAME, null, values);//将values插入表中
        values.clear();//清空ContentValues对象
    }



    public LinkedList<IntrosBean> getIntrosList() {
        LinkedList<IntrosBean> intros = new LinkedList<>();
        SQLiteDatabase SQdb = this.getWritableDatabase();
        //使用Cursor的rawQuery方法执行sql查询语句：根据记录tag降序排序
        Cursor cursor = SQdb.rawQuery("select DISTINCT * from Introspect  " +
                "order by tag desc",null);
        if (cursor.moveToFirst()) {
            do {
                //获得表中每一列对应属性对应的值
                String uuid = cursor.getString(cursor.getColumnIndex("uuid"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                long tag = cursor.getLong(cursor.getColumnIndex("tag"));
                //创建新的记录存储读取出的值
                IntrosBean intro = new IntrosBean(date, content, time);
                intro.setUuid(uuid);
                intro.setTag(tag);
                intros.add(intro);
            } while (cursor.moveToNext());
        }
        cursor.close();//关闭Cursor类
        return intros;//返回所有的记录条目
    }

    public void  removeIntro(String uuid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Introspect","uuid = ?",new String[]{uuid});
    }

    public void updateIntro(String uuid, IntrosBean introsBean){
        removeIntro(uuid);
        introsBean.setUuid(uuid);
        addIntro(introsBean);
    }
}





