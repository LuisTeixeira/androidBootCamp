package com.example.yamba;


import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class TimelineActivity extends ListActivity{
	static final String TAG = "TimelineActivity";
	static final String [] FROM = {StatusData.C_USER,StatusData.C_TEXT,StatusData.C_CREATED_AT};
	static final int [] TO = {R.id.textUser,R.id.textText,R.id.textCreatedAt};
	Cursor cursor;
	SimpleCursorAdapter adapter; 
	TimelineReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(R.string.timeline);
		cursor = ((YambaApp)getApplication()).statusData.query();
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);
		setListAdapter(adapter);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(receiver == null)receiver = new TimelineReceiver();
		registerReceiver(receiver, new IntentFilter(YambaApp.ACTION_NEW_STATUS));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}


	static final ViewBinder VIEW_BINDER = new ViewBinder(){

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if(view.getId() != R.id.textCreatedAt)
				return false;
			
			long time = cursor.getLong(cursor.getColumnIndex(StatusData.C_CREATED_AT));
			CharSequence text = DateUtils.getRelativeTimeSpanString(time);
			((TextView) view).setText(text);
			return true;
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent updateIntent = new Intent(this,UpdateService.class);
		Intent refreshIntent = new Intent(this,RefreshService.class);
		Intent prefsIntent = new Intent(this,PrefsActivity.class);
		Intent statusIntent = new Intent(this,StatusActivity.class);
		
		switch(item.getItemId()){
		case R.id.item_start_service:
			startService(updateIntent);
			return true;
		case R.id.item_stop_service:
			stopService(updateIntent);
			return true;
		case R.id.item_refresh_service:
			startService(refreshIntent);
			return true;
		case R.id.item_prefs:
			startActivity(prefsIntent);
			return true;
		case R.id.item_status_update:
			startActivity(statusIntent);
			return true;
		default:
			return false;
		}
	}
	
	class TimelineReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			cursor = ((YambaApp)getApplication()).statusData.query();
			adapter.changeCursor(cursor);
			int count = intent.getIntExtra("count", 0);
			String result = (count > 1) ? String.format("You have %d new tweets", count) : String.format("You have %d new tweet", count);
			Toast.makeText(TimelineActivity.this, result, Toast.LENGTH_LONG).show();
			Log.d(TAG,"Timeline Receiver");
		}
		
	}
}
