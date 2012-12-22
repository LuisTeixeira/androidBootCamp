package com.example.yamba;

import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener{
	
	Button btnUpdate;
	EditText editStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Debug.startMethodTracing("Yamba.trace");
		
		setContentView(R.layout.status);
		
		btnUpdate = (Button) findViewById(R.id.btnUpdate);
		editStatus = (EditText) findViewById(R.id.editStatus);
		
		btnUpdate.setOnClickListener(this);
	}

	
	

	@Override
	protected void onStop(){
		super.onStop();
		
		//Debug.stopMethodTracing();
	}
	
	@Override
	public void onClick(View v) {
		String text = editStatus.getText().toString();
		new PostToTwitter().execute(text);
	}
	
	class PostToTwitter extends AsyncTask<String,Void,String>{

		@Override
		protected String doInBackground(String... arg0) {
			try {
				((YambaApp) getApplication()).getTwitter().setStatus(arg0[0]);
				Log.d("Status updated", ("Successfuly posted: "+ arg0[0]));
				return "Successfuly posted: "+ arg0[0];
			} catch (TwitterException e) {
				Log.e("Update error", "Failed to post");
				return "Failed to post: "+ arg0[0];
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_SHORT).show();
		}
		
		
	}

}
