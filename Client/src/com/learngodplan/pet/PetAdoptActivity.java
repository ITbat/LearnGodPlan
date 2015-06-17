package com.learngodplan.pet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.learngodplan.R;

public class PetAdoptActivity extends Activity {
	private ListView lv;
	private int PET_NUM = 3;
	private SimpleAdapter simplead;
	private List<Map<String, Object>> lvMap;
	
	private String[] libPicText = {
			"这是关于第一只宠物 的说明文本",
			"这是关于第二只宠物 的说明文本",
			"这是关于第三只宠物 的说明文本"
	};
	private int[] libPicId = {
			R.drawable.adopt_list_pet1, R.drawable.adopt_list_pet2, R.drawable.adopt_list_pet3
	};
	
     @Override
     public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			setContentView(R.layout.pet_adopt);
			
	        ActionBar acb = ((Activity) PetAdoptActivity.this).getActionBar();
	        acb.setTitle("领养宠物");
			
			lvMap = new ArrayList<Map<String, Object>>();  
			for (int i = 0; i < PET_NUM; i++) {  
			    Map<String, Object> listItem = new HashMap<String, Object>();  
			    listItem.put("libPicId", libPicId[i]);  
			    listItem.put("libPicText", libPicText[i]);  
			    lvMap.add(listItem);  
			}
	        
	        /*SimpleAdapter的参数说明 
	         * 第一个参数 表示访问整个android应用程序接口，基本上所有的组件都需要 
	         * 第二个参数表示生成一个Map(String ,Object)列表选项 
	         * 第三个参数表示界面布局的id  表示该文件作为列表项的组件 
	         * 第四个参数表示该Map对象的哪些key对应value来生成列表项 
	         * 第五个参数表示来填充的组件 Map对象key对应的资源一依次填充组件 顺序有对应关系 
	         * */  
	        simplead = new SimpleAdapter(
	        		              this,
	        		              lvMap,  
	                              R.layout.adopt_pet_item, 
	                              new String[] { "libPicId", "libPicText"},  
	                              new int[] {R.id.adoptItemImg, R.id.adoptItemText}
	        		          );  
	          
	        lv=(ListView)findViewById(R.id.adoptPetList);  
	        //debug
	        if(lv == null){
		        Log.d("x", "lv is null");	
	        }
	        lv.setAdapter(simplead);  
	        lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {					
	            // 绑定选择的宠物的类型,结束当前的Activity
	            Intent intent = new Intent();
	            Bundle bd = new Bundle();
	            bd.putInt("petType", position+1);
	            intent.putExtras(bd);

	            // Set result and finish this Activity
	            setResult(Activity.RESULT_OK, intent);
	            finish();
			}
        });
     }
}
