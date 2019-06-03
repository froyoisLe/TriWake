package site.isleti.triwake.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.LinkedList;
import site.isleti.triwake.model.RecordBean;
/***
*记账数据库创建及操作管理
*@author islet
*@time 2019/5/9 0009 15:22
***/
public class RecordDBHelper extends SQLiteOpenHelper {

    private String TAG ="RecordDBHelper";
    private Context mContext;

    public static final String DB_NAME = "Record";  //表名Record
    /***定义建表语句CREATE_RECORD_DB ***@author：IsLeti***/
    private static final String CREATE_RECORD_DB = "create table Record ("
        + "id integer primary key autoincrement, "
        + "uuid text, "
        + "type integer, "
        + "category, "
        + "remark text, "
        + "amount double, "
        + "time integer, "
        + "date date )";
    private Cursor mCursor;

    public RecordDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECORD_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    /*** 新增账目的实现 ***@author：IsLeti***/
    public void addRecord(RecordBean bean){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uuid",bean.getUuid());
        values.put("type",bean.getType());
        values.put("category",bean.getCategory());
        values.put("remark",bean.getRemark());
        values.put("amount",bean.getAmount());
        values.put("date",bean.getDate());
        values.put("time",bean.getTimeStamp());
        db.insert(DB_NAME,null,values);//插入数据
        values.clear();                             //清除ContentValues对象携带参数
    }

    /*** 删除账目 通过获取uuid实现***@author：IsLeti***/
    public void  removeRecord(String uuid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_NAME,"uuid = ?",new String[]{uuid});
    }

    /*** 编辑账目 通过uuid替换将新帐目插入原旧账目处实现 ***@author：IsLeti***/
    public void editRecord(String uuid,RecordBean record){
        removeRecord(uuid);
        record.setUuid(uuid);
        addRecord(record);
    }

    /*** 读取数据库信息 ***@author：IsLeti***/
    public LinkedList<RecordBean> getRecords(String dateStr){
        LinkedList<RecordBean> records = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        //*** 按日期查询显示 *** @author：IsLeti
        mCursor = db.rawQuery("select DISTINCT * from Record "
                + "where date = ? order by time asc",new String[]{dateStr});
        if (mCursor.moveToFirst()){
            do{
                String uuid = mCursor.getString(mCursor.getColumnIndex("uuid"));
                int type = mCursor.getInt(mCursor.getColumnIndex("type"));
                String category = mCursor.getString(mCursor.getColumnIndex("category"));
                String remark = mCursor.getString(mCursor.getColumnIndex("remark"));
                double amount = mCursor.getDouble(mCursor.getColumnIndex("amount"));
                String date = mCursor.getString(mCursor.getColumnIndex("date"));
                long timeStamp = mCursor.getLong(mCursor.getColumnIndex("time"));
                RecordBean record = new RecordBean();
                record.setUuid(uuid);
                record.setType(type);
                record.setCategory(category);
                record.setRemark(remark);
                record.setAmount(amount);
                record.setDate(date);
                record.setTimeStamp(timeStamp);
                records.add(record);
            }while (mCursor.moveToNext());
        }
        mCursor.close();
        return records;
    }

    /*** 获取已存在日期 ***@author：IsLeti***/
    public LinkedList<String> getAvailableDate(){
        LinkedList<String> dates = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT * from Record order by date asc",new String[]{});
        if (cursor.moveToFirst()){
            do{
                String date = cursor.getString(cursor.getColumnIndex("date"));
                if (!dates.contains(date)){
                    dates.add(date);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return dates;
    }

}
