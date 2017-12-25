package com.example.musicplayer;

import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	
	 static  TextView songNameView,singerNameView,updateLeft,updateRight;
	 static  ListView musicListView;
	 static  boolean isPlaying=false; 
	 static  NotificationManager nmanager = null;
 	 static  NotificationCompat.Builder notification;
	         ImageButton nextButton,prevButton;
	 static  ImageButton playButton;
	 static  SeekBar seekBar;
	         Intent intent;
	         ToggleButton toggleButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		songNameView   = (TextView) findViewById(R.id.textView2);
		singerNameView = (TextView) findViewById(R.id.textView3);
		updateLeft     = (TextView) findViewById(R.id.textView4);
		updateRight    = (TextView) findViewById(R.id.textView5);
		musicListView  = (ListView) findViewById(R.id.listView1);
	    playButton     = (ImageButton) findViewById(R.id.imageButton1);
		nextButton     = (ImageButton) findViewById(R.id.imageButton3);
		prevButton     = (ImageButton) findViewById(R.id.imageButton2);
		toggleButton   = (ToggleButton) findViewById(R.id.toggleButton1);
		seekBar		   = (SeekBar) findViewById(R.id.seekBar1);
		
			Intent tempIntent = new Intent(getApplicationContext(), BackgroundMusicService.class);
	       tempIntent.putExtra("toDo", "startService");
		   startService(tempIntent);
			
		   intent = new Intent();
			
			playButton.setBackgroundResource(android.R.drawable.ic_media_play);
			musicListView.setBackgroundColor(Color.parseColor("#001100"));
			toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent intentb = new Intent(getApplicationContext(),sensorService.class);
				if(isChecked)
				{
					intentb.putExtra("toDo","startSensor");
				}
				else
				{
					intentb.putExtra("toDo", "stopSensor");
				}
				
				startService(intentb);
			}
		});
		
		playButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent.setAction("playMusic");
				sendBroadcast(intent);
			}
		});
		
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent.setAction("nextMusic");
				sendBroadcast(intent);
			}
		});
		
		prevButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent.setAction("prevMusic");
				sendBroadcast(intent);
			}
		});
		
		musicListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				BackgroundMusicService.songIndex = arg2;
				intent.setAction("playMusicFromList");
				sendBroadcast(intent);
			}
		});
		
		registerForContextMenu(musicListView);
		musicListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				BackgroundMusicService.songIndex = arg2;
				return false;
			}
		});
	}
	
	public void notifyMusic(String song,String singer)
	{
		notification = new NotificationCompat.Builder(this);
		notification.setSmallIcon(R.drawable.icon1);
		notification.setContentTitle(song);
		notification.setContentText(singer);
		notification.setTicker("Music Player is running");
		notification.setAutoCancel(false);
//		notification.setDefaults(Notification.DEFAULT_LIGHTS);

		Intent play = new Intent("playMusic");
		Intent next = new Intent("nextMusic");
		Intent prev =  new Intent("prevMusic");
		
		PendingIntent piPlay = PendingIntent.getBroadcast(this, 0, play, 0);
		PendingIntent piNext = PendingIntent.getBroadcast(this, 0, next, 0);
		PendingIntent piPrev = PendingIntent.getBroadcast(this, 0, prev, 0);
		
		notification.addAction(android.R.drawable.ic_media_previous, "prev", piPrev);
		if(isPlaying)
		{
			notification.addAction(android.R.drawable.ic_media_pause,"pause",piPlay);
		}
		else
		{
			notification.addAction(android.R.drawable.ic_media_play, "play", piPlay);
		}
		notification.addAction(android.R.drawable.ic_media_next,"next",piNext);
	
		nmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nmanager.notify(1, notification.build());
	}
	
	@Override
	protected void onPause() {
	//	Toast.makeText(getApplicationContext(), "main Activity paused", Toast.LENGTH_SHORT).show();
	super.onPause();
	}
	@Override
	protected void onResume() {
	//	Toast.makeText(getApplicationContext(), "main Activity resumed", Toast.LENGTH_SHORT).show();
	super.onResume();
	}
	@Override
	protected void onStop() {
	//	Toast.makeText(getApplicationContext(), "main Activity atopped", Toast.LENGTH_SHORT).show();
	super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		
	//	Toast.makeText(getApplicationContext(), "main Activity destroyed", Toast.LENGTH_SHORT).show();
		if(nmanager != null) nmanager.cancelAll();
		this.stopService(new Intent(getApplicationContext(), BackgroundMusicService.class));
		this.stopService(new Intent(getApplicationContext(), notificationService.class));
		
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		
		if(v.getId() == R.id.listView1) 
			getMenuInflater().inflate(R.menu.optionmenu, menu);
	
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		Intent menuIntent = new Intent();
		
		if(item.getTitle().equals("play"))
		{
			menuIntent.setAction("playMusicFromList");
		}
		else if(item.getTitle().equals("pause"))
		{
			menuIntent.setAction("pauseMusic");
		}
		else if(item.getTitle().equals("stop"))
		{
			menuIntent.setAction("stopMusic");
		}
		
		sendBroadcast(menuIntent);
		
	return super.onContextItemSelected(item);
	}
}

