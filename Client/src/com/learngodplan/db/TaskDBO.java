package com.learngodplan.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDBO extends SQLiteOpenHelper {
	private static final String DB_NAME = "taskDB01.db";
    private static final String TABLE_NAME = "TaskData";
    private static final int DB_VERSION = 1;
	
	public TaskDBO(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	public TaskDBO(Context context, String name, CursorFactory factory, int version) {
		super(context, DB_NAME, factory, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE" + " " + TABLE_NAME 
				          + "(" 
				          + "task_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				          + "task_priority INTEGER,"
				          + "task_name TEXT,"
				          + "task_plan_time INTEGER,"
				          + "task_total_time INTEGER,"
				          + "task_isfinished INTEGER,"
				          + "task_start_time TEXT,"
				          + "task_end_time TEXT)";
		db.execSQL(sql);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	public ArrayList<Task> getAllTask(){
		ArrayList<Task> tl = new ArrayList<Task>();
		
		SQLiteDatabase taskDB = getReadableDatabase();

		Cursor cursor = taskDB.query(TABLE_NAME, new String[] {"task_id", "task_name", "task_priority", "task_plan_time", "task_total_time", "task_isfinished", "task_start_time", "task_end_time" }, "", null, null, null, null);
		if (cursor.getCount() == 0 || !cursor.moveToFirst()) {
			taskDB.close();
			return null;
		}

		for (int i = 0; i < cursor.getCount(); ++i) {
			Task member = new Task(
					cursor.getInt(cursor.getColumnIndex("task_id")),
					cursor.getString(cursor.getColumnIndex("task_name")),
					cursor.getInt(cursor.getColumnIndex("task_priority")),
					cursor.getInt(cursor.getColumnIndex("task_plan_time")),
					cursor.getInt(cursor.getColumnIndex("task_total_time")),
					cursor.getInt(cursor.getColumnIndex("task_isfinished")),
					cursor.getString(cursor.getColumnIndex("task_start_time")),
					cursor.getString(cursor.getColumnIndex("task_end_time"))
			);
			tl.add(member);
			cursor.moveToNext();
		}
		taskDB.close();
		return tl;
	}
	
	/* 新建成员 */
	public long insert(Task entity) {
		SQLiteDatabase db = getReadableDatabase();
		ContentValues values = new ContentValues();
//		values.put("task_id", entity.tId);
		values.put("task_name", entity.tName);
		values.put("task_priority", entity.tPriority);
		values.put("task_plan_time", entity.tPlanTime);
		values.put("task_total_time", entity.tTotalTime);
		values.put("task_start_time", entity.tStartTime);
		values.put("task_end_time", entity.tEndTime);
		values.put("task_isfinished", entity.isFinished);
		
		long member_id = db.insert(TABLE_NAME, null, values);
		db.close();
		return member_id;
	}
	
	/* 修改成员 */
	public int update(Task entity) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
     	values.put("task_id", entity.tId);
		values.put("task_name", entity.tName);
		values.put("task_priority", entity.tPriority);
		values.put("task_plan_time", entity.tPlanTime);
		values.put("task_total_time", entity.tTotalTime);
		values.put("task_start_time", entity.tStartTime);
		values.put("task_end_time", entity.tEndTime);
		values.put("task_isfinished", entity.isFinished);
		
		int row = db.update(TABLE_NAME, values, "task_id=" + entity.tId, null);
		db.close();
		return row;
	}
	
	/* 清除成员 */
	public int delete(long member_id) {
		SQLiteDatabase db = getReadableDatabase();
		int row = db.delete(TABLE_NAME, "task_id=" + member_id, null);
		db.close();
		return row;
	}
    
    /* 设置任务内容 */
    public void updateIsFinished(int task_id, int isFinished_) {
    	SQLiteDatabase db = getReadableDatabase();
        ContentValues updateTask = new ContentValues();
        updateTask.put("task_isfinished", isFinished_);
        db.update(TABLE_NAME, updateTask, "task_id=" + task_id, null);
        db.close();
    }
	
	//有BUG
//	//根据ID获取task的名称
//    public String getTaskNameByID(int task_id) {
//    	SQLiteDatabase db = getReadableDatabase();
//    	Cursor cursor = db.query(
//    			false,
//                TABLE_NAME,
//                new String[] {"task_id"},
//                "task_id=" +task_id, null, null, null, null, null);
//        if (cursor.getCount() == 0 || !cursor.moveToFirst()) {
//            db.close();
//            return null;
//        }
//        db.close();
//        return cursor.getString(cursor.getColumnIndex("task_name"));
//    }  
}
