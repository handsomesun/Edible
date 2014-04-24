package com.edible.main;

import com.edible.main.Card;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.client.methods.CloseableHttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.utils.URIBuilder;
import ch.boye.httpclientandroidlib.impl.client.CloseableHttpClient;
import ch.boye.httpclientandroidlib.impl.client.HttpClients;

import com.edible.ocr.R;
import com.edible.ocr.R.id;
import com.edible.ocr.R.layout;

import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DetailInfo extends Activity {

	private TextView chName, enName, background, category;
	private TextView information, memo, editor, lattitude, longitude, date;
	private ImageView picture;
	private ImageView pic;
	private Bitmap bm; 
	private ProgressBar circleProgressBar;
	private Socket _socket = null;
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	private FileOutputStream fos = null;
//	private FileInputStream fis = null;
	private File file = null;
	private byte[] inputBytes = null;
	private Intent intent;
	protected static final int MSG_SUCCESS = 1;  
    protected static final int MSG_FAILURE = 0; 
    private String filePath = "";
    public HttpURLConnection connection = null;

	private HashMap result;
	private byte[] rawImg;
    private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what) {
			case MSG_SUCCESS:
				circleProgressBar.setVisibility(View.INVISIBLE);
				setContentView(R.layout.detail_info);
				chName = (TextView) findViewById(R.id.chName);
				enName = (TextView) findViewById(R.id.enName);
				background = (TextView) findViewById(R.id.background);
				category = (TextView) findViewById(R.id.category);
				information = (TextView) findViewById(R.id.information);
				picture = (ImageView) findViewById(R.id.picture);
				Card card = (Card) result.get("card");
				ArrayList<Long> imgID =  (ArrayList<Long>) result.get("imgID");
				Console dataManager = new Console();
				byte[] rawImg = (byte[]) dataManager.getPhotoById(imgID.get(0)).get("pData");
				if ( card.cn != null)
					chName.setText(card.cn);
				if ( card.en != null)
					enName.setText( card.en);
				if ( card.bg != null)
					background.setText(card.bg);
				if ( card.cat != null)
					category.setText(card.cat);
				if ( card.info != null)
					information.setText(card.info);
				
				Bitmap bMap = BitmapFactory.decodeByteArray(rawImg, 0, rawImg.length);
	            picture.setImageBitmap(bMap);
				//bm = BitmapFactory.decodeFile(filePath);
				//pic.setImageBitmap(bm);
				break;
			case MSG_FAILURE:
				circleProgressBar.setVisibility(View.INVISIBLE);
				Toast.makeText(getApplicationContext(), "Network unavailable", Toast.LENGTH_LONG).show();
				break;
			}
			super.handleMessage(msg);
		}
    	
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_info_before);
        intent = getIntent();
        circleProgressBar = (ProgressBar) findViewById(R.id.circleProgressBar);
        circleProgressBar.setIndeterminate(true);
        circleProgressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					String request = intent.getStringExtra("request");
					Console dataManager = new Console();
					HashMap hm = dataManager.getSingleData(request);
					
					if(hm == null) {
						Toast.makeText(getApplicationContext(), "Discover!", Toast.LENGTH_LONG).show();
						mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
					} else {
						result = hm;
						System.out.println(((Card)hm.get("card")).toString());
						if(hm.get("imgID") == null) 
							Toast.makeText(getApplicationContext(), "No Photos", Toast.LENGTH_LONG).show();
						else {
							System.out.println(hm.get("imgID").toString());
						}
						mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
					}
						
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				
				} finally {
					try {
						if(fos != null)
							fos.close();
						if(ois != null)
		                    ois.close();
						if(oos != null)
							oos.close();
		                if(_socket != null)
		                    _socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();        
    }
    
}
