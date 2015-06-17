package com.learngodplan.home;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.learngodplan.R;

public class OpenAnimation extends Activity {
	private int WELCOM_DURATION =  3000;
    @Override
   	protected void onCreate(Bundle savedInstanceState) {
		   super.onCreate(savedInstanceState);
           setContentView(R.layout.welcome);
           
           new Handler().postDelayed(new Thread(){
        	       @Override
        	       public void run(){
        	    	     Intent loginIntent = new Intent(OpenAnimation.this, MainActivity.class);
        	    	     OpenAnimation.this.startActivity(loginIntent);
        	    	     OpenAnimation.this.finish();
        	    	     overridePendingTransition(R.layout.fadein, R.layout.fadeout);
        	       }
           }, WELCOM_DURATION);
	}

}
