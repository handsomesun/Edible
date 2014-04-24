package com.example.ediblebluechese;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class ProfileActivity extends Activity {
	private static final int NONE = 0;  
    private static final int PHOTOHRAPH = 1;// capture
    private static final int PHOTOZOOM = 2; // cut photo 
    private static final int PHOTORESOULT = 3;
    private static final String IMAGE_UNSPECIFIED = "image/*";  
    
	private ImageView selfieView;
	private TextView IDView;
	private EditText usernameView;
	private TextView createTimeView;
	
	private Button editButton;
	private Button discoverButton;
	private Context context;
	private JSONObject result;
	private Client client;
	
	private Bitmap photo; // for recording the lasted chosen photo
	
	/* Update Thread */
	Thread updateInfo = new Thread(new Runnable() {
		
		public void run() {
			// TODO Auto-generated method stub
			try {
				SharedPreferences prefs = getSharedPreferences(Global.PREFS_NAME, 0);
				JSONObject new_user = new JSONObject();
				new_user.put("uid", prefs.getString(Global.PREF_KEY_ID, "null"));		
				new_user.put("old_uname", prefs.getString(Global.PREF_KEY_USERNAME, "null"));
				new_user.put("new_uname", usernameView.getText().toString());
				
				
				result = client.updateInfo( new_user );
				
				if ( result != null ) {
					mHandler.obtainMessage(Global.MSG_INFO_SUCCESS).sendToTarget();
				} else {
					mHandler.obtainMessage(Global.MSG_FAILURE).sendToTarget();
				}	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	});
	
Thread updateSelfie = new Thread(new Runnable() {
		
		public void run() {
			// TODO Auto-generated method stub
			try {
				SharedPreferences prefs = getSharedPreferences(Global.PREFS_NAME, 0);
				JSONObject new_user = new JSONObject();
				new_user.put("uid", prefs.getString(Global.PREF_KEY_ID, "null"));
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
				
				byte[] byteArray = stream.toByteArray();
				JSONArray jsa = new JSONArray();
				for ( byte b : byteArray ) {
					jsa.put(b);
				}
				new_user.put("uselfie", jsa);

				result = client.updateSelfie( new_user );
				
				if ( result != null ) {
					mHandler.obtainMessage(Global.MSG_SELFIE_SUCCESS).sendToTarget();
				} else {
					mHandler.obtainMessage(Global.MSG_FAILURE).sendToTarget();
				}	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	});
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what) {
			case Global.MSG_INFO_SUCCESS:
				try {
					if ( result.getBoolean("status") == true ) {
						Toast.makeText(getApplicationContext(), result.getString("log"), Toast.LENGTH_LONG).show();
						// Update local Username
						SharedPreferences settings = getSharedPreferences(Global.PREFS_NAME, 0);
					    SharedPreferences.Editor editor = settings.edit();
					    editor.putString(Global.PREF_KEY_USERNAME, usernameView.getText().toString());
					    editor.commit();
					    updateUI();
					} else {
						Toast.makeText(getApplicationContext(), result.getString("log"), Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case Global.MSG_SELFIE_SUCCESS:
				try {
					if ( result.getBoolean("status") == true ) {
						Toast.makeText(getApplicationContext(), result.getString("log"), Toast.LENGTH_LONG).show();
						cacheSelfie(photo);	
						updateUI();
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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
		
		selfieView = (ImageView) findViewById(R.id.selfie_view);
		IDView = (TextView) findViewById(R.id.id_view);
		usernameView = (EditText) findViewById(R.id.username_view);
		usernameView.setFocusableInTouchMode(false);
		usernameView.setFocusable(false);

		createTimeView = (TextView) findViewById(R.id.create_time_view);
		editButton = (Button) findViewById(R.id.edit_button);
		context = this;
		client = new Client();
		/* Select Image */
		final String [] items			= new String [] {"Take from camera", "Select from gallery"};				
		ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
		AlertDialog.Builder builder		= new AlertDialog.Builder(this);
		
		builder.setTitle("Select Image");
		builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int item ) { //pick from camera
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
	                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg")));  
	                dialog.dismiss();
	                startActivityForResult(intent, PHOTOHRAPH);  

				} else { 
					//pick from file
					Intent intent = new Intent(Intent.ACTION_PICK, null);  
	                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);  
	                dialog.dismiss();
	                startActivityForResult(intent, PHOTOZOOM);  
				}
			}
		} );
		
		final AlertDialog dialog = builder.create();
		
		selfieView.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.show();
			}
		});
		
		editButton.setOnClickListener( new View.OnClickListener() {
			
			private boolean editting = false;
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ( editting == false ) {
					usernameView.setFocusableInTouchMode(true);
					usernameView.setFocusable(true);
					editting = true;
					editButton.setText(R.string.SAVE);
				} else {
					updateInfo.start();
					usernameView.setFocusableInTouchMode(false);
					usernameView.setFocusable(false);
					editting = false;
					editButton.setText(R.string.EDIT);
				}
			}
		});
		discoverButton = (Button) findViewById(R.id.discover_button);
		// TODO OnClick
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateUI();
		
	}
	
	private void updateUI(){
		try {
			FileInputStream fis = openFileInput(Global.SELFIE_TEMP);
			
			byte[] selfie_raw =  Utility.FISToByteArray( fis );
			fis.close();
			Bitmap bMap = BitmapFactory.decodeByteArray(selfie_raw, 0, selfie_raw.length);
			selfieView.setImageBitmap(bMap);
		} catch (FileNotFoundException e) {
			selfieView.setImageResource(R.drawable.default_user);
		} catch (IOException e ) {
			e.printStackTrace();
		}
		
		SharedPreferences prefs = getSharedPreferences(Global.PREFS_NAME, 0);
		
		IDView.setText(prefs.getString(Global.PREF_KEY_ID, "null"));
		usernameView.setText(prefs.getString(Global.PREF_KEY_USERNAME, "null"));
		createTimeView.setText(prefs.getString(Global.PREF_KEY_JOINTIME, "null"));
	}
	/*
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != RESULT_OK) return;
	   
	    switch (requestCode) {
		    case PICK_FROM_CAMERA:
		    	doCrop();
		    	break;
		    case PICK_FROM_FILE: 
		    	mImageCaptureUri = data.getData();
		    	doCrop();
		    	break;	    	
		    case CROP_FROM_CAMERA:	    	
		        Bundle extras = data.getExtras();
		        if (extras != null) {	        	
		            Bitmap photo = extras.getParcelable("data");
		            
		            selfieView.setImageBitmap(photo);
		            cacheSelfie( photo );
		        }
		        File f = new File(mImageCaptureUri.getPath());            
		        if (f.exists()) f.delete();
		        break;

	    }
	}
	*/
	
	private void cacheSelfie( Bitmap photo ){
		FileOutputStream fos;
		try {
			fos = openFileOutput(Global.SELFIE_TEMP, Context.MODE_PRIVATE);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.PNG, 80, stream);
			byte[] byteArray = stream.toByteArray();
			fos.write(byteArray);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == NONE)  
            return;  
        // æ‹ç…§  
        if (requestCode == PHOTOHRAPH) {  
            //è®¾ç½®æ–‡ä»¶ä¿å­˜è·¯å¾„è¿™é‡Œæ”¾åœ¨è·Ÿç›®å½•ä¸‹  
            File picture = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");  
            startPhotoZoom(Uri.fromFile(picture));  
        }  
          
        if (data == null)  
            return;  
          
        // è¯»å–ç›¸å†Œç¼©æ”¾å›¾ç‰‡  
        if (requestCode == PHOTOZOOM) {  
            startPhotoZoom(data.getData());  
        }  
        // å¤„ç†ç»“æžœ  
        if (requestCode == PHOTORESOULT) {  
            Bundle extras = data.getExtras();  
            if (extras != null) {  
                photo = extras.getParcelable("data");    
                //cacheSelfie( photo );
                //updateUI();
                updateSelfie.start();
                
            }  
  
        }  

  
        //super.onActivityResult(requestCode, resultCode, data);  
    }  
  
    public void startPhotoZoom(Uri uri) {  
        Intent intent = new Intent("com.android.camera.action.CROP");  
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);  
        intent.putExtra("crop", "true"); 
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", 1);  
        intent.putExtra("aspectY", 1);  
        intent.putExtra("outputX", 180);  
        intent.putExtra("outputY", 180);  
        intent.putExtra("return-data", true);  
        startActivityForResult(intent, PHOTORESOULT);  
    }  

}
