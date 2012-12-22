package com.example.yamba;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

public class RefreshAlarmReceiver extends BroadcastReceiver {

	static PendingIntent lastOp;
	@Override
	public void onReceive(Context context, Intent intent) {

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		long interval = Long.parseLong(PreferenceManager
				.getDefaultSharedPreferences(context).getString("delay", "60")) * 1000;

		PendingIntent operation = PendingIntent.getService(context, -1,
				new Intent(YambaApp.ACTION_REFRESH),
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		alarmManager.cancel(lastOp);
		if (interval > 0) {
			alarmManager.setInexactRepeating(AlarmManager.RTC,
					System.currentTimeMillis(), interval, operation);
		}
		
		lastOp = operation;
		Log.d("BootReceiver", "onReceive - delay: " + interval);
	}

}
