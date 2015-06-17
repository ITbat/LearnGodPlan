package com.learngodplan.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learngodplan.R;
import com.learngodplan.mood.FragmentMood;
import com.learngodplan.mood.LoginOrRegistActivity;
import com.learngodplan.pet.FragmentPet;
import com.learngodplan.plan.FragmentPlan;
  
  
  
public class MainActivity extends FragmentActivity implements OnClickListener{  
  
    //定义4个Fragment的对象  
    public static  FragmentHome fgHome;  
    public static  FragmentPlan fgPlan;  
    public static FragmentPet fgPet;
    public static  FragmentMood fgPersonal;
    
    //定义底部导航栏的三个布局  
    public static RelativeLayout guideLayout_Home;  
    public static RelativeLayout guideLayout_Plan;  
    public static RelativeLayout guideLayout_Pet; 
    public static RelativeLayout guideLayout_Personal;
    
    //定义底部导航栏中的ImageView与TextView  
    public static  ImageView guideImg_Home;  
    public static ImageView guideImg_Plan;  
    public static ImageView guideImg_Pet;  
    public static ImageView guideImg_Personal;  
    
    public static  TextView guideText_Home;  
    public static TextView guideText_Plan;  
    public static TextView guideText_Pet;
    public static TextView guideText_Personal;
    
    //定义要用的颜色值  
    public static int whirt = 0xFFFFFFFF;  
    public static int gray = 0xFF7597B3;  
    public static int blue =0xFF0AB2FB;  
    
    public static Context mainAcContext;
    
    //定义FragmentManager对象  
    public static  FragmentManager fManager;  
    public static  FragmentTransaction transaction;
    
    //再按一次退出程序的按钮
    public static long exitTime = 0;
	
    @Override  
    protected void onCreate(Bundle savedInstanceState) {      	
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
        fManager = getSupportFragmentManager();  
        initViews();  
        mainAcContext = this;
        
        //默认显示home的fragment
        FragmentTransaction transaction = fManager.beginTransaction();    
        clearChioce();  
        hideFragments(transaction);  
        guideImg_Home.setImageResource(R.drawable.pressbottom_img_home);    
        guideText_Home.setTextColor(blue);  
        guideLayout_Home.setBackgroundResource(R.drawable.ic_tabbar_bg_click);  
        if (fgHome == null) {    
            // 如果fgHome为空，则创建一个并添加到界面上    
            fgHome = new FragmentHome();    
            transaction.add(R.id.content, fgHome);
            transaction.show(fgHome); 
        } else {    
            //如果MessageFragment不为空，则刷新它
            fgHome = new FragmentHome();   
        	transaction.replace(R.id.content, fgHome);
            transaction.show(fgHome);    
        }
        transaction.commit();
    }  
       
    public boolean onCreateOptionsMenu(Menu menu) { 
        // Inflate the menu; this adds items to the action bar if it is present.  
        getMenuInflater().inflate(R.menu.menu_logout, menu); 
        return true; 
    } 
    
    @Override 
    public boolean onOptionsItemSelected(MenuItem item) { 
          switch (item.getItemId()) { 
               case R.id.logout_option: {
					transaction = MainActivity.fManager.beginTransaction();			

				    SharedPreferences sharedPreferences = getSharedPreferences("userInfo",  Activity.MODE_PRIVATE);  
				    
					if(sharedPreferences.getBoolean("hasLog", false)){
						Editor editor = sharedPreferences.edit();//获取编辑器  
						editor.putBoolean("hasLog", false);
						editor.commit();//提交修改  
						//返回计划首页
						
				        //重置选项+隐藏所有Fragment  
				        FragmentTransaction transaction = MainActivity.fManager.beginTransaction();    
				        MainActivity.guideImg_Personal.setImageResource(R.drawable.unpressbottom_img_personal);  
				        MainActivity.guideLayout_Personal.setBackgroundColor(MainActivity.whirt);  
				        MainActivity.guideText_Personal.setTextColor(MainActivity.gray);  
						
			            MainActivity.guideImg_Home.setImageResource(R.drawable.pressbottom_img_home);    
			            MainActivity.guideText_Home.setTextColor( MainActivity.blue);  
			            MainActivity.guideLayout_Home.setBackgroundResource(R.drawable.ic_tabbar_bg_click);
			            
			            if ( MainActivity.fgHome == null) {    
			            	 MainActivity.fgHome = new FragmentHome();    
			            	 transaction.add(R.id.content,  MainActivity.fgHome);    
			            } else {    
			            	 MainActivity.fgHome = new FragmentHome(); 
			            	 transaction.replace(R.id.content,  MainActivity.fgHome);
			            	 transaction.show( MainActivity.fgHome);    
			            } 
			            
			            transaction.commit();
			            Toast.makeText(this, "注销成功", Toast.LENGTH_SHORT).show();
					}
					else{
						Toast.makeText(this, "您目前还没有登陆", Toast.LENGTH_SHORT).show();
					}
               }
               break; 
             
               default: 
               break; 
          } 
   
          return true; 
    }  
    
    //完成导航组件的初始化  
    public void initViews()  
    {  
        guideImg_Home = (ImageView) findViewById(R.id.guideImg_Home);  
        guideImg_Plan = (ImageView) findViewById(R.id.guideImg_Plan);  
        guideImg_Pet = (ImageView) findViewById(R.id.guideImg_Pet);
        guideImg_Personal = (ImageView) findViewById(R.id.guideImg_Personal);
        
        guideText_Home = (TextView) findViewById(R.id.guideText_Home);  
        guideText_Pet = (TextView) findViewById(R.id.guideText_Pet);  
        guideText_Plan = (TextView) findViewById(R.id.guideText_Plan); 
        guideText_Personal = (TextView)findViewById(R.id.guideText_Personal);
        
        guideLayout_Home = (RelativeLayout) findViewById(R.id.guideLayout_Home);  
        guideLayout_Plan = (RelativeLayout) findViewById(R.id.guideLayout_Plan);  
        guideLayout_Pet = (RelativeLayout) findViewById(R.id.guideLayout_Pet);  
        guideLayout_Personal = (RelativeLayout) findViewById(R.id.guideLayout_Personal);  
        
        guideLayout_Home.setOnClickListener(this);  
        guideLayout_Plan.setOnClickListener(this);   
        guideLayout_Pet.setOnClickListener(this);
        guideLayout_Personal.setOnClickListener(this);
        
        transaction = fManager.beginTransaction();  
    }  
      
    //重写onClick事件  
    @Override  
    public  void onClick(View view) {  
        switch (view.getId()) {  
        case R.id.guideLayout_Home:  
            setChioceItem(0);  
            break;  
        case R.id.guideLayout_Plan:  
            setChioceItem(1);  
            break;  
        case R.id.guideLayout_Pet:  
            setChioceItem(2);  
            break;  
        case R.id.guideLayout_Personal:  
            setChioceItem(3);  
            break;  
            
        default:  
            break;  
        }  
          
    }  
      
      
    //定义一个选中一个item后的处理  
    public  void setChioceItem(int index)  
    {  
        //重置选项+隐藏所有Fragment  
        FragmentTransaction transaction = fManager.beginTransaction();    
        clearChioce();  
        hideFragments(transaction);  
        switch (index) {  
        case 0:  
            guideImg_Home.setImageResource(R.drawable.pressbottom_img_home);    
            guideText_Home.setTextColor(blue);  
            guideLayout_Home.setBackgroundResource(R.drawable.ic_tabbar_bg_click);  
            if (fgHome == null) {    
                fgHome = new FragmentHome();    
                transaction.add(R.id.content, fgHome);    
            } else {    
                fgHome = new FragmentHome(); 
            	transaction.replace(R.id.content, fgHome);
                transaction.show(fgHome);    
            }    
            break;    
  
        case 1:  
            guideImg_Plan.setImageResource(R.drawable.pressbottom_img_plan);    
            guideText_Plan.setTextColor(blue);  
            guideLayout_Plan.setBackgroundResource(R.drawable.ic_tabbar_bg_click);  
            if (fgPlan == null) {    
                fgPlan = new FragmentPlan();    
                transaction.add(R.id.content, fgPlan);    
            } else {     
                fgPlan= new FragmentPlan(); 
            	transaction.replace(R.id.content, fgPlan);
                transaction.show(fgPlan);  
            }    
            break;        
          
         case 2:  
            guideImg_Pet.setImageResource(R.drawable.pressbottom_img_pet);    
            guideText_Pet.setTextColor(blue);  
            guideLayout_Pet.setBackgroundResource(R.drawable.ic_tabbar_bg_click); 
            if (fgPet == null) {      
                fgPet = new FragmentPet();    
                transaction.add(R.id.content, fgPet);    
            } else {    
                fgPet = new FragmentPet(); 
            	transaction.replace(R.id.content, fgPet);
                transaction.show(fgPet);  
            }    
            break;
            
         case 3:  
             //进入心情墙前，检查用户是否登陆，登陆则转入心情墙，未登录转入注册或登陆界面
        	 SharedPreferences preferences = getSharedPreferences("userInfo",   Activity.MODE_PRIVATE);  
             boolean login = preferences.getBoolean("hasLog", false);
             if(login){
                 guideImg_Personal.setImageResource(R.drawable.pressbottom_img_personal);    
                 guideText_Personal.setTextColor(blue);  
                 guideLayout_Personal.setBackgroundResource(R.drawable.ic_tabbar_bg_click); 
                 
                 if (fgPersonal == null) { 
                     fgPersonal = new FragmentMood();    
                     transaction.add(R.id.content, fgPersonal);    
                 } else {     
                     fgPersonal = new FragmentMood(); 
                 	 transaction.replace(R.id.content, fgPersonal);
                     transaction.show(fgPersonal);  
                 }   	 
             }
             else{
//                 guideImg_Home.setImageResource(R.drawable.pressbottom_img_home);    
//                 guideText_Home.setTextColor(blue);  
//                 guideLayout_Home.setBackgroundResource(R.drawable.ic_tabbar_bg_click);  
                 
                 if (fgHome == null) { 
                     fgHome = new FragmentHome();    
                     transaction.add(R.id.content, fgHome);    
                 } else {     
                     fgHome = new FragmentHome(); 
                 	 transaction.replace(R.id.content, fgHome);
                     transaction.show(fgHome);  
                 }   	
                 
            	 Intent it = new Intent(MainActivity.this ,LoginOrRegistActivity.class);
            	 startActivityForResult(it , 3);
             }
             break;     
        }  
        transaction.commit();  
    }  
      
    //隐藏所有的Fragment,避免fragment混乱  
    public static void hideFragments(FragmentTransaction transaction) {    
        if (fgHome != null) {    
            transaction.hide(fgHome);    
        }    
        if (fgPlan != null) {    
            transaction.hide(fgPlan);    
        }    
        if (fgPet != null) {    
            transaction.hide(fgPet);    
        }
        if (fgPersonal != null) {    
            transaction.hide(fgPersonal);    
        }
    }    
              
    //定义一个重置所有选项的方法  
    public static void clearChioce()  
    {  
        guideImg_Home.setImageResource(R.drawable.unpressbottom_img_home);  
        guideLayout_Home.setBackgroundColor(whirt);  
        guideText_Home.setTextColor(gray);
        
        guideImg_Plan.setImageResource(R.drawable.unpressbottom_img_plan);  
        guideLayout_Plan.setBackgroundColor(whirt);  
        guideText_Plan.setTextColor(gray);  
        
        guideImg_Pet.setImageResource(R.drawable.unpressbottom_img_pet);  
        guideLayout_Pet.setBackgroundColor(whirt);  
        guideText_Pet.setTextColor(gray);  
        
        guideImg_Personal.setImageResource(R.drawable.unpressbottom_img_personal);  
        guideLayout_Personal.setBackgroundColor(whirt);  
        guideText_Personal.setTextColor(gray);  
    } 
    
    //"再按一次退出程序"
    @Override
    public  boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
		    if((System.currentTimeMillis()-exitTime) > 1000){
			    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
			    exitTime = System.currentTimeMillis();
		    } 
		    else {
			    finish();
			    System.exit(0);
		    }
		    return true;
	    }
	    return super.onKeyDown(keyCode, event);
    } 
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){ 
    	super.onActivityResult(requestCode, resultCode, data);
    	
        Log.e("ee","call here in mainactivity onActivityResult");
    	if(resultCode == Activity.RESULT_OK && requestCode == 3){
            //重置选项+隐藏所有Fragment    
            clearChioce();  
            hideFragments(transaction); 
            Log.d("on ActivityResult is called", "when log activity is finished");
            guideImg_Personal.setImageResource(R.drawable.pressbottom_img_personal);    
            guideText_Personal.setTextColor(blue);  
            guideLayout_Personal.setBackgroundResource(R.drawable.ic_tabbar_bg_click); 

            if (fgPersonal == null) { 
                fgPersonal = new FragmentMood();    
                transaction.add(R.id.content, fgPersonal);    
            } else {     
                 fgPersonal = new FragmentMood(); 
            	 transaction.replace(R.id.content, fgPersonal);
                 transaction.show(fgPersonal);  
            } 
            transaction.commitAllowingStateLoss();  
            return;
    	}
    	
		//如果当时启动的是选择宠物的Activity,就更新当前显示的宠物,更新BasicInfo中的basic_type列
		if(requestCode == FragmentPet.REQUEST_CHOOSE_PET && resultCode == Activity.RESULT_OK){
             //调用宠物fragment的onActivityResult
			 fgPet.onActivityResult(requestCode, resultCode, data);
		}
    	
    }
}