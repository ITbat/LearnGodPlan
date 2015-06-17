package com.learngodplan.db;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BasicInfoDBO extends SQLiteOpenHelper {
	private static final String DB_NAME = "basicInfoDB05.db";
    private static final String TABLE_NAME = "BasicInfo";
    private static final int DB_VERSION = 1;
    
	public BasicInfoDBO(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//注:由于有了basic_type，当basic_type为999时是没有领养宠物，其他情况都是领养了宠物
		//因此basic_has这一列已近作废，失去作用，在后面的操作都不会用到
		String sql = "CREATE TABLE" + " " + TABLE_NAME 
				          + "(" 
				          + "basic_name INTEGER PRIMARY KEY,"
				          + "basic_has INTEGER,"
				          + "basic_last TEXT,"
				          + "basic_hunger INTEGER,"
				          + "basic_type INTEGER"
				          + ")";
		db.execSQL(sql);
		
        //获取当前日期
    	Calendar ca = Calendar.getInstance();
    	int  year = ca.get(Calendar.YEAR);
    	int month = ca.get(Calendar.MONTH) + 1;
    	int day = ca.get(Calendar.DAY_OF_MONTH);
    	int hour = ca.get(Calendar.HOUR_OF_DAY);
    	
    	String str = String.valueOf(year)
    			          +"-"
    			          +String.valueOf(month)
    			          +"-"
    			          +String.valueOf(day)
    			          +"-"
    			          +String.valueOf(hour);
    	//当第一次创建数据库时,初始化一个BasicInfo条目，之后的操作均是对该条目进行更新,999表示还未领养宠物
		BasicInfo bsInfo = new BasicInfo(10086, 0, str, 100, 999);//int basicName, int has, String last, int hunger_, int type_
		
		ContentValues values = new ContentValues();
		values.put("basic_name", bsInfo.basicInfoName);
		values.put("basic_has", bsInfo.hasAdopted);
		values.put("basic_last", bsInfo.lastLogTime);
		values.put("basic_hunger", bsInfo.hunger);
		values.put("basic_type", bsInfo.type);
		
		db.insert(TABLE_NAME, null, values);
		
	}
	
	//获取BasicInfoDBO中保存的条目
	public BasicInfo getBasicInfo(){
		
		SQLiteDatabase sqdb = getReadableDatabase();
		Cursor cursor = sqdb.query(
				                        TABLE_NAME, 
				                        new String[] {"basic_name", "basic_has", "basic_last", "basic_hunger ", "basic_type"},
				                        "", null, null, null, null
				                        );
		
		if (cursor.getCount() == 0 || !cursor.moveToFirst()) {
			sqdb.close();
			return null;
		}

	    BasicInfo member = new BasicInfo(
			cursor.getInt(cursor.getColumnIndex("basic_name")),
			cursor.getInt(cursor.getColumnIndex("basic_has")),
			cursor.getString(cursor.getColumnIndex("basic_last")),
			cursor.getInt(cursor.getColumnIndex("basic_hunger")),
			cursor.getInt(cursor.getColumnIndex("basic_type"))
	    );
		sqdb.close();
		
		return member;
	}
	
	
	/* 新建成员 */
	public long insert(BasicInfo bsInfo) {
		SQLiteDatabase db = getReadableDatabase();
		ContentValues values = new ContentValues();
		
		values.put("basic_name", bsInfo.basicInfoName);
		values.put("basic_has", bsInfo.hasAdopted);
		values.put("basic_last", bsInfo.lastLogTime);
		values.put("basic_hunger", bsInfo.hunger);
		values.put("basic_type", bsInfo.type);
		
		long member_id = db.insert(TABLE_NAME, null, values);
		db.close();
		return member_id;
	}
	
	/* 修改成员 */
	public int update(BasicInfo bsInfo) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put("basic_name", bsInfo.basicInfoName);
		values.put("basic_has", bsInfo.hasAdopted);
		values.put("basic_last", bsInfo.lastLogTime);
		values.put("basic_hunger", bsInfo.hunger);
		values.put("basic_type", bsInfo.type);
		
		int row = db.update(TABLE_NAME, values, "basic_name=" + bsInfo.basicInfoName, null);
		db.close();
		return row;
	}
	
	//更新宠物类型列的值
    public void updatePetType(int name,  int type_) {
    	SQLiteDatabase db = getReadableDatabase();
        ContentValues updateBasic = new ContentValues();
        updateBasic.put("basic_type", type_);
        db.update(TABLE_NAME, updateBasic, "basic_name=" + name, null);
        db.close();
    }
}
