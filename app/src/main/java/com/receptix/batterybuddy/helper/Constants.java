package com.receptix.batterybuddy.helper;

/**
 * Created by futech on 5/17/2017.
 */

public class Constants {


    public static class Preferences {
        public static final String PREFER_NAME = "AndroidExamplePref";
        public static final String IS_OPTIMIZED_NOW = "is_optimized";
        public static final String IS_FIRST_TIME = "is_first_time";
        // Shared pref mode (Context.MODE_PRIVATE)
        public static final int PRIVATE_MODE = 0;
        public static final String PREFERENCES_IS_ACTIVE = "pref_is_active";
        public static final String IS_ACTIVE = "is_active";
        public static final String IS_ONE_DAY_FINISHED="is_one_day_finished";
        public static final String LAST_TIMESTAMP_LOCK_ADS = "lastTimestamp";
        public static final String LAST_TIMESTAMP_LOCK_ADS_POWER_CONNECTION_RECEIVER = "lastTimestampPowerConnectionReceiver";
    }


    public static class BatteryParams {
        public static final String BATTERY_LEVEL = "level";
        public static final String BATTERY_SCALE = "scale";
        public static final String IS_BATTERY_PRESENT = "present";
        public static final String BATTERY_VOLTAGE = "voltage";
        public static final String BATTERY_TECHNOLOGY = "technology";
        public static final String BATTERY_TEMPERATURE = "temperature";
        public static final double BATTERY_VOLTAGE_CONVERSION_UNIT = 0.001;
        public static final double BATTERY_TEMPERATURE_CONVERSION_UNIT = 0.1;
    }

    public static class Tags {
        public static final String TAG_HOME_FRAGMENT = "HomeFragment";
    }

    public static class PowerProfileParams {
        public static final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
        public static final String METHOD_GET_AVERAGE_POWER = "getAveragePower";
        public static final String BATTERY_CAPACITY = "battery.capacity";

        public static final String RADIO_ACTIVE = "radio.active";
        public static final String RADIO_SCANNING = "radio.scanning";
        public static final String RADIO_ON = "radio.on";

        public static final String CPU_AWAKE = "cpu.awake";
        public static final String CPU_ACTIVE = "cpu.active";
        public static final String CPU_IDLE = "cpu.idle";

        public static final String WIFI_ON = "wifi.on";
        public static final String WIFI_ACTIVE = "wifi.active";
        public static final String WIFI_SCANNING = "wifi.scan";

        public static final String GPS_ON = "gps.on";

        public static final String SCREEN_ON = "screen.on";
        public static final String SCREEN_FULL = "screen.full";

        public static final String DSP_AUDIO = "dsp.audio";
        public static final String DSP_VIDEO = "dsp.video";

    }

    public static class BrightnessLevel {
        public static final int BRIGHTNESS_LEVEL_0 = 0;
        public static final int BRIGHTNESS_LEVEL_1 = 1;
        public static final int BRIGHTNESS_LEVEL_2 = 2;
        public static final int BRIGHTNESS_LEVEL_3 = 3;
        public static final int BRIGHTNESS_LEVEL_4 = 4;
        public static final int BRIGHTNESS_LEVEL_5 = 5;
        public static final int BRIGHTNESS_LEVEL_6 = 6;
        public static final int BRIGHTNESS_LEVEL_7 = 7;
        public static final int BRIGHTNESS_LEVEL_8 = 8;
        public static final int BRIGHTNESS_LEVEL_9 = 9;
    }

    public static class BrightnessLevelValues {
        public static final int BRIGHTNESS_DEFAULT_VALUE = 20;
        public static final int BRIGHTNESS_LEVEL_0_UPPER_LIMIT = 26;
        public static final int BRIGHTNESS_LEVEL_1_UPPER_LIMIT = 51;
        public static final int BRIGHTNESS_LEVEL_2_UPPER_LIMIT = 77;
        public static final int BRIGHTNESS_LEVEL_3_UPPER_LIMIT = 102;
        public static final int BRIGHTNESS_LEVEL_4_UPPER_LIMIT = 128;
        public static final int BRIGHTNESS_LEVEL_5_UPPER_LIMIT = 153;
        public static final int BRIGHTNESS_LEVEL_6_UPPER_LIMIT = 179;
        public static final int BRIGHTNESS_LEVEL_7_UPPER_LIMIT = 204;
        public static final int BRIGHTNESS_LEVEL_8_UPPER_LIMIT = 230;
        public static final int BRIGHTNESS_LEVEL_9_UPPER_LIMIT = 255;
    }


    public static class SoundModes {
        public static final int SILENT_MODE = 0;
        public static final int RINGTONE_MODE = 1;
        public static final int VIBRATE_MODE = 2;
    }

    public static class StatisticTypes {
        public static final int OVERALL_BATTERY = 0;
        public static final int VOICE_CALL = 1;
        public static final int VIDEO = 2;
        public static final int WIFI = 3;
    }

    public static class ShortHandNotations {
        public static final String HOURS = "h";
        public static final String MINUTES = "min";
    }

    public static class LockScreenTimeout {
        public static final int TIMEOUT_AUTO_LOCK = 0;
        public static final int TIMEOUT_5_SECONDS = 1;
        public static final int TIMEOUT_15_SECONDS = 2;
        public static final int TIMEOUT_30_SECONDS = 3;

        public static final int TIMEOUT_1_MINUTE = 4;
        public static final int TIMEOUT_2_MINUTE = 5;
        public static final int TIMEOUT_5_MINUTES = 6;
        public static final int TIMEOUT_10_MINUTES = 7;
        public static final int TIMEOUT_30_MINUTES = 8;
    }

    public static class Params {
        public static final int INVALID_POSITION = -1;
        public static final int MINIMUM_INSTALLED_APPS = 10;
        public static final int NUMBER_OF_SYSTEM_APPS_TO_SHOW = 12;
        public static final int COUNTDOWN_TIMER_VALUE = 1800000; //30 minutes before we show ISSUES with the phone again (to Optimize)
        public static final int SCREEN_LOCK_ADS_TIMER_VALUE_MINUTES = 1440; //24 hours have 1440 minutes
        public static final String APP_PACKAGE_NAME = "com.receptix.batterybuddy";

        public static final String FROM = "from";
        public static final String IS_SCREEN_ON = "isScreenOn";
        public static final String BROADCAST_RECEIVER = "broadcast";

        public static final String INTENT_ACTION_UPDATE_WIDGET_BATTERY_SERVICE = "INTENT_ACTION_UPDATE_WIDGET_BATTERY_SERVICE";

        public static final String USED_RAM_PERCENTAGE = "usedRamPercentage";
        public static final String STATUS_SUCCESS ="success";

        //install referrer pixel paramters
        public static final String REFERRER = "referrer";
        public static final String APP_NAME = "package";
        public static final String REFERRER_JSON_OBJECT = "RequestBody" ;

    }


    public static class DateFormats
    {
        //Date-Month-Year Hour:Minutes am/pm
        public static final String FORMAT_DATE_MONTH_YEAR_HOUR_MINUTES = "dd-MMM-yyyy hh:mm a";
        public static final String FORMAT_FULL_LENGTH_DAY = "EEEE";
    }

    public static class CurrentValues
    {
        public static final int CURRENT_RATE_USB = 500;
        public static final int CURRENT_RATE_AC = 1000;
    }

    public static class JsonProperties {
        public static final String DEVICE_INFO = "deviceInfo";
        public static final String DEVICE_ID = "deviceId";
        public static final String IP_ADDRESS = "ipaddress";
        public static final String MAC_ADDRESS = "macaddress";
        public static final String WLAN = "wlan0";
        public static final String ETHERNET = "eth0";
        public static final String DEFAULT_LAUNCHER = "launcher";
        public static final String LOCATION = "location";
        public static final String COUNTRY = "country";
        public static final String EMAILS = "emails";
        public static final String INSTALLED_APPS = "installedapps";
        public static final String REQUEST_OBJECT = "requestObject";
        public static final String RESPONSE_OBJECT = "responseObject";
    }

    public static class Urls
    {
        public static final String URL_OZOCK = "http://www.ozock.com/";
        public static final String URL_TRACKING_OZOCK = "http://192.99.150.33/serve/android/install.php";
        public static final String URL_EMAIL_ADDRESS_SUPPORT = "akshit@zero1.io";
    }

    public static class APPS_FLYER_ATTRIBUTES
    {
        public static final String INSTALL_TYPE = "af_status";
        public static final String MEDIA_SOURCE = "media_source";
        public static final String INSTALL_TIME = "install_time";
        public static final String CLICK_TIME = "click_time";
        public static final String INSTALL_CAMPAIGN = "campaign";
        public static final String AGENCY = "agency";
        public static final String CLICK_ID="clickid";
        public static final String CHANNEL="af_channel";
    }

    public static class BannerPlacementIds
    {
        public static final String INMOBI_ACCOUNT_ID = "4a38c3c40747428fa346cb0456d9034f";
        public static final String LOCK_ADS_PLACEMENT_ID = "1496191677809";
    }

    public static class MoPubAdIds
    {
        public static final String LOCK_ADS_ID = "9868b50964e34c6faa88914bc7c91d05";
    }

}
