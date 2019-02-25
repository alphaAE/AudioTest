package com.alphaae.android.audiotest.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaae.android.audiotest.model.Music;
import com.alphaae.android.audiotest.R;
import com.alphaae.android.audiotest.helper.MusicListHelper;
import com.alphaae.android.audiotest.service.PlayAudioService;

import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.mHolder> {

    private Context mContext;
    private List<Music> musicList;
    MusicListHelper musicListHelper;

    public PlayListAdapter(Context mContext, MusicListHelper musicListHelper) {
        this.mContext = mContext;
        this.musicList = musicListHelper.getNowMusicListm();
        this.musicListHelper = musicListHelper;
    }

    @NonNull
    @Override
    public mHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_playlist, viewGroup, false);
        final mHolder holder = new mHolder(view);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                PlayAudioService.intentAction(mContext, PlayAudioService.EXTRA_TO, pos);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull mHolder mHolder, int i) {
        if (i == musicListHelper.getIndex()) {
            mHolder.imgSelected.setVisibility(View.VISIBLE);
        } else {
            mHolder.imgSelected.setVisibility(View.GONE);
        }
        mHolder.textTitle.setText(musicList.get(i).getTitle());
        mHolder.textArtist.setText("- " + musicList.get(i).getArtist());
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    @Override
    public long getItemId(int position) {
        return musicList.get(position).getId();
    }

    class mHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        ImageView imgSelected;
        TextView textTitle;
        TextView textArtist;

        public mHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            imgSelected = itemView.findViewById(R.id.img_selected);
            textTitle = itemView.findViewById(R.id.text_title);
            textArtist = itemView.findViewById(R.id.text_artist);
        }
    }
}
