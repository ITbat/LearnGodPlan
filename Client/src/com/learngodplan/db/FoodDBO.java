package com.learngodplan.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FoodDBO extends SQLiteOpenHelper {
	private static final String DB_NAME = "foodDB04.db";
    private static final String TABLE_NAME = "Food";
    private static final int DB_VERSION = 1;
    
	public FoodDBO(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE" + " " + TABLE_NAME 
				          + "(" 
				          + "food_id INTEGER PRIMARY KEY, "
				          + "food_name TEXT, "
				          + "food_num INTEGER, "
				          + "food_hunger INTEGER"
				          + ")";
		db.execSQL(sql);
		
		//第一次创建数据库时候就初始化数据库中的5个条目,后续进行的操作均为更新这些条目中的food_num列
		Food[] foodArray = {
			new Food(1, "白甜筒", 0 ,20),
			new Food(2, "草莓", 0, 25),
			new Food(3, "冰激凌", 0, 30),
			new Food(4, "火腿", 0, 35),
			new Food(5, "红甜筒", 0, 40)
		};
		
		for( int i = 0; i < 5; i++){
			ContentValues values = new ContentValues();
			
			values.put("food_id", foodArray[i].foodId);
			values.put("food_name", foodArray[i].foodName);
			values.put("food_num", foodArray[i].foodNum);
			values.put("food_hunger", foodArray[i].foodHunger);
			
            db.insert(TABLE_NAME, null, values);
		}
	}
	
	//获取全部食物的库存
	public ArrayList<Food> getAllFood(){
		ArrayList<Food> allFood = new ArrayList<Food>();
		SQLiteDatabase sqdb = getReadableDatabase();
		Cursor cursor = sqdb.query(TABLE_NAME, new String[] {"food_id", "food_name", "food_num", "food_hunger" }, "", null, null, null, null);
		
		if (cursor.getCount() == 0 || !cursor.moveToFirst()) {
			sqdb.close();
			return null;
		}

		for (int i = 0; i < cursor.getCount(); ++i) {
			Food member = new Food(
					cursor.getInt(cursor.getColumnIndex("food_id")),
					cursor.getString(cursor.getColumnIndex("food_name")),
					cursor.getInt(cursor.getColumnIndex("food_num")),
					cursor.getInt(cursor.getColumnIndex("food_hunger"))
			);
			allFood.add(member);
			cursor.moveToNext();
		}
		sqdb.close();
		
		return allFood;
	}
	
	/* 新建成员 */
	public long insert(Food entity) {
		SQLiteDatabase db = getReadableDatabase();
		ContentValues values = new ContentValues();
		
		values.put("food_id", entity.foodId);
		values.put("food_name", entity.foodName);
		values.put("food_num", entity.foodNum);
		values.put("food_hunger", entity.foodHunger);
		
		long member_id = db.insert(TABLE_NAME, null, values);
		db.close();
		return member_id;
	}
	
	/* 修改成员 */
	public int update(Food entity) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("food_id", entity.foodId);
     	values.put("food_name", entity.foodName);
		values.put("food_num", entity.foodNum);
		values.put("food_hunger", entity.foodHunger);
		
		int row = db.update(TABLE_NAME, values, "food_id=" + entity.foodId, null);
		db.close();
		return row;
	}
}
