package com.learngodplan.db;

public class Food {
	public int foodId;
	public String foodName;
	public int foodNum;
	public int foodHunger;
	
	public Food(){}
	
	public Food(int id, String fname, int fnum, int hunger){
		foodId = id;
		foodName = fname;
		foodNum = fnum;
		foodHunger = hunger;
	}
}
