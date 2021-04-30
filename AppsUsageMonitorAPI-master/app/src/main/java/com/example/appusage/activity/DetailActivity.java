package com.example.appusage.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.appusage.R;
import com.example.appusage.adapter.AppTimeLineAdapter;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import bot.box.appusage.contract.PackageContracts;
import bot.box.appusage.contract.TimelineContracts;
import bot.box.appusage.contract.UsageContracts;
import bot.box.appusage.handler.Monitor;
import bot.box.appusage.model.AppData;
import bot.box.appusage.model.TimeLine;
import bot.box.appusage.utils.Duration;
import bot.box.appusage.utils.UsageUtils;

public class DetailActivity extends AppCompatActivity implements UsageContracts.View, AdapterView.OnItemSelectedListener {
    private static final String PACKAGE_NAME = "_packageName";
    String packageName;
    int duration;
    TextView time;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        packageName = getIntent().getStringExtra(PACKAGE_NAME);
        duration=getIntent().getIntExtra("period",0);


        time=findViewById(R.id.time);

        /**
         * fetching timeline
         */
        AppTimeLineAdapter timeLineAdapter = new AppTimeLineAdapter(getApplicationContext());
        RecyclerView rv = findViewById(R.id.timelineRecyclerView);
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rv.setAdapter(timeLineAdapter);
        Monitor.scan().generateTimeline(new TimelineContracts.View() {
            @Override
            public void onTimelineGenerated(List<List<TimeLine>> timeline) {
                timeLineAdapter.updateData(timeline);
            }
        }).whichPackage(packageName).fetchForToday();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onResume() {
        super.onResume();
        if (Monitor.hasUsagePermission()) {


            Monitor.scan().getAppLists(this).fetchFor(duration);
        } else {
            Monitor.requestUsagePermission();
        }

    }

    @Override
    public void getUsageData(List<AppData> usageData, long mTotalUsage, int duration) {

        for(AppData app:usageData){
            if(app.mPackageName.equals(packageName)){

                ((TextView) findViewById(R.id.name)).setText(app.mName);
                ((TextView) findViewById(R.id.total_times_launched)).setText(app.mCount + " " + getResources().getQuantityString(R.plurals.times_launched, app.mCount));
                ((TextView) findViewById(R.id.data_used)).setText(UsageUtils.humanReadableByteCount(app.mWifi + app.mMobile) + " Consumed");
                ((TextView) findViewById(R.id.last_launched)).setText(String.format(Locale.getDefault(),
                        "%s", "Last Launch " +
                                new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(app.mEventTime))));
                ((TextView) findViewById(R.id.total_usage_time)).
                        setText(UsageUtils.humanReadableMillis(app.mUsageTime));
                Glide.with(DetailActivity.this)
                        .load(UsageUtils.parsePackageIcon(app.mPackageName, R.mipmap.ic_launcher)).
                        transition(new DrawableTransitionOptions().crossFade())
                        .into((ImageView) findViewById(R.id.icon));
                time.setVisibility(View.VISIBLE);


            }
        }

    }
}
