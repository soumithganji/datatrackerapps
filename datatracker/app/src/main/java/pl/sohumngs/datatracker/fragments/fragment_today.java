package pl.sohumngs.datatracker.fragments;

import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datatracker.R;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import pl.sohumngs.datatracker.MainActivity;
import pl.sohumngs.datatracker.utils.NetworkStatsHelper;


public class fragment_today extends Fragment {

    private static fragment_today instance;
    TextView wifi,mobile,total;
    LinearLayout today;
    PieChart pieChart;

    public fragment_today() {
        // Required empty public constructor
    }

    public static synchronized fragment_today getInstance() {
        if (instance == null)
            instance = new fragment_today();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_today, container, false);

        wifi=itemView.findViewById(R.id.wifi);
        mobile=itemView.findViewById(R.id.mobile);
        today=itemView.findViewById(R.id.today);
        pieChart = itemView.findViewById(R.id.piechart);
        total=itemView.findViewById(R.id.total);



        // Inflate the layout for this fragment
        return itemView;
    }


    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onResume() {
        super.onResume();
        if (!hasPermissions()) {
            return;
        }

        fillData();

    }
    private boolean hasPermissions() {
        MainActivity activity = (MainActivity) getActivity();

        return activity.x;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fillData() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) getContext().getSystemService(Context.NETWORK_STATS_SERVICE);
            NetworkStatsHelper networkStatsHelper = new NetworkStatsHelper(networkStatsManager);

            //method to get rx/tx of wifi/mobiledata(without dual sim)
            fillNetworkStatsAll(networkStatsHelper);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fillNetworkStatsAll(NetworkStatsHelper networkStatsHelper) {
        long start;

        LocalDateTime now = LocalDateTime.now(); // current date and time
        LocalDateTime midnight = now.toLocalDate().atStartOfDay();
        Date d1 = Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());
        start=d1.getTime();


        long wifiRx =  networkStatsHelper.getAllRxBytesWifi(start,System.currentTimeMillis());
        long wifiTx = networkStatsHelper.getAllTxBytesWifi(start,System.currentTimeMillis());


        long mobileRx = networkStatsHelper.getAllRxBytesMobile(getContext(),start,System.currentTimeMillis());
        long mobileTx = networkStatsHelper.getAllTxBytesMobile(getContext(),start,System.currentTimeMillis());

        double a=round(((wifiRx+wifiTx)*0.000001),1);
        double b=round(((mobileRx+mobileTx)*0.000001),1);

        wifi.setText(a+" Mb");
        mobile.setText(b+" Mb");
        total.setText(""+round(a+b,1)+" Mb");



        pieChart.addPieSlice(
                new PieModel(
                        "Wifi",
                        (int) ((wifiRx+wifiTx)*0.000001)
                        ,
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "Python",
                        (int)((mobileRx+mobileTx)*0.000001),
                        Color.parseColor("#66BB6A")));

        pieChart.startAnimation();

//        Toast.makeText(getContext(),"Wifi :"+((wifiRx+wifiTx)*0.000001)+ "Mb Mobile"+((mobileRx+mobileTx)*0.000001)+"Mb",Toast.LENGTH_SHORT).show();

    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

}