package com.receptix.batterybuddy.model;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by zero1 on 5/9/2017.
 */

public class RankData {

    public static final int SYSTEM_TYPE =0;
    public static final int INSTALL_TYPE=1;


    String packageName,powerUsage,packageLabel;
    Drawable packageIcon;
    int type;

//    public RankData(int type,String packageName, String powerUsage, String packageIconUrl, String id) {
//        this.packageName = packageName;
//        this.powerUsage = powerUsage;
//        this.packageIconUrl = packageIconUrl;
//        this.id = id;
//        this.type=type;
//    }

    public RankData(String packageName, String powerUsage,Drawable packageIcon, int type,String packageLabel) {
        this.packageName = packageName;
        this.powerUsage = powerUsage;
        this.packageIcon = packageIcon;
        this.type = type;
        this.packageLabel=packageLabel;
    }

    public Drawable getPackageIcon() {

        return packageIcon;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getPowerUsage() {
        return powerUsage;
    }
    public String getPackageLabel(){
        return packageLabel;
    }

    public int getType(){
        return type;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setPowerUsage(String powerUsage) {
        this.powerUsage = powerUsage;
    }

    public void setPackageLabel(String packageLabel) {
        this.packageLabel = packageLabel;
    }

    public void setPackageIcon(Drawable packageIcon) {
        this.packageIcon = packageIcon;
    }

    public void setType(int type) {
        this.type = type;
    }
}
