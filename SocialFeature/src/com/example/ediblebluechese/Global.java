package com.example.ediblebluechese;

import java.io.File;

import android.net.Uri;
import android.os.Environment;
//hasdhaskjdashdkjas
public class Global {
	/* POST action enum */
	public static final int SIGNUP = 0;
	public static final int LOGIN = 1;
	public static final int UPDATE_INFO = 2;
	public static final int UPDATE_SELFIE = 3;
	
	/* Intent Keys */
	public static final String PREF_KEY_ID = "id";
	public static final String PREF_KEY_USERNAME = "username";
	public static final String PREF_KEY_JOINTIME = "join_time";
	public static final String PREF_KEY_USERTYPE = "user_type";
	
	/* Shared Preference */
	public static final String PREFS_NAME = "UserData";
	
	/* Handler Messages */
	public static final int MSG_INFO_SUCCESS = 0;
	public static final int MSG_FAILURE = 1;
	public static final int MSG_SELFIE_SUCCESS = 2;
	public static final int MSG_SUCCESS = 3;
	
	/* Temporal Selfie Image */
	public static final String SELFIE_TEMP = "selfie_tmp";
}
