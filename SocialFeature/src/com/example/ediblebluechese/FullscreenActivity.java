 package com.example.ediblebluechese;


import com.example.ediblebluechese.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class FullscreenActivity extends Activity {

	private Button loginButton;
	private Button signButton;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
			loginButton = (Button)findViewById(R.id.login_button);
		    loginButton.setOnClickListener(new View.OnClickListener() {
				
		    	@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub		    		
					Intent login = new Intent(context, LoginActivity.class);
					startActivity(login);
				}
			});	    
		    
		    signButton = (Button)findViewById(R.id.sign_button);
		    signButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub					
					Intent signUp = new Intent(context, SignUpActivity.class);
					startActivity(signUp);
				}
			});
	}


}

