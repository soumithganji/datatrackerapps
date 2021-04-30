package com.example.appusage.adapter;

import android.app.Activity;
import android.app.usage.NetworkStatsManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.appusage.NetworkStatsHelper;
import com.example.appusage.PackageManagerHelper;
import com.example.appusage.R;
import com.example.appusage.activity.DetailActivity;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bot.box.appusage.model.AppData;
import bot.box.appusage.utils.UsageUtils;

/**
 * Created by Anon on 16,March,2019
 */
public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {


    private List<AppData> mData;
    private WeakReference<Context> mContext;
    Context context1;
    long start,end;
    int period;

    public AppAdapter(Context mContext,Context context1,long start,long end,int period) {
        this.mContext = new WeakReference(mContext);
        this.context1=context1;
        this.start=start;
        this.end=end;
        this.period=period;
    }

    public void updateData(List<AppData> data) {
        mData = data;
        notifyDataSetChanged();
    }

    private AppData getUsageByPosition(int position) {
        if (mData.size() > position) {
            return mData.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_app_usage, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppData item = getUsageByPosition(position);

        holder.mName.setText(item.mName);

        holder.mUsage.setText(UsageUtils.humanReadableMillis(item.mUsageTime));

        holder.mTime.setText(String.format(Locale.getDefault(),
                "%s", "Last Launch " +
                        new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(item.mEventTime))));

        holder.launch_count.setText(item.mCount + " " + mContext.get().getResources().getQuantityString(R.plurals.times_launched, item.mCount));


        Glide.with(this.mContext.get())
                .load(UsageUtils.parsePackageIcon(item.mPackageName, R.mipmap.ic_launcher))
                .transition(new DrawableTransitionOptions().crossFade())
                .into(holder.mIcon);

        holder.parent.setOnClickListener(v -> {

            Intent intent = new Intent(context1, DetailActivity.class);
            intent.putExtra("_packageName", item.mPackageName);
            intent.putExtra("period",period);
            context1.startActivity(intent);

        });


        int uid = PackageManagerHelper.getPackageUid(context1, item.mPackageName);
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) context1.getSystemService(Context.NETWORK_STATS_SERVICE);
        NetworkStatsHelper networkStatsHelper = new NetworkStatsHelper(networkStatsManager, uid);

        long data=networkStatsHelper.getPackageRxBytesMobile(context1,start,end) +
                networkStatsHelper.getPackageRxBytesWifi(start,end)+
                networkStatsHelper.getPackageTxBytesMobile(context1,start,end) +
                networkStatsHelper.getPackageTxBytesWifi(start,end);

        data= (long) (data*0.000001);


        holder.data_used.setText(""+round(data,1));
        holder.data_used.setText(""+data+" Mb");



    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    @Override
    public int getItemCount() {
        if (mData != null)
            return mData.size();
        else return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout parent;
        private TextView mName;
        private TextView mUsage;
        private TextView mTime;
        private ImageView mIcon;
        private TextView launch_count;
        private TextView data_used;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            mName = itemView.findViewById(R.id.app_name);
            mUsage = itemView.findViewById(R.id.app_usage);
            mTime = itemView.findViewById(R.id.app_time);
            mIcon = itemView.findViewById(R.id.app_image);
            launch_count = itemView.findViewById(R.id.launch_count);
            data_used = itemView.findViewById(R.id.data_used);
        }
    }
}
