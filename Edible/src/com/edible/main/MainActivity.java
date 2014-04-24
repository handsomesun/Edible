package com.edible.main;

import com.edible.ocr.CaptureActivity;
import com.edible.ocr.Global;
import com.edible.ocr.R;
import com.edible.ocr.R.id;
import com.edible.ocr.R.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Button open_camera;
	private Context classContext;
	private TextView resultView;
	private TextView IPView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		classContext = this;
		resultView = (TextView)findViewById(R.id.resultView);
		IPView = (TextView)findViewById(R.id.IP_View);
		open_camera = (Button)findViewById(R.id.open_camera);
		open_camera.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String IP = (String) IPView.getText();
				Intent start_camera = new Intent(classContext, CaptureActivity.class);
				start_camera.putExtra("IP", IP);
				startActivityForResult(start_camera, Global.GET_RESULT_REQUEST);
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
	    if (requestCode == Global.GET_RESULT_REQUEST) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	        	resultView.setText(data.getStringExtra(Global.GET_RESULT_REQUEST_ID));
	        }
	    }
	}
}
