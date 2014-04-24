package com.example.ediblebluechese;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;



public class Client {


	private HttpURLConnection connection = null;

	private OutputStream writer = null;
	private BufferedReader reader = null;

	private static final String SIGNUP_URL = "http://1-dot-edible-bluecheese-server.appspot.com/registration";
	private static final String LOGIN_URL = "http://1-dot-edible-bluecheese-server.appspot.com/login";
	private static final String UPDATE_INFO_URL = "http://1-dot-edible-bluecheese-server.appspot.com/updateprofile";
	private static final String UPDATE_SELFIE_URL = "http://1-dot-edible-bluecheese-server.appspot.com/changeselfie";

	public Client() {
		// TODO Auto-generated constructor stub
	}
	
	public JSONObject signUp( JSONObject usr ) {
		return post( usr, Global.SIGNUP );
	}
	
	public JSONObject login( JSONObject usr ) {
		return post( usr, Global.LOGIN );
	}
	
	public JSONObject updateInfo( JSONObject usr ) {
		return post( usr, Global.UPDATE_INFO  );
	}
	
	public JSONObject updateSelfie( JSONObject usr ) {
		return post( usr, Global.UPDATE_SELFIE  );
	}
	
	public JSONObject post( JSONObject usr, int action ) {
		try {
			URL postUrl;
			switch ( action ) {
			case Global.SIGNUP:
				postUrl = new URL( SIGNUP_URL );
				break;
			case Global.LOGIN:
				postUrl = new URL( LOGIN_URL );
				break;
			case Global.UPDATE_INFO:
				postUrl = new URL( UPDATE_INFO_URL );
				break;
			case Global.UPDATE_SELFIE:
				postUrl = new URL( UPDATE_SELFIE_URL );
				break;
			default:
				postUrl = null;
				break;
			}
				
			connection = (HttpURLConnection) postUrl.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
 			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.connect();
			
			writer = connection.getOutputStream();
			PrintWriter pw = new PrintWriter(writer);
			pw.print(usr);
			pw.flush();
			pw.close();
			writer.close();
//			
//			writer.write(usr.toString().getBytes()); // ?
//			writer.flush();
			
			reader = new BufferedReader( new InputStreamReader( connection.getInputStream()));
			
			StringBuffer sb = new StringBuffer();
			
			String line;
			while ( ( line = reader.readLine() ) != null ) {
				line = new String(line.getBytes(), "utf-8");
				sb.append(line);
			}
			
			JSONObject result = new JSONObject( sb.toString() );
			return result;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if ( writer != null )
					writer.close();
				if ( reader != null )
					reader.close();
				if (connection != null)
					connection.disconnect();
				
//				connection.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}
