package com.example.musicplayer;

import java.util.ArrayList;

import android.R.integer;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import android.os.Bundle;
import android.app.Activity;

public class BackgroundMusicService extends Service{

	MediaPlayer	mp;
	long songId;
	int totalSong ;
	static int songIndex = 0;
	ArrayList<String>  songIdList,songTitle,singerNameList;
	
	Cursor cursor1;
    MediaMetadataRetriever mr=new MediaMetadataRetriever();
    
    String projection[]={MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION
                        };
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;	
	}
	
	@Override
		public void onCreate() {
			
			ContentResolver contentResolver = getContentResolver();
			Uri musicUri =  android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			Cursor cursor = contentResolver.query(musicUri, null, null, null, null);
			songTitle = new ArrayList<String>();
			songIdList= new ArrayList<String>();
			singerNameList = new ArrayList<String>();
			while(cursor.moveToNext())
			{
				songIdList.add(cursor.getString(0));
				songTitle.add(cursor.getString(2));
				singerNameList.add(cursor.getString(25));	
			}
			
			totalSong = songIdList.size();
			Toast.makeText(getApplicationContext(), "Music Archive Updated: " + Integer.toString(totalSong)+" Songs Added", Toast.LENGTH_LONG).show();
			songId = Long.valueOf(songIdList.get(0));
			
			 cursor1= contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);
		        ArrayList<SongListModel> songListModels=new ArrayList<SongListModel>();
		        while(cursor1.moveToNext()){
		          //  song.add(cursor.getString(0));
		           String sPath= cursor1.getString(0);
		            String sName=cursor1.getString(1);
		            songListModels.add(new SongListModel(sPath,sName,cursor1.getString(2)));
		        }
		     
		        SongListAdapter songTitleAdapter=new SongListAdapter(getApplicationContext(),songListModels);
		     	MainActivity.musicListView.setAdapter(songTitleAdapter);
			
			t.start();
			updateleft.start();
			updateright.start();
			
			MainActivity.seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {			
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					if(fromUser)
					{					
					if(mp != null)
					{
						int settime = progress*1000;
						mp.seekTo(settime);
					}				
					}
				}
			});
	//		Toast.makeText(getApplicationContext(), "main m service createded", Toast.LENGTH_SHORT).show();
			super.onCreate();
		}
		
		
		
	
		
		
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		long tempSongId = intent.getLongExtra("songID",-5);
		
		if(tempSongId != -5)
		{
			songId = tempSongId;
		}
		
		String toDo = intent.getStringExtra("toDo");	
		
		if(toDo.equals("playMusic"))
		{	
			play();
		}
		else if(toDo.equals("nextMusic"))
		{
			next();
		}
		else if(toDo.equals("prevMusic"))
		{
			prev();
		}
		else if(toDo.equals("pauseMusic"))
		{
			pause();
		}
		else if(toDo.equals("stopMusic"))
		{
			stop();
		}
		else if(toDo.equals("playMusicFromList"))
		{
			stop();
			play();
		}
		
		
		
		if(MainActivity.nmanager != null)
		{
			
		}
		
		if(MainActivity.isPlaying)
		{
			MainActivity.playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
		}
		else
		{
			MainActivity.playButton.setBackgroundResource(android.R.drawable.ic_media_play);
		}
		
		MainActivity.songNameView.setText(songTitle.get(songIndex));
		MainActivity.singerNameView.setText("By: "+singerNameList.get(songIndex));
		
//		Toast.makeText(getApplicationContext(), "main m service started", Toast.LENGTH_SHORT).show();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	@Override
	public void onDestroy() {
		if(mp != null) mp.stop();
//		Toast.makeText(getApplicationContext(), "main m service destroyed", Toast.LENGTH_SHORT).show();
		t.stop();
		updateleft.stop();
		updateright.stop();
		
//		Toast.makeText(getApplicationContext(),"thread t : "+t.getState().toString() ,Toast.LENGTH_SHORT ).show();
//		Toast.makeText(getApplicationContext(),"thread t : "+updateleft.getState().toString() ,Toast.LENGTH_SHORT ).show();
//		Toast.makeText(getApplicationContext(),"thread t : "+updateright.getState().toString() ,Toast.LENGTH_SHORT ).show();
		super.onDestroy();
	}
	
	
		public void play()
		{
			if(mp == null)
			{	
				songId = Long.valueOf(songIdList.get(songIndex));
				Uri ur = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,songId);
				mp = MediaPlayer.create(getApplicationContext(),ur);
				mp.start();
				MainActivity.isPlaying = true;
			}
			else
			{	if(mp.isPlaying())
				{
					mp.pause();
					MainActivity.isPlaying = false;
				}
				else
				{
					mp.start();
					MainActivity.isPlaying = true;
				}
			}
			
			mp.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					Intent localIntent = new Intent("nextMusic");
					sendBroadcast(localIntent);					
				}
			});
	
			MainActivity.songNameView.setText(songTitle.get(songIndex));
			MainActivity.singerNameView.setText("By: "+singerNameList.get(songIndex));
			MainActivity.seekBar.setMax(mp.getDuration()/1000);
			
		}
		
		public void stop()
		{
			if(mp != null)
			{
				mp.stop();
				MainActivity.isPlaying = false;
				mp=null;
			}
			mp=null;
		}
	
		public void pause()
		{	
			if(mp != null)
			{
				mp.pause();
				MainActivity.isPlaying = false;
			}
			
		}
		
		public void next()
		{
			if(songIndex == totalSong-1)
			{
				songIndex = 0;
			}
			else
			{
				songIndex++;
			}
			stop();
			play();		
		}
		
		public void prev()
		{
				if(songIndex == 0)
				{
					songIndex = totalSong-1;
				}
				else
				{
					songIndex--;
				}
				stop();
				play();		
		}
		
		
		

		Thread updateright = new Thread(new Runnable() {	
			@Override
			public void run() {
				while(true)
				{	
					try
					{   Thread.sleep(1000);
					MainActivity.updateRight.post(new Runnable() {
							@Override
							public void run() {
								if(mp != null)
								{
									int total = mp.getDuration();
									int left = total - mp.getCurrentPosition();							
									String s = String.format("%d : %d : %d",(left/3600000)%60,(left/60000)%60,(left/1000)%60);
									MainActivity.updateRight.setText(s);
								}
								else
								{
									MainActivity.updateRight.setText("00 : 00 : 00");	
								}
								
							}
						});		
					}catch(Exception e){}
				}			
			}
		});

		Thread updateleft = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true)
				{
					try
					{   Thread.sleep(1000);
						MainActivity.updateLeft.post(new Runnable() {
							@Override
							public void run() {
								if(mp != null)
								{
									int current = mp.getCurrentPosition();
									String s = String.format("%d : %d : %d", (current/3600000)%60,(current/60000)%60,(current/1000)%60);
									MainActivity.updateLeft.setText(s);
								}
								else
								{
									MainActivity.updateLeft.setText("00 : 00 : 00");	
								}	
							}
						});		
					}catch(Exception e){}				
				}			
			}
		});
		
		Thread t = new Thread(new Runnable() {		
			@Override
			public void run() {
				while(true)
				{  
					try
					{   Thread.sleep(1000);
						if(mp == null)
						{
							MainActivity.seekBar.post(new Runnable() {				
								@Override
								public void run() {
									MainActivity.seekBar.setProgress(0);
								}
							});
						}
						else
						{					
							MainActivity.seekBar.post(new Runnable() {							
								@Override
								public void run() {
									MainActivity.seekBar.setProgress(mp.getCurrentPosition()/1000);
								}
							});						
						}
					}catch(Exception e){}
				}		
			}
		});
		
}
