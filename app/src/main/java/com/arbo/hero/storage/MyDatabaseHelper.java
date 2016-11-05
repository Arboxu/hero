package com.arbo.hero.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2016/7/19.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String mTableName = "mfavorites";
    public static final String CREATE_FAVOR = "create table mfavorites(" +
            "id integer primary key autoincrement," +"heroid integer UNIQUE,"+
            "favorite text NOT NULL,enname text,cnname text)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory cursor, int version) {
        super(context,name,cursor,version);
        mContext = context;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAVOR);
        Log.i("创建数据库成功",""+db.getVersion());
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists mfavorites");
        onCreate(db);
    }

    private Cursor ExecSQLForCursor(String sql, String[] selectionArgs){
        SQLiteDatabase db =getWritableDatabase();
        Log.i("ExecSQLForCursor",sql);
        return db.rawQuery(sql, selectionArgs);
    }

    //添加照片信息
    public long InsertUserfavor(int heroid,String heroname,boolean isfavorite){
        SQLiteDatabase db =getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("heroid", heroid);
        cv.put("favorite", isfavorite);
        return db.insert(mTableName, null, cv);
    }
    //查询照片信息
    public Cursor Searchfavor(int row, int heroId){
        Cursor cur = null;
        try{
            String sql = "select * from mfavorites order by heroid "+heroId;
            String[] args = {String.valueOf(row)};
            if(row>0){
                sql +=" limit ?";
            }else{
                args=null;
            }
            cur = ExecSQLForCursor(sql,args);
        }catch (Exception e) {
            cur = null;
            Log.e("Search Exception",e.getMessage());
            e.printStackTrace();
        }
        return cur;
    }
    //修改照片信息
    public int UpdateUserfavor(int id,int heroid,String title,String favorite){
        SQLiteDatabase db =getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("heroid", heroid);
        cv.put("title", title);
        cv.put("favorite", favorite);

        String[] args = {String.valueOf(id)};
        return db.update(mTableName, cv, "id=?",args);
    }
    //删除照片信息
    public int DeleteUserfavor(int id){
        SQLiteDatabase db =getWritableDatabase();
        String[] args = {String.valueOf(id)};
        return db.delete(mTableName, "id=?", args);
    }




}
