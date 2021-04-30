package pl.sohumngs.datatracker.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datatracker.R;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class Adapter extends RecyclerView.Adapter<Adapter.ContactHolder> {

    public ArrayList<Data> contactsList = new ArrayList<>();
    public Context mContext;
    public Context context;
    PieChart pieChart;
    String key;
    DateFormatSymbols dateFormatSymbolsobject = new DateFormatSymbols();
    String[] shortFormatMonthsNames = dateFormatSymbolsobject.getShortMonths();

    // Counstructor for the Class
    public Adapter(ArrayList<Data> contactsList,Context context,PieChart pieChart,String key) {
        this.contactsList = contactsList;
        this.mContext = context;
        this.pieChart=pieChart;
        this.key=key;
    }

    // This method creates views for the RecyclerView by inflating the layout
    // Into the viewHolders which helps to display the items in the RecyclerView
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.item, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public int getItemCount() {
        return contactsList == null ? 0 : contactsList.size();
    }

    // This method is called when binding the data to the views being created in RecyclerView
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {
        final Data contact = contactsList.get(position);


        holder.date.setText(contact.getDate());
        holder.wifi.setText(contact.getWifi()+" Mb");
        holder.mobile.setText(contact.getData()+" Mb");
        holder.total.setText(round(contact.getWifi()+contact.getData(),1)+" Mb");


        LocalDateTime now = LocalDateTime.now().minusWeeks(position).with(DayOfWeek.MONDAY); // current date and time
        LocalDateTime midnight = now.toLocalDate().atStartOfDay();
        Date d1 = Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());

        LocalDateTime now_1 = LocalDateTime.now().minusWeeks(position).with(DayOfWeek.SUNDAY); // current date and time
        LocalDateTime midnight_1 = now_1.toLocalDate().atStartOfDay();
        Date d2 = Date.from(midnight_1.atZone(ZoneId.systemDefault()).toInstant());


        if(position!=0){

            if(key.equals("w")){

                holder.range.setText(d1.getDate() +"-"+shortFormatMonthsNames[d1.getMonth()]+" to "+d2.getDate()+"-"+shortFormatMonthsNames[d2.getMonth()]);

//                Log.d("position",""+position);


            }

        }else{

            if(key.equals("w")){

                holder.range.setText(d1.getDate() +"-"+shortFormatMonthsNames[d1.getMonth()]+" to today");

            }

        }




        holder.linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                pieChart.addPieSlice(
                        new PieModel(
                                "Wifi",
                                (int) ((contact.getWifi()))
                                ,
                                Color.parseColor("#FFA726")));
                pieChart.addPieSlice(
                        new PieModel(
                                "Python",
                                (int)(contact.getData()),
                                Color.parseColor("#66BB6A")));

                pieChart.startAnimation();


            }
        });





    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }


    // This is your ViewHolder class that helps to populate data to the view
    public class ContactHolder extends RecyclerView.ViewHolder {
        private TextView wifi,mobile,date,total,range;
        LinearLayout linearlayout;

        public ContactHolder(View itemView) {
            super(itemView);
            wifi = itemView.findViewById(R.id.wifi);
            mobile=itemView.findViewById(R.id.mobile);
            date=itemView.findViewById(R.id.day);
            linearlayout=itemView.findViewById(R.id.linearlayout);
            total=itemView.findViewById(R.id.total);
            range=itemView.findViewById(R.id.range);
        }

    }
}
