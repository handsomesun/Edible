package com.example.ediblebluechese;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends Activity {
	EditText emailField;
	EditText usernameField;
	EditText passwordField;
	Button signUpButton;
	Button cancelButton;
	JSONObject result = null;
	Client client = null;
	Context context;
	
	private Handler signUpHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what) {
			case Global.MSG_SUCCESS:
				try {
					if ( result.getBoolean("status") == true ) {
					    cacheSelfie();
					    cacheInfo();
					    Intent goToProfile = new Intent(context, ProfileActivity.class);
						startActivity( goToProfile );
					} else {
						Toast.makeText(getApplicationContext(), result.getString("log"), Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case Global.MSG_FAILURE:
				Toast.makeText(getApplicationContext(), "Network Unavailable..", Toast.LENGTH_LONG).show();
				break;
			}
			super.handleMessage(msg);
		}
    	
    };
    
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what) {
			case Global.MSG_SUCCESS:
				try {
					if ( result.getBoolean("status") == true ) {
						Toast.makeText(getApplicationContext(), result.getString("log"), Toast.LENGTH_LONG).show();
						login();
					} else {
						Toast.makeText(getApplicationContext(), result.getString("log"), Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case Global.MSG_FAILURE:
				Toast.makeText(getApplicationContext(), "Network Unavailable..", Toast.LENGTH_LONG).show();
				break;
			}
			super.handleMessage(msg);
		}
    	
    };
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up);
		emailField = (EditText) findViewById(R.id.emailField);
		usernameField = (EditText) findViewById(R.id.username);
		passwordField = (EditText) findViewById(R.id.password);
		
		signUpButton = (Button) findViewById(R.id.sign_button);
		cancelButton = (Button) findViewById(R.id.cancel_button);
		client = new Client();
		context = this;
		signUpButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// pass info as Json to database
				new Thread(new Runnable() {
					
					public void run() {
						// TODO Auto-generated method stub
						try {
							JSONObject new_user = new JSONObject();
							new_user.put("uid", emailField.getText().toString());
							new_user.put("uname", usernameField.getText().toString());
							new_user.put("upwd", passwordField.getText().toString());
							
							result = client.signUp( new_user );
							
							if ( result != null ) {
								mHandler.obtainMessage(Global.MSG_SUCCESS).sendToTarget();
							} else {
								mHandler.obtainMessage(Global.MSG_FAILURE).sendToTarget();
							}
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							
						
						} 
					}
				}).start();  
			}
		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	private void login(){
		new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					JSONObject new_user = new JSONObject();
					new_user.put("uid", emailField.getText().toString());
					new_user.put("upwd", passwordField.getText().toString());
					
					result = client.login( new_user );
					
					if ( result != null ) {
						signUpHandler.obtainMessage( Global.MSG_SUCCESS ).sendToTarget();
					} else {
						signUpHandler.obtainMessage( Global.MSG_FAILURE ).sendToTarget();
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}).start(); 
	}
	
	private void cacheSelfie(){
		Object selfie_raw;
		try {
		if( ( selfie_raw =  result.get("uselfie") ) != JSONObject.NULL ) {
				FileOutputStream fos = openFileOutput(Global.SELFIE_TEMP, Context.MODE_PRIVATE);
				JSONArray jsa = (JSONArray) selfie_raw;
				byte[] byteArray = new byte[jsa.length()];
				for ( int i = 0; i < byteArray.length; i++ ) {
					byteArray[i] = Byte.parseByte(jsa.getString(i));
				}
				fos.write( byteArray );
				fos.close();
			} else { 
				deleteFile(Global.SELFIE_TEMP);
			}
		    	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void cacheInfo() throws JSONException{
		SharedPreferences settings = getSharedPreferences(Global.PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putString(Global.PREF_KEY_ID, result.getString("uid"));
	    editor.putString(Global.PREF_KEY_USERNAME, result.getString("uname"));
	    editor.putString(Global.PREF_KEY_JOINTIME, result.getString("ucreate_time"));
	    editor.putInt(Global.PREF_KEY_USERTYPE, result.getInt("utype"));
	    editor.commit();
	}
}
