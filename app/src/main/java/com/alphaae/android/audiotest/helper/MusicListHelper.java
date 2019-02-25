package com.alphaae.android.audiotest.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.alphaae.android.audiotest.model.Music;
import com.alphaae.android.audiotest.R;
import com.alphaae.android.audiotest.utils.PlayListUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicListHelper {

    private int index = 0;
    private int previousIndex = 0;
    private Context mContext;
    private List<Music> playList = new ArrayList<>();

    public MusicListHelper(Context mContext) {
        this.mContext = mContext;
        PlayListUtils.initmusicList(mContext, playList);
    }

    public int getIndex() {
        return index;
    }

    private void setIndex(int index) {
        this.previousIndex = this.index;
        this.index = index;
    }

    public int getPreviousIndex() {
        return previousIndex;
    }

    public List<Music> getNowMusicListm() {
        return playList;
    }

    public Music getMusic(int index) {
        setIndex(index);
        return playList.get(index);
    }

    public Music firstMusic() {
        if (playList.size() > 0)
            return playList.get(0);
        return null;
    }

    public Music nextMusic() {
        try {
            if (playList.size() > 0) {
                setIndex(getIndex() + 1);
                return playList.get(getIndex());
            }
        } catch (Exception e) {
            setIndex(0);
            return playList.get(getIndex());
        }
        return null;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
//Util

    /**
     * 根据专辑ID获取专辑封面图
     *
     * @param album_id 专辑ID
     * @return
     */
    public Bitmap getAlbumArt(long album_id) {

        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = mContext.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString((int) album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bm = null;
        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art);
        } else {
            bm = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.not_default);
        }
        return bm;
    }

}
