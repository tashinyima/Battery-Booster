package com.receptix.batterybuddy.rank;

/**
 * Created by zero1 on 5/9/2017.
 */

public class RankData {

    public static final int SYSTEM_TYPE =0;
    public static final int INSTALL_TYPE=1;


    String packageName,powerUsage,packageIconUrl,id;
    int type;

    public RankData(int type,String packageName, String powerUsage, String packageIconUrl, String id) {
        this.packageName = packageName;
        this.powerUsage = powerUsage;
        this.packageIconUrl = packageIconUrl;
        this.id = id;
        this.type=type;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getPowerUsage() {
        return powerUsage;
    }

    public String getPackageIconUrl() {
        return packageIconUrl;
    }

    public String getId() {
        return id;
    }
    public int getType(){

        return type;
    }
}
