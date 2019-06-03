package site.isleti.triwake.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.LinkedList;
import site.isleti.triwake.model.TodoBean;

public class TodoDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "TodoDBHelper";
    private final Context mContext;
    private static final String DB_NAME = "Todo";
    private static final String CREATE_TODO = "create table Todo("
            + "id integer primary key autoincrement, "
            + "date text, "
            + "time text, "
            + "uuid text, "
            + "state integer,"
            + "tag integer, "
            + "content text)";


    public TodoDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Todo");
        onCreate(db);
    }

    public void addTodo(TodoBean todoBean){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uuid",todoBean.getUuid());
        values.put("content",todoBean.getContent());
        values.put("date",todoBean.getDate());
        values.put("time",todoBean.getTime());
        values.put("tag", todoBean.getTag());
        values.put("state", todoBean.getState());
        db.insert(DB_NAME,null,values);
        values.clear();
    }

    public LinkedList<TodoBean> getTodoList(){
        LinkedList<TodoBean> todoBeans = new LinkedList<>();
        SQLiteDatabase SQdb = this.getWritableDatabase();
        //根据创建时间降序显示
        Cursor cursor = SQdb.rawQuery("select DISTINCT * from Todo order by tag desc",null);
        if (cursor.moveToFirst()){
            do{
                String uuid = cursor.getString(cursor.getColumnIndex("uuid"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                long tag = cursor.getLong(cursor.getColumnIndex("tag"));
                int state = cursor.getInt(cursor.getColumnIndex("state"));
                TodoBean todoBean = new TodoBean(date,content,time,state,tag);
                todoBean.setUuid(uuid);
                todoBeans.add(todoBean);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return todoBeans;
    }

    public void  removeTodo(String uuid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Todo","uuid = ?",new String[]{uuid});
    }

    public void updateTodo(String uuid, TodoBean todoBean){
        removeTodo(uuid);
        todoBean.setUuid(uuid);
        addTodo(todoBean);
    }

}


