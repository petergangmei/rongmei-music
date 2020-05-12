package com.petergangmei.rongmeimusic.model;

public class Music {
    public Music() {
    }

    private int id;
    private String songId, title, artist, genre, coverUrl, songUrl;

    public Music(int id, String songId, String title, String artist, String genre, String coverUrl, String songUrl) {
        this.id = id;
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.coverUrl = coverUrl;
        this.songUrl = songUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }
}
