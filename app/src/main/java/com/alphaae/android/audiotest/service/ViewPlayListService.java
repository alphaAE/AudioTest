package com.alphaae.android.audiotest.service;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.alphaae.android.audiotest.R;
import com.alphaae.android.audiotest.adapter.PlayListAdapter;
import com.alphaae.android.audiotest.base.BaseService;
import com.alphaae.android.audiotest.collector.ServiceCollector;

import java.util.ArrayList;
import java.util.List;

public class ViewPlayListService extends BaseService {

    private AlertDialog alertDialog;

    private static ViewPager viewPager;
    private List<View> pagerViewList = new ArrayList<>();
    public static RecyclerView recyclerPlayList;
    public static PlayListAdapter adapter;

    public static void actionStart(Context mContext) {
        if (ServiceCollector.isFinishing(ViewPlayListService.class)) {
            Intent intent = new Intent(mContext, ViewPlayListService.class);
            mContext.startService(intent);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        View rootView = LayoutInflater.from(this).inflate(R.layout.dialog_playlist, null, false);

        //initRecyclerView
        View playListView = LayoutInflater.from(this).inflate(R.layout.viewpager_playlist, null, false);
        recyclerPlayList = playListView.findViewById(R.id.recycler_playlist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerPlayList.setLayoutManager(linearLayoutManager);
        adapter = new PlayListAdapter(this, PlayAudioService.musicListHelper);
        adapter.setHasStableIds(true);
        recyclerPlayList.setAdapter(adapter);
        pagerViewList.add(playListView);

        //initInfoView
        View tablesView = LayoutInflater.from(this).inflate(R.layout.viewpager_playlisttables, null, false);
        pagerViewList.add(tablesView);

        //initInfoView
        View infoView = LayoutInflater.from(this).inflate(R.layout.viewpager_info, null, false);
        pagerViewList.add(infoView);

        //initViewPager
        viewPager = rootView.findViewById(R.id.viewpager_main);
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return pagerViewList.size();
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = pagerViewList.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }
        };
        viewPager.setAdapter(pagerAdapter);

        //initAlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        builder.setView(rootView);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                Log.i("keyCode", "" + keyCode);
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_MENU) {
                    stopSelf();
                    return true;
                }
                return false;
            }
        });

        alertDialog = builder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        alertDialog.dismiss();
        recyclerPlayList = null;
        adapter = null;
    }


}
