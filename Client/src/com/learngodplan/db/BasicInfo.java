package com.learngodplan.db;

public class BasicInfo {
	public int basicInfoName;
	public int hasAdopted;
    public String lastLogTime;
    public int hunger;
    public int type;
    
    public BasicInfo() {}
    
    public BasicInfo(int basicName, int has, String last, int hunger_, int type_){
    	basicInfoName = basicName;
    	hasAdopted = has;
    	lastLogTime = last;
    	hunger = hunger_;
    	type = type_;
    }
}
