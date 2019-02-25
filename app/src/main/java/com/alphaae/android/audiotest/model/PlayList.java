package com.alphaae.android.audiotest.model;

import java.util.Date;
import java.util.List;

public class PlayList {

    private int id;
    private String listName;
    private Date createDate;
    private List<Music> playList;

    public PlayList(String listName, Date createDate, List<Music> playList) {
        this.listName = listName;
        this.createDate = createDate;
        this.playList = playList;
    }

    public String getListName() {
        return listName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public List<Music> getPlayList() {
        return playList;
    }
}
