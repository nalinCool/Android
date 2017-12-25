package com.example.musicplayer;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SongListAdapter extends BaseAdapter{
	 private ImageView imageAlbum;
	    private TextView songTitle,songDuration;
	    ArrayList<SongListModel> songListModels;
	    MediaMetadataRetriever mr=new MediaMetadataRetriever();
	    private Context context;
	    private static LayoutInflater layoutInflater;
	    
	    public SongListAdapter(Context context,ArrayList<SongListModel> songListModels){
	        this.context=context;
	       this.songListModels=songListModels;
	        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	    }
	    
	    
	    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		 return songListModels.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		  return songListModels.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 if(convertView==null) {
	            convertView = layoutInflater.inflate(R.layout.activity_custo, null);
	        }
		 imageAlbum= (ImageView) convertView.findViewById(R.id.imgAlbum);
      songTitle= (TextView) convertView.findViewById(R.id.musicTitle);
      songDuration= (TextView) convertView.findViewById(R.id.songDuration);
      mr.setDataSource(songListModels.get(position).getSongPath());
      byte [] imgdata = mr.getEmbeddedPicture();
      if(imgdata!=null) {
          BitmapFactory.Options options=new BitmapFactory.Options();
          options.inPurgeable=true;
          Bitmap bmp = BitmapFactory.decodeByteArray(imgdata, 0, imgdata.length,options);
          imageAlbum.setImageBitmap(bmp);
      }
       else {
          imageAlbum.setImageResource(R.drawable.icon1);
      }
      songTitle.setText(songListModels.get(position).getSongName());

      int i=Integer.parseInt(songListModels.get(position).getSongDuration());
       i=i/1000;
        songDuration.setText((i/60)+":"+(i%60));

		return convertView;
	}
}
