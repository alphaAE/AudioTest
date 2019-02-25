package com.alphaae.android.audiotest.model;


public class Music {

    private long id;    // 音乐ID
    private String title;   // 音乐标题
    private String artist;  // 艺术家
    private String album;   // 专辑
    private long albumId;   // 专辑ID
    private long duration;  // 时长
    private long size;  // 文件大小
    private String url; // 文件路径

    public Music(long id, String title, String artist, String album, long albumId, long duration, long size, String url) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.albumId = albumId;
        this.duration = duration;
        this.size = size;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public long getAlbumId() {
        return albumId;
    }

    public long getDuration() {
        return duration;
    }

    public long getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }
}
