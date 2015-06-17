package com.learngodplan.alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import com.example.learngodplan.R;

public class AlarmActivity extends Activity{
	@Override
	 public void onCreate(Bundle savedInstanceState){
        final Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{1000, 1000, 1000, 1000}, -1);
		//从Intent取出任务的id获取任务名
        Intent it = this.getIntent();
        Bundle bd = it.getBundleExtra("taskNameBundle");
        String name = bd.getString("taskName");
        
		super.onCreate(savedInstanceState);
		new AlertDialog.Builder(this)
		.setTitle("任务提醒")
		.setIcon(R.drawable.info)
		.setMessage(name)
        .setPositiveButton("知道了",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                vibrator.cancel();
                AlarmActivity.this.finish();
            }
        })
        .show();
	}

}
