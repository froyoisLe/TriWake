package site.isleti.triwake.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.LinkedList;
import site.isleti.triwake.model.NoteBean;

public class NoteDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "NoteDBHelper";
    private final Context mContext;
    private static final String DB_NAME = "Notes";
    private static final String CREATE_NOTE = "create table Notes("
            + "id integer primary key autoincrement, "
            + "date text, "
            + "time text, "
            + "uuid text, "
            + "tag integer, "
            + "content text)";


    public NoteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Notes");
        onCreate(db);
    }

    public void addNotes(NoteBean noteBean){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uuid",noteBean.getUuid());
        values.put("content",noteBean.getContent());
        values.put("date",noteBean.getDate());
        values.put("time",noteBean.getTime());
        values.put("tag", noteBean.getTag());
        db.insert(DB_NAME,null,values);
        values.clear();
    }

    public void  removeNotes(String uuid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_NAME,"uuid = ?",new String[]{uuid});
    }

    public LinkedList<NoteBean> getNotesList(){
        LinkedList<NoteBean> notes = new LinkedList<>();
        SQLiteDatabase SQdb = this.getWritableDatabase();
        //Cursor cursor = SQdb.query("Notes",null,null,null,null,null,null);
        Cursor cursor = SQdb.rawQuery("select DISTINCT * from  Notes order by tag desc",null);
        if (cursor.moveToFirst()){
            do{
                String uuid = cursor.getString(cursor.getColumnIndex("uuid"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                long tag = cursor.getLong(cursor.getColumnIndex("tag"));
                NoteBean noteBean = new NoteBean(date,content,time);
                noteBean.setUuid(uuid);
                noteBean.setContent(content);
                noteBean.setDate(date);
                noteBean.setTime(time);
                noteBean.setTag(tag);
                notes.add(noteBean);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }


    public void updateNote(String uuid, NoteBean noteBean){
        removeNotes(uuid);
        noteBean.setUuid(uuid);
        addNotes(noteBean);
    }
}


