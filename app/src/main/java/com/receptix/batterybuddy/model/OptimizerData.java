package com.receptix.batterybuddy.model;

import android.graphics.drawable.Drawable;

/**
 * Created by hello on 5/15/2017.
 */

public class OptimizerData {

    Drawable drawableIcon;
    String packageName;
    String packageLable;

    public OptimizerData(Drawable drawableIcon, String packageName, String packageLable) {
        this.drawableIcon = drawableIcon;
        this.packageName = packageName;
        this.packageLable = packageLable;
    }

    public Drawable getDrawableIcon() {

        return drawableIcon;
    }

    public void setDrawableIcon(Drawable drawableIcon) {

        this.drawableIcon = drawableIcon;
    }

    public String getPackageName() {


        return packageName;
    }

    public void setPackageName(String packageName) {

        this.packageName = packageName;
    }

    public String getPackageLable() {

        return packageLable;
    }

    public void setPackageLable(String packageLable) {

        this.packageLable = packageLable;
    }
}
