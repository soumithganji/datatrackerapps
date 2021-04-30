package pl.sohumngs.datatracker.fragments;

import android.app.DatePickerDialog;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.datatracker.R;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pl.sohumngs.datatracker.utils.NetworkStatsHelper;

public class fragment_custom extends Fragment {

    private static fragment_custom instance;
    View start,end;
    Button select;
    long start_time;
    long end_time;
    long wifiRx,wifiTx,mobileRx,mobileTx;
    TextView wifi,data,starttext,endtext,total;
    PieChart pieChart;
    CardView cardView;


    public fragment_custom() {
        // Required empty public constructor
    }

    public static synchronized fragment_custom getInstance() {
        if (instance == null)
            instance = new fragment_custom();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_custom, container, false);

        start=itemView.findViewById(R.id.startdate);
        end=itemView.findViewById(R.id.enddate);
        wifi=itemView.findViewById(R.id.wifi);
        data=itemView.findViewById(R.id.data);
        starttext=itemView.findViewById(R.id.starttext);
        endtext=itemView.findViewById(R.id.endtext);
        pieChart = itemView.findViewById(R.id.piechart);
        select=itemView.findViewById(R.id.select);
        cardView=itemView.findViewById(R.id.card);
        total=itemView.findViewById(R.id.total);


        final Calendar myCalendar = Calendar.getInstance();
        final Calendar myCalendar_2 = Calendar.getInstance();


        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getContext().getSystemService(Context.NETWORK_STATS_SERVICE);
        final NetworkStatsHelper networkStatsHelper = new NetworkStatsHelper(networkStatsManager);


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                LocalDateTime now = LocalDateTime.of(myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);// current date and time
                LocalDateTime midnight = now.toLocalDate().atStartOfDay();
                Date d1 = Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());
                start_time=d1.getTime();


                starttext.setText(d1.getDate()+"-"+d1.getMonth()+"-"+d1.getYear());

            }

        };



        final DatePickerDialog.OnDateSetListener date_2 = new DatePickerDialog.OnDateSetListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar_2.set(Calendar.YEAR, year);
                myCalendar_2.set(Calendar.MONTH, monthOfYear);
                myCalendar_2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabel();


                LocalDateTime now = LocalDateTime.of(myCalendar_2.get(Calendar.YEAR),myCalendar_2.get(Calendar.MONTH),myCalendar_2.get(Calendar.DAY_OF_MONTH),0,0,0).plusDays(1);// current date and time
                LocalDateTime midnight = now.toLocalDate().atStartOfDay();
                Date d1 = Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());
                end_time=d1.getTime();

                endtext.setText((d1.getDate()-1)+"-"+d1.getMonth()+"-"+d1.getYear());


            }

        };


        start.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(getContext(), date_2, myCalendar_2
                        .get(Calendar.YEAR), myCalendar_2.get(Calendar.MONTH),
                        myCalendar_2.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(start_time!=0&&end_time!=0){


                    Log.d("startend",""+start_time);
                    Log.d("startend",""+end_time);


                    wifiRx =  networkStatsHelper.getAllRxBytesWifi(start_time,end_time);
                    wifiTx = networkStatsHelper.getAllTxBytesWifi(start_time,end_time);

                    mobileRx = networkStatsHelper.getAllRxBytesMobile(getContext(),start_time,end_time);
                    mobileTx = networkStatsHelper.getAllTxBytesMobile(getContext(),start_time,end_time);




                    double a=round(((wifiRx+wifiTx)*0.000001),1);
                    double b=round(((mobileRx+mobileTx)*0.000001),1);

                    wifi.setText(a+" Mb");
                    data.setText(b+" Mb");
                    total.setText(""+round(a+b,1)+" Mb");
                    cardView.setVisibility(View.VISIBLE);


                    pieChart.addPieSlice(
                            new PieModel(
                                    "Wifi",
                                    (int) ((wifiRx+wifiTx)*0.000001),
                                    Color.parseColor("#FFA726")));

                    pieChart.addPieSlice(
                            new PieModel(
                                    "Mobile Data",
                                    (int)((mobileRx+mobileTx)*0.000001),
                                    Color.parseColor("#66BB6A")));

                    pieChart.startAnimation();

                }

            }
        });

        // Inflate the layout for this fragment
        return itemView;
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here

            try {
                starttext.setText("");
                endtext.setText("");
                cardView.setVisibility(View.GONE);
            }catch (Exception e){

            }

        }
    }

}