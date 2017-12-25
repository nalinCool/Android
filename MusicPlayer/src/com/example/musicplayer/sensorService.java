package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.widget.Toast;

public class sensorService  extends Service implements SensorEventListener{
	SensorManager sensorManager ;
	Sensor acceleroSensor ;
	int countRight = 1,countLeft=1;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
			String action = intent.getStringExtra("toDo");
			if(action.equals("startSensor"))
			{
				sensorManager =  (SensorManager) getSystemService(SENSOR_SERVICE);
				acceleroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);	
				if(acceleroSensor == null)
				{
					Toast.makeText(getApplicationContext(), "sensor not found",	Toast.LENGTH_SHORT ).show();
					stopSelf();
				}
				else
				{
				sensorManager.registerListener(this, acceleroSensor,SensorManager.SENSOR_DELAY_NORMAL);}
				Toast.makeText(getApplicationContext(), "Enjoy the world of 6th Sense",	Toast.LENGTH_SHORT ).show();
			}
			else if(action.equals("stopSensor"))
			{	
				if(sensorManager != null)
				sensorManager.unregisterListener(this);
				stopSelf();
				Toast.makeText(getApplicationContext(), "Your way Man!!!",	Toast.LENGTH_SHORT ).show();
			}
			
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float sensedValues[] = event.values;
		float upDown = sensedValues[1];
		volChanger(upDown);
		float leftRight = sensedValues[0];
		if(leftRight > -1  ) countRight=1;
		if(leftRight < 1) countLeft=1;
		if(leftRight < -8)
		{
			
			if(countRight == 1)
				mediaChanger(leftRight);
		}
		if(leftRight > 8)
		{
			if(countLeft == 1)
				mediaChanger(leftRight);
		}
		
		
	}
	
	public void volChanger(float val )
	{
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		if(val > 9)
		{
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,15 ,0);
		}
		else if(val > 7)
		{
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,13 ,0);
		}
		else if(val > 4)
		{
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,10 ,0);
		}
		else if(val > 2)
		{
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,9 ,0);
		}
		else if(val > 0)
		{
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,8 ,0);
		}
		else if(val < -2)
		{
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,6 ,0);
		}
		else if(val < -4)
		{
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,4 ,0);
		}
		else if(val < -7 )
		{
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,2 ,0);
		}
		else if(val > -9)
		{
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0 ,0);
		}
	}
	public void mediaChanger(float val)
	{
		if(val > 8)
		{
			Intent i =new Intent("prevMusic");
			i.putExtra("toDo", "prevMusic");
			sendBroadcast(i);
			countLeft=0;
		}
		else if(val < -8)
		{
			Intent i =new Intent("nextMusic");
			i.putExtra("toDo", "nextMusic");
			sendBroadcast(i);
			countRight=0;
		}
	}
}
