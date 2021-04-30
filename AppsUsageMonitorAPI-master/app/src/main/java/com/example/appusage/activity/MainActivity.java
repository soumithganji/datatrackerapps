package com.example.appusage.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.appusage.R;
import com.example.appusage.adapter.AppAdapter;
import com.example.appusage.fragments.fragment_monthly;
import com.example.appusage.fragments.fragment_weekly;
import com.example.appusage.fragments.fragment_today;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import bot.box.appusage.contract.UsageContracts;
import bot.box.appusage.model.AppData;

public class MainActivity extends AppCompatActivity implements UsageContracts.View, AdapterView.OnItemSelectedListener {

    private AppAdapter mAdapter;
    private static final int READ_PHONE_STATE_REQUEST = 37;
    public static final String EXTRA_PACKAGE = "ExtraPackage";
    public Boolean x;

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());

    private String[] tabIcons = {
            "Today",
            "This Week",
            "This Month",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
        }


        tabLayout=findViewById(R.id.tab_layout);
        viewPager=(ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);

        viewPagerAdapter.addFragment(new fragment_today());
        viewPagerAdapter.addFragment(new fragment_weekly());
        viewPagerAdapter.addFragment(new fragment_monthly());

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#fad1b6"));

        setupTabIcons();
        x=false;
    }


    private void setupTabIcons() {
        tabLayout.getTabAt(0).setText(tabIcons[0]);
        tabLayout.getTabAt(1).setText(tabIcons[1]);
        tabLayout.getTabAt(2).setText(tabIcons[2]);


    }

    @Override
    protected void onResume() {

        super.onResume();

        if (!hasPermissions()) {
            return;
        }

//        fillData();

//        if (Monitor.hasUsagePermission()) {
//            Monitor.scan().getAppLists(this,0,System.currentTimeMillis()).fetchFor(Duration.TODAY);
//            init();
//        } else {
//            Monitor.requestUsagePermission();
//        }

    }

//    private void init() {
//        RecyclerView mRecycler = findViewById(R.id.recycler);
//        mAdapter = new AppAdapter(this);
//
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
//        mRecycler.setLayoutManager(mLayoutManager);
//        mRecycler.setAdapter(mAdapter);
//    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        Monitor.scan().getAppLists(this,System.currentTimeMillis(),System.currentTimeMillis()).fetchFor(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }


    @Override
    public void showProgress() {
    }

    @Override
    public void hideProgress() {
    }

    /**
     * @param usageData   list of application that has been within the duration for which query has been made.
     * @param mTotalUsage a sum total of the usage by each and every app with in the request duration.
     * @param duration    the same duration for which query has been made i.e.fetchFor(Duration...)
     */

    @Override
    public void getUsageData(List<AppData> usageData, long mTotalUsage, int duration) {
        mAdapter.updateData(usageData);
    }

    private boolean hasPermissions() {
        if(hasPermissionToReadNetworkHistory()&&hasPermissionToReadPhoneStats()){

            x=true;

        }
        return hasPermissionToReadNetworkHistory() && hasPermissionToReadPhoneStats();
    }

    private boolean hasPermissionToReadPhoneStats() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
            return false;
        } else {
            return true;
        }
    }

    private boolean hasPermissionToReadNetworkHistory() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        final AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }
        appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
                getApplicationContext().getPackageName(),
                new AppOpsManager.OnOpChangedListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onOpChanged(String op, String packageName) {
                        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                                android.os.Process.myUid(), getPackageName());
                        if (mode != AppOpsManager.MODE_ALLOWED) {
                            return;
                        }
                        appOps.stopWatchingMode(this);
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        if (getIntent().getExtras() != null) {
                            intent.putExtras(getIntent().getExtras());
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                });
        requestReadNetworkHistoryAccess();
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void requestReadNetworkHistoryAccess() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Fragment> fragments;
        int position_1;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments=new ArrayList<>();
        };

        @NonNull
        @Override
        public Fragment getItem(int position) {
            position_1=position;
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // Yet another bug in FragmentStatePagerAdapter that destroyItem is called on fragment that hasnt been added. Need to catch
            try {
                super.destroyItem(container, position, object);
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
        }

        public void addFragment(Fragment fragment){
            fragments.add(fragment);
        }

    }

}