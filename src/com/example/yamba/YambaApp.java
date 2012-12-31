package com.example.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.TwitterException;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

public class YambaApp extends Application implements OnSharedPreferenceChangeListener{
	static final String TAG = "Yamba App";
	public static final String ACTION_NEW_STATUS="com.example.yamba.NEW_STATUS";
	public static final String ACTION_REFRESH = "com.example.yamba.RefreshService";
	public static final String REFRESH_ALARM = "com.example.yamba.RefreshAlarm";
	private Twitter twitter;
	SharedPreferences prefs;

	@Override
	public void onCreate() {
		super.onCreate();

		// Prefs stuff
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		
		Log.d(TAG, "Created!");
	}
	
	public Twitter getTwitter(){
		if(twitter == null){
			// Prefs stuff
			String username = prefs.getString("username", "");
			String password = prefs.getString("password", "");
			String server = prefs.getString("server", "");

			// Twitter stuff
			twitter = new Twitter(username, password);
			twitter.setAPIRootUrl(server);
		}
		return twitter;
	}

	static final Intent refreshAlarm = new Intent(REFRESH_ALARM);
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		twitter = null;
		sendBroadcast(refreshAlarm);
		Log.d(TAG,"Preference changed for key: " +key);
	}
	
	long lastTimestampSeen = -1;
	public int pullAndInsert(){
		int count = 0;
		long biggestTimestampSeen = -1;
		try {
			List<Status> timeline = getTwitter().getPublicTimeline();
			
			for(Status s : timeline){
				getContentResolver().insert(StatusProvider.CONTENT_URI, StatusProvider.statusToValues(s));
				if(s.createdAt.getTime()>lastTimestampSeen){
					count++;
					biggestTimestampSeen = (s.createdAt.getTime()> biggestTimestampSeen)?s.createdAt.getTime():biggestTimestampSeen;
				}	
				Log.d(TAG, String.format("%s : %s", s.user.name,s.text));
			}
		} catch (TwitterException e) {
			Log.d(TAG,"Failed to pull data from twitter");
			e.printStackTrace();
		}
		if(count > 0)
			sendBroadcast(new Intent(ACTION_NEW_STATUS).putExtra("count", count));
		
		lastTimestampSeen = biggestTimestampSeen;
		return count;
}
}
