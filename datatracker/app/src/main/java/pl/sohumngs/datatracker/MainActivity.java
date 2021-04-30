package pl.sohumngs.datatracker;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.example.datatracker.R;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

import pl.sohumngs.datatracker.fragments.fragment_custom;
import pl.sohumngs.datatracker.fragments.fragment_daily;
import pl.sohumngs.datatracker.fragments.fragment_monthly;
import pl.sohumngs.datatracker.fragments.fragment_today;
import pl.sohumngs.datatracker.fragments.fragment_weekly;
import pl.sohumngs.datatracker.utils.NetworkStatsHelper;


public class MainActivity extends AppCompatActivity {

    private static final int READ_PHONE_STATE_REQUEST = 37;
    public static final String EXTRA_PACKAGE = "ExtraPackage";

    TabLayout tabLayout;
    ViewPager viewPager;
    FrameLayout frameLayout;
    public Boolean x;
    ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());

    private String[] tabIcons = {
            "Today",
            "Daily",
            "Weekly",
            "Monthly",
//            "Yearly",
            "Custom"
    };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout=findViewById(R.id.frag_container);
        tabLayout=findViewById(R.id.tab_layout);
        viewPager=(ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);

        viewPagerAdapter.addFragment(new fragment_today());
        viewPagerAdapter.addFragment(new fragment_daily());
        viewPagerAdapter.addFragment(new fragment_weekly());
        viewPagerAdapter.addFragment(new fragment_monthly());
        viewPagerAdapter.addFragment(new fragment_custom());

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#fad1b6"));

        setupTabIcons();

        x=false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestPermissions();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onResume() {

        super.onResume();
        if (!hasPermissions()) {
            return;
        }

        fillData();

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setText(tabIcons[0]);
        tabLayout.getTabAt(1).setText(tabIcons[1]);
        tabLayout.getTabAt(2).setText(tabIcons[2]);
        tabLayout.getTabAt(3).setText(tabIcons[3]);
        tabLayout.getTabAt(4).setText(tabIcons[4]);
//        tabLayout.getTabAt(5).setText(tabIcons[5]);

    }

    private void fillData() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);
            NetworkStatsHelper networkStatsHelper = new NetworkStatsHelper(networkStatsManager);

            //method to get rx/tx of wifi/mobiledata(without dual sim)
            fillNetworkStatsAll(networkStatsHelper);

        }
    }

    //method to get rx/tx of wifi/mobiledata(without dual sim)
    @TargetApi(Build.VERSION_CODES.M)
    private void fillNetworkStatsAll(NetworkStatsHelper networkStatsHelper) {
//        long mobileRx = networkStatsHelper.getAllRxBytesMobile(this);
//        long mobileRx = 0;
//        long wifiRx =  networkStatsHelper.getAllRxBytesWifi();

//        long mobileTx = networkStatsHelper.getAllTxBytesMobile(this);
//        long mobileTx=0;
//        long wifiTx = networkStatsHelper.getAllTxBytesWifi();

//        Toast.makeText(getApplicationContext(),"Wifi :"+((wifiRx+wifiTx)*0.000001)+ "Mb Mobile"+((mobileRx+mobileTx)*0.000001)+"Mb",Toast.LENGTH_SHORT).show();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void subscriberIdsOfDualSim() {
        SubscriptionManager subscriptionManager = SubscriptionManager.from(this);
        //we'll now get a List of information of active SIM cards
        //for example, if there are 2 SIM slots and both the slots have 1 SIM card each, then the List size will be 2
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();

        Log.d("activeSubscriptionInfo",""+activeSubscriptionInfoList.size());


        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
            //loop through the SIM card info
            //if there are 2 SIM cards, then we'll try to print the subscriberId of each of the SIM cards
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                //the following createForSubscriptionId() is available from API 24 :(
                TelephonyManager manager1=manager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                String operatorName=manager1.getNetworkOperatorName();
                String subscriberId=manager1.getSubscriberId(); //use this subscriberId to do NetworkStatsManager stuff
                Log.d("Carrier Name: "+operatorName,", subscriber id: "+subscriptionInfo.getSubscriptionId());
            }
        }

    }


    private boolean hasPermissions() {
        if(hasPermissionToReadNetworkHistory()&&hasPermissionToReadPhoneStats()){
            x=true;
        }
        return hasPermissionToReadNetworkHistory() && hasPermissionToReadPhoneStats();
    }

    public void requestPermissions() {
        if (!hasPermissionToReadNetworkHistory()) {
            return;
        }
        if (!hasPermissionToReadPhoneStats()) {
            requestPhoneStateStats();
        }
    }

    private void requestPhoneStateStats() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_REQUEST);
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