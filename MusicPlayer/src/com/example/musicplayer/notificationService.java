package com.example.musicplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class notificationService extends Service{

		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return null;
		}
		
		
		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
			notifyMusic(MainActivity.songNameView.getText().toString(),MainActivity.singerNameView.getText().toString());
			return super.onStartCommand(intent, flags, startId);
		}

		@Override
		public void onDestroy() {
			if(MainActivity.nmanager != null) MainActivity.nmanager.cancelAll();
			super.onDestroy();
		}
		
		
		public void notifyMusic(String song,String singer)
		{
		MainActivity.notification = new NotificationCompat.Builder(this);
		MainActivity.notification.setSmallIcon(R.drawable.icon1);
		MainActivity.notification.setContentTitle(song);
		MainActivity.notification.setTicker("Music Player is running");
		MainActivity.notification.setContentText(singer);
		MainActivity.notification.setAutoCancel(false);
//		notification.setDefaults(Notification.DEFAULT_LIGHTS);

			Intent play = new Intent("playMusic");
			Intent next = new Intent("nextMusic");
			Intent prev =  new Intent("prevMusic");
			
			PendingIntent piPlay = PendingIntent.getBroadcast(this, 0, play, 0);
			PendingIntent piNext = PendingIntent.getBroadcast(this, 0, next, 0);
			PendingIntent piPrev = PendingIntent.getBroadcast(this, 0, prev, 0);
			MainActivity.notification.addAction(android.R.drawable.ic_media_previous, "prev", piPrev);
			if(MainActivity.isPlaying)
			{
				MainActivity.notification.addAction(android.R.drawable.ic_media_pause,"pause",piPlay);
			}
			else
			{
				MainActivity.notification.addAction(android.R.drawable.ic_media_play, "play", piPlay);
			}

			MainActivity.notification.addAction(android.R.drawable.ic_media_next,"next",piNext);
		
			MainActivity.nmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			MainActivity.nmanager.notify(1, MainActivity.notification .build());
		}
		
	}
