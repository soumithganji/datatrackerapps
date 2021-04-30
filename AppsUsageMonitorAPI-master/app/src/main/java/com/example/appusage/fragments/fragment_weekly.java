package com.example.appusage.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.appusage.R;
import com.example.appusage.adapter.AppAdapter;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;

import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import bot.box.appusage.contract.UsageContracts;
import bot.box.appusage.handler.Monitor;
import bot.box.appusage.model.AppData;
import bot.box.appusage.utils.Duration;
import bot.box.appusage.utils.UsageUtils;


public class fragment_weekly extends Fragment implements UsageContracts.View, AdapterView.OnItemSelectedListener {

    private static fragment_weekly instance;
    PieChart pieChart;
    private AppAdapter mAdapter;
    RecyclerView mRecycler;
    int maxpack;
    ImageView maximg1,maximg2,maximg3,maximg4;
    TextView maxtext1,maxtext2,maxtext3,maxtext4,apptext,date;
    CardView card;
    BarChart barchart;

    public fragment_weekly() {
        // Required empty public constructor
    }

    public static synchronized fragment_weekly getInstance() {
        if (instance == null)
            instance = new fragment_weekly();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_weekly, container, false);

        mRecycler = itemView.findViewById(R.id.recycler);
        pieChart = itemView.findViewById(R.id.piechart);

        maximg1 = itemView.findViewById(R.id.maximg1);
        maximg2 = itemView.findViewById(R.id.maximg2);
        maximg3 = itemView.findViewById(R.id.maximg3);
        maximg4 = itemView.findViewById(R.id.maximg4);

        maxtext1 = itemView.findViewById(R.id.maxtext1);
        maxtext2 = itemView.findViewById(R.id.maxtext2);
        maxtext3 = itemView.findViewById(R.id.maxtext3);
        maxtext4 = itemView.findViewById(R.id.maxtext4);

        card = itemView.findViewById(R.id.card);
        apptext = itemView.findViewById(R.id.apptext);
        date = itemView.findViewById(R.id.date);
        barchart = itemView.findViewById(R.id.barchart);


        // Inflate the layout for this fragment
        return itemView;
    }


    @SuppressLint("WrongConstant")
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onResume() {
        super.onResume();
        if (Monitor.hasUsagePermission()) {


            barchart.clearChart();
            barchart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
            card.setVisibility(View.GONE);
            apptext.setVisibility(View.GONE);
            date.setVisibility(View.GONE);

            Monitor.scan().getAppLists(this).fetchFor(Duration.WEEK);
            init();
        } else {
            Monitor.requestUsagePermission();
        }

    }

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

    PieModel largest(ArrayList<PieModel> arr) {
        int i;
        PieModel max = arr.get(0);
        for (i = 1; i < arr.size(); i++)
            if (arr.get(i).getValue() > max.getValue()){
                max = arr.get(i);
                maxpack=i;
            }
        return max;
    }

    @Override
    public void getUsageData(List<AppData> usageData, long mTotalUsage, int duration) {
        mAdapter.updateData(usageData);

        barchart.setVisibility(View.VISIBLE);
        Random rnd = new Random();
        ArrayList<PieModel> x=new ArrayList<>();
        int c=0;
        for(AppData data:usageData) {

            if(c<=9) {

                int color = Color.argb(255,
                        rnd.nextInt(256),
                        rnd.nextInt(256),
                        rnd.nextInt(256));


                PieModel p = new PieModel(
                        data.mPackageName,
                        data.mUsageTime,
                        color
                );


                x.add(p);
                pieChart.addPieSlice(p);
                barchart.addBar(new BarModel(data.mUsageTime, color));
                c++;
            }

        }

            PieModel maxp = largest(x);

            String appname = usageData.get(maxpack).mName;
        maxtext1.setText(appname);
        maxtext1.setTextColor(maxp.getColor());
        Glide.with(getContext())
                .load(UsageUtils.parsePackageIcon(usageData.get(maxpack).mPackageName, R.mipmap.ic_launcher))
                .transition(new DrawableTransitionOptions().crossFade())
                .into(maximg1);

        maxtext2.setText(usageData.get(1).mName);
        maxtext2.setTextColor(x.get(1).getColor());
        Glide.with(getContext())
                .load(UsageUtils.parsePackageIcon(usageData.get(1).mPackageName, R.mipmap.ic_launcher))
                .transition(new DrawableTransitionOptions().crossFade())
                .into(maximg2);

        maxtext3.setText(usageData.get(2).mName);
        maxtext3.setTextColor(x.get(2).getColor());
        Glide.with(getContext())
                .load(UsageUtils.parsePackageIcon(usageData.get(2).mPackageName, R.mipmap.ic_launcher))
                .transition(new DrawableTransitionOptions().crossFade())
                .into(maximg3);

        maxtext4.setText(usageData.get(3).mName);
        maxtext4.setTextColor(x.get(3).getColor());
        Glide.with(getContext())
                .load(UsageUtils.parsePackageIcon(usageData.get(3).mPackageName, R.mipmap.ic_launcher))
                .transition(new DrawableTransitionOptions().crossFade())
                .into(maximg4);

            card.setVisibility(View.VISIBLE);
            apptext.setVisibility(View.VISIBLE);
            date.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        long start,end;
        LocalDateTime now = LocalDateTime.now().minusWeeks(0).with(DayOfWeek.MONDAY); // current date and time
        LocalDateTime midnight = now.toLocalDate().atStartOfDay();
        Date d1 = Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());


        LocalDateTime now_1 = LocalDateTime.now(); // current date and time
        LocalDateTime midnight_1 = now_1.toLocalDate().atStartOfDay();
        Date d1_1 = Date.from(midnight_1.atZone(ZoneId.systemDefault()).toInstant());


        DateFormatSymbols dateFormatSymbolsobject = new DateFormatSymbols();
        String[] shortFormatMonthsNames = dateFormatSymbolsobject.getShortMonths();

        date.setText(d1.getDate()+"-"+shortFormatMonthsNames[d1.getMonth()]+" to "
                +d1_1.getDate()+"-"+shortFormatMonthsNames[d1_1.getMonth()]);

        start=d1.getTime();
        end=System.currentTimeMillis();

        mAdapter = new AppAdapter(getContext(),getContext(),start,end, Duration.WEEK);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mAdapter);
    }

}