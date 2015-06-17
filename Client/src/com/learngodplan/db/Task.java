package com.learngodplan.db;

public class Task {
	public long tId;
    public String tName;
    public int tPriority;
    public String tStartTime;
    public String tEndTime;
    public int tPlanTime;
    public int tTotalTime;
    //0为未，1为已
    public int isFinished;
    
    public Task(){}
	
	public Task(long id, String n, int p, int planLearnTime_, int totalLearnTime_, int isfinish, String st, String et){
		tId = id;
		tPriority = p;
		isFinished = isfinish;
		
		tPlanTime = planLearnTime_;
		tTotalTime = totalLearnTime_;
		
		tName = n;
		tStartTime = st;
		tEndTime = et;
	}
}
