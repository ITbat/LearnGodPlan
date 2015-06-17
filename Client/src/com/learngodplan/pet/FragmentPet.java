package com.learngodplan.pet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learngodplan.R;
import com.learngodplan.db.BasicInfo;
import com.learngodplan.db.BasicInfoDBO;
import com.learngodplan.db.Food;
import com.learngodplan.db.FoodDBO;
import com.learngodplan.home.MainActivity;
import com.learngodplan.mood.FragmentMood;

public class FragmentPet extends Fragment {
	//REQUEST_CODE = 2是进入选择宠物列表
	 public static  int REQUEST_CHOOSE_PET = 10086;
	
	private int leftHunger = 0;
	
	public static ImageView petImg;
    private ProgressBar hungerBar;
	private TextView hungerText;
	
	public static BasicInfo myBasic;
	private ArrayList<Food> myFoodArray;
	private FoodDBO foodDBO;
	public static BasicInfoDBO basicDBO;
	
	public void initData(){
		basicDBO = new BasicInfoDBO(MainActivity.mainAcContext);
		foodDBO = new FoodDBO(MainActivity.mainAcContext);
		
		myBasic = basicDBO.getBasicInfo();
		myFoodArray = foodDBO.getAllFood();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActionBar acb = ((Activity) MainActivity.mainAcContext).getActionBar();
        acb.setTitle("我的宠物");
        
		initData();
		
		View view = inflater.inflate(R.layout.fragment_pet, container,false);
		//设置显示的宠物
		setPet(view);
		//设置领养宠物
		setAdoptPet(view);
		
        //设置饥饿条
		setHungerBar(view);
		
		//设置饲养动画
    	setAnimation(view);
		
		return view;
	}
	
	public void setPet(View v){
        petImg = (ImageView)v.findViewById(R.id.petImg);
		petImg.setOnClickListener( new OnClickListener(){
			//如果还没领养宠物，才可以点击,跳转到宠物领养页面
			@Override
			public void onClick(View v) {
				if(myBasic.type == 999){
					Intent toAdoptIntent = new Intent(MainActivity.mainAcContext, PetAdoptActivity.class);
					startActivityForResult(toAdoptIntent, REQUEST_CHOOSE_PET);
				}
				else{
					return;
				}
			}
		});
		switch(myBasic.type){
			case 1: petImg.setImageResource(R.drawable.pet1);break;
			case 2: petImg.setImageResource(R.drawable.pet2);break;
			case 3: petImg.setImageResource(R.drawable.pet3);break;
			default: petImg.setImageResource(R.drawable.pet_empty);break;
		}
	}
	
	public void setHungerBar(View v){
		Log.d("myBasic.lastLogTime", "myBasic.lastLogTime before setHunger is " +myBasic.lastLogTime );
		Log.d("hunger", "hungerBefore is "+myBasic.hunger);
		
		int hunger = myBasic.hunger;
		hungerText = (TextView)v.findViewById(R.id.hungerText);
		hungerBar = (ProgressBar)v.findViewById(R.id.hungerBar);
		
        //获取当前日期
    	Calendar ca = Calendar.getInstance();
    	int  year = ca.get(Calendar.YEAR);
    	int month = ca.get(Calendar.MONTH) + 1;
    	int day = ca.get(Calendar.DAY_OF_MONTH);
    	int hour = ca.get(Calendar.HOUR_OF_DAY);
		//根据当前时间与上次记录下来的时间，初始化饥饿值条,每小时减少5点饥饿
    	String[] dateArray = myBasic.lastLogTime.split("-");
    	int subtractor;
    	if(year - Integer.parseInt(dateArray[0]) >= 1){
    		subtractor = 100;
    	}
    	else if( month - Integer.parseInt(dateArray[1]) >= 1){
    		subtractor = 100;
    	}
    	else {
    		subtractor = (( day - Integer.parseInt(dateArray[2]) ) * 24 + hour - Integer.parseInt(dateArray[3])) * 5;
    	}
    	if(subtractor > 100){
    		Log.e("subtractor is over 100", "substractor is over 100");
    		subtractor = 100;
    	}
    	
    	leftHunger = hunger - subtractor;
    	if(leftHunger <= 0){
    		leftHunger = 0;
    	}
    	if(leftHunger > 100){
    		leftHunger = 100;
    	}
    	
    	hungerText.setText(String.valueOf(leftHunger) + "/" + "100");
    	hungerBar.setProgress(leftHunger);
    	
    	//更新饥饿值, 更新最后登陆时间
    	myBasic.hunger = leftHunger;
    	String str = String.valueOf(year)
		          +"-"
		          +String.valueOf(month)
		          +"-"
		          +String.valueOf(day)
		          +"-"
		          +String.valueOf(hour);
    	myBasic.lastLogTime = str;
    	basicDBO.update(myBasic);
    	
		Log.d("myBasic.lastLogTime", "myBasic.lastLogTime after setHunger is "+myBasic.lastLogTime);
		Log.d("hunger", "hungerAfter is "+myBasic.hunger);
	}
	
	public void setAdoptPet(View v){
		ImageButton adiv = (ImageButton)v.findViewById(R.id.adoptPetImg);
		adiv.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent toAdoptIntent = new Intent(MainActivity.mainAcContext, PetAdoptActivity.class);
				startActivityForResult(toAdoptIntent, REQUEST_CHOOSE_PET);
			}
			
		});
	}
	
	public void setAnimation(View v){
		ImageView iv = (ImageView)v.findViewById(R.id.petFoodImg);
		iv.setBackgroundResource(R.layout.petfood_animation);
		AnimationDrawable anim = (AnimationDrawable)iv.getBackground();
		anim.start();
		
        //点击饲养宠物弹出自定义对话框
		iv.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {
				View foodView = View.inflate(MainActivity.mainAcContext, R.layout.pop_food, null);
				ListView foodList = (ListView)foodView.findViewById(R.id.popFoodList);
				if(foodList == null){
					Log.d("test", "null");
				}
				
				Log.d("test my foodArray", String.valueOf(myFoodArray.size()));
				
				int[] libPicId = new int[] {
						R.drawable.food_in_list1, 
						R.drawable.food_in_list2, 
						R.drawable.food_in_list3,
						R.drawable.food_in_list4,
						R.drawable.food_in_list5};
				String[] libEffect = new String[]{
						 "+20饥饿", "+25饥饿", "+30饥饿", "+35饥饿", "+40饥饿"
				};
				
				String[] libFoodNum = new String[]{
					"剩余:", "剩余:", "剩余:", "剩余:", "剩余:"
				};
				
				//初始化foodList的adapter
				ArrayList<Map<String, Object>> lvMap = new ArrayList<Map<String, Object>>();  
				for (int i = 0; i < myFoodArray.size(); i++) {  
				    Map<String, Object> listItem = new HashMap<String, Object>();  
				    listItem.put("libPicId", libPicId[i]);  
				    listItem.put("libEffect", libEffect[i]);
				    libFoodNum[i] += String.valueOf(myFoodArray.get(i).foodNum);
				    listItem.put("libFoodNum",  libFoodNum[i]);
				    lvMap.add(listItem);  
				}
				
		        /*SimpleAdapter的参数说明 
		         * 第一个参数 表示访问整个android应用程序接口，基本上所有的组件都需要 
		         * 第二个参数表示生成一个Map(String ,Object)列表选项 
		         * 第三个参数表示界面布局的id  表示该文件作为列表项的组件 
		         * 第四个参数表示该Map对象的哪些key对应value来生成列表项 
		         * 第五个参数表示来填充的组件 Map对象key对应的资源一依次填充组件 顺序有对应关系 
		         * */  
				SimpleAdapter adp = new SimpleAdapter(
						MainActivity.mainAcContext, 
						lvMap,
						R.layout.food_list_item,
						new String[] {"libPicId", "libEffect", "libFoodNum"},
						new int[] {R.id.foodListImage, R.id.foodListEffect, R.id.foodListNum }
				);
				
				foodList.setAdapter(adp);

				final AlertDialog foodWindow = new AlertDialog.Builder(MainActivity.mainAcContext)
                .setView(foodView)
                .setTitle("喂养宠物")
                .show();
				
				foodList.setOnItemClickListener(new OnItemClickListener(){
					//处理ListView点击事件，更新数据库的宠物的饥饿值，所选的食物的数量，myFoodArray对应的内容,关闭对话框
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if(myFoodArray.get(position).foodNum == 0){
							Toast.makeText(MainActivity.mainAcContext, "库存不足", Toast.LENGTH_SHORT).show();
						}else{
							//更新myFoodArray的内容
							myFoodArray.get(position).foodNum -= 1;
							//更新宠物的饥饿,饥饿条
							leftHunger += myFoodArray.get(position).foodHunger;
							if(leftHunger > 100){
								leftHunger = 100;
							}
					    	hungerText.setText(String.valueOf(leftHunger) + "/" + "100");
					    	hungerBar.setProgress(leftHunger);
					    	//更新数据库hunger值, 更新food数据库的num
					    	myBasic.hunger = leftHunger;
					    	basicDBO.update(myBasic);
					    	Log.d("test", "test");
					    	foodDBO.update(myFoodArray.get(position));
					    	
							Toast.makeText(
									MainActivity.mainAcContext, 
									"喂食成功+"+String.valueOf(myFoodArray.get(position).foodHunger)+"饥饿",
									Toast.LENGTH_SHORT
							).show();
							//关闭弹出的窗口
							foodWindow.dismiss();
						}
					}
				});
			}
			
		});
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		    super.onActivityResult(requestCode, resultCode, intent);
	        Log.e("ee","call  in Fragment onActivityResult");
	        
			//如果当时启动的是选择宠物的Activity,就更新当前显示的宠物,更新BasicInfo中的basic_type列
			if(requestCode == FragmentPet.REQUEST_CHOOSE_PET && resultCode == Activity.RESULT_OK){
				Log.d("called choose pet","xxxxxxxxxxxxx");
				//获取回传的选择了哪个宠物的数据
				Bundle bd = intent.getExtras();
				
	            //更新BasicInfo的type
				Log.d("test updateType", "test updateType");
				FragmentPet.basicDBO.updatePetType(FragmentPet.myBasic.basicInfoName, bd.getInt("petType"));
				Log.d("test updateType", "test updateType2");
				
				switch(bd.getInt("petType")){
				case 1: FragmentPet.petImg.setImageResource(R.drawable.pet1);break;
				case 2: FragmentPet.petImg.setImageResource(R.drawable.pet2);break;
				case 3: FragmentPet.petImg.setImageResource(R.drawable.pet3);break;
				}
			}	    	
	    }

}