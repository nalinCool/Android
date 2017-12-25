package com.example.musicplayer;

public class SongListModel {
	String songPath;
    String songName;
    String songDuration;
    public SongListModel(String songPath,String songName,String songDuration){
        this.songPath=songPath;
        this.songName=songName;
        this.songDuration=songDuration;
          }

    public String getSongPath() {
        return songPath;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongDuration() {
        if(songDuration==null || songDuration.equals(""))
        return "0";
        else
        return songDuration;
    }
}
