package com.petergangmei.rongmeimusic;

public interface ItemClickInterface {
        void onItemClick(int position,String cplaylist, int  id, String songURL, String songTitle, String songArtist);
        void onTrackActions(String action);

}
