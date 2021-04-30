package pl.sohumngs.datatracker.utils;

public class Data {
    double wifi;
    double data;
    String date;



    public Data(double wifi, double data, String date) {
        this.wifi = wifi;
        this.data = data;
        this.date=date;
    }

    public double getWifi() {
        return wifi;
    }

    public void setWifi(long wifi) {
        this.wifi = wifi;
    }

    public double getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
