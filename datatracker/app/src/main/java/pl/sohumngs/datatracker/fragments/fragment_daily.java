package pl.sohumngs.datatracker.fragments;

import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.datatracker.R;

import org.eazegraph.lib.charts.PieChart;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import pl.sohumngs.datatracker.utils.Adapter;
import pl.sohumngs.datatracker.utils.Data;
import pl.sohumngs.datatracker.utils.NetworkStatsHelper;

public class fragment_daily extends Fragment {

    private static fragment_daily instance;
    RecyclerView recyclerView;
    ArrayList<Data> list=new ArrayList<>();
    PieChart pieChart;



    public fragment_daily() {
        // Required empty public constructor
    }

    public static synchronized fragment_daily getInstance() {
        if (instance == null)
            instance = new fragment_daily();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_daily, container, false);

        recyclerView=itemView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pieChart = itemView.findViewById(R.id.piechart);


        list.clear();

        populatedata();

        Adapter adapter=new Adapter(list,getContext(),pieChart,"");
        recyclerView.setAdapter(adapter);

        return itemView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void populatedata() {

        long start,end;
        long wifiRx,wifiTx,mobileRx,mobileTx;

        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getContext().getSystemService(Context.NETWORK_STATS_SERVICE);
        NetworkStatsHelper networkStatsHelper = new NetworkStatsHelper(networkStatsManager);

        end=System.currentTimeMillis();

        for(int i=0;i<7;i++){


            LocalDateTime now = LocalDateTime.now().minusDays(i); // current date and time
            LocalDateTime midnight = now.toLocalDate().atStartOfDay();
            Date d1 = Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());
            start=d1.getTime();

            wifiRx =  networkStatsHelper.getAllRxBytesWifi(start,end);
            wifiTx = networkStatsHelper.getAllTxBytesWifi(start,end);


            mobileRx = networkStatsHelper.getAllRxBytesMobile(getContext(),start,end);
            mobileTx = networkStatsHelper.getAllTxBytesMobile(getContext(),start,end);

            end=start;

            list.add(new Data(round(((wifiRx+wifiTx)*0.000001),1),
                    round(((mobileRx+mobileTx)*0.000001),1),
                    ""+d1.getDate()+"-"+d1.getMonth()+"-"+d1.getYear()));


        }
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}