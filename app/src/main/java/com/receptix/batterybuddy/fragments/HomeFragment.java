package com.receptix.batterybuddy.fragments;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiNative;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.activities.OptimalStateActivity;
import com.receptix.batterybuddy.activities.OptimizerActivity;
import com.receptix.batterybuddy.databinding.FragmentHomeBinding;
import com.receptix.batterybuddy.helper.Constants;
import com.receptix.batterybuddy.helper.LogUtil;
import com.receptix.batterybuddy.helper.UserSessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_LEVEL;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_SCALE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_TECHNOLOGY;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_TEMPERATURE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_TEMPERATURE_CONVERSION_UNIT;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_VOLTAGE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_VOLTAGE_CONVERSION_UNIT;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.IS_BATTERY_PRESENT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_0;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_1;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_2;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_3;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_4;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_5;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_6;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_7;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_8;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_9;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_DEFAULT_VALUE;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_0_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_1_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_2_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_3_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_4_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_5_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_6_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_7_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_8_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_9_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_10_MINUTES;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_15_SECONDS;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_1_MINUTE;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_2_MINUTE;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_30_MINUTES;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_30_SECONDS;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_5_MINUTES;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_5_SECONDS;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_AUTO_LOCK;
import static com.receptix.batterybuddy.helper.Constants.NativeAdContentJson.AD_CALL_TO_ACTION;
import static com.receptix.batterybuddy.helper.Constants.NativeAdContentJson.AD_DESCRIPTION;
import static com.receptix.batterybuddy.helper.Constants.NativeAdContentJson.AD_ICON;
import static com.receptix.batterybuddy.helper.Constants.NativeAdContentJson.AD_IMAGE_URL;
import static com.receptix.batterybuddy.helper.Constants.NativeAdContentJson.AD_LANDING_URL;
import static com.receptix.batterybuddy.helper.Constants.NativeAdContentJson.AD_RATING;
import static com.receptix.batterybuddy.helper.Constants.NativeAdContentJson.AD_TITLE;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.BATTERY_CAPACITY;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.CPU_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.CPU_AWAKE;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.CPU_IDLE;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.DSP_AUDIO;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.DSP_VIDEO;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.METHOD_GET_AVERAGE_POWER;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.POWER_PROFILE_CLASS;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.RADIO_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.RADIO_ON;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.RADIO_SCANNING;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.SCREEN_ON;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.WIFI_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.WIFI_ON;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.WIFI_SCANNING;
import static com.receptix.batterybuddy.helper.Constants.ShortHandNotations.HOURS;
import static com.receptix.batterybuddy.helper.Constants.ShortHandNotations.MINUTES;
import static com.receptix.batterybuddy.helper.Constants.SoundModes.RINGTONE_MODE;
import static com.receptix.batterybuddy.helper.Constants.SoundModes.SILENT_MODE;
import static com.receptix.batterybuddy.helper.Constants.SoundModes.VIBRATE_MODE;
import static com.receptix.batterybuddy.helper.Constants.StatisticTypes.OVERALL_BATTERY;
import static com.receptix.batterybuddy.helper.Constants.StatisticTypes.VIDEO;
import static com.receptix.batterybuddy.helper.Constants.StatisticTypes.VOICE_CALL;
import static com.receptix.batterybuddy.helper.Constants.StatisticTypes.WIFI;
import static com.receptix.batterybuddy.helper.Constants.Tags.TAG_HOME_FRAGMENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final int DELAY = 3000;
    View view;
    Context context;
    Toolbar toolbar;
    String batteryTechnology;
    int batteryVoltage, batteryTemperature, batteryLevel, batteryScale;
    int lockScreenTimeOut = 0;
    int inWhichBrightness = 0;
    int WhatisLockScreenTimeOut = 0;
    int isBluetoothOn; // 1 = if Bluetooth OFF, 0 otherwise

    BluetoothAdapter bluetoothAdapter;
    AudioManager audioManagerMode = null;
    NotificationManager notificationManager;

    int audioCurrentMode = 0;
    int inWhichSoundIndex;

    BatteryManager myBatteryManger;
    UserSessionManager userSessionManager;
    PendingIntent pendingIntent;
    Intent intent;
    int NOTIFICATION_ID = 999;
    FragmentHomeBinding homeBinding;
    private int mProgressStatus = 0;
    private int screenBrightness = 0;
    private BroadcastReceiver battery_info_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isBatteryPresent = intent.getBooleanExtra(IS_BATTERY_PRESENT, false);
            if (isBatteryPresent) {
                batteryTechnology = intent.getStringExtra(BATTERY_TECHNOLOGY);
                batteryVoltage = intent.getIntExtra(BATTERY_VOLTAGE, 0);
                batteryTemperature = intent.getIntExtra(BATTERY_TEMPERATURE, 0);
                batteryLevel = intent.getIntExtra(BATTERY_LEVEL, 0);
                batteryScale = intent.getIntExtra(BATTERY_SCALE, 0);
                double voltageValueInDouble = batteryVoltage * BATTERY_VOLTAGE_CONVERSION_UNIT;
                double temperatureValueInDouble = batteryTemperature * BATTERY_TEMPERATURE_CONVERSION_UNIT;

                // Calculate the battery charged percentage
                float batteryChargedPercentage = batteryLevel / (float) batteryScale;
                int totalBatteryCapacityInMAh = (int) getBatteryCapacity();
                int remainingBatteryCapacityInMah = batteryLevel * totalBatteryCapacityInMAh / 100;

                // ETA FOR SCREEN ON
                double screenPowerConsumption = getPowerConsumption_Total();
                double timeLeft_ScreenOn = remainingBatteryCapacityInMah / screenPowerConsumption;
                getValueInHoursMinutes(timeLeft_ScreenOn, OVERALL_BATTERY);

                // ETA FOR WIFI CONSUMPTION
                double wifiPowerConsumption = getPowerConsumption_Wifi();
                double timeLeft_Wifi = remainingBatteryCapacityInMah / wifiPowerConsumption;
                getValueInHoursMinutes(timeLeft_Wifi, WIFI);

                // ETA FOR VOICE CALLS
                double voiceCallConsumption = getPowerConsumption_VoiceCalls();
                double timeLeft_VoiceCall = remainingBatteryCapacityInMah / voiceCallConsumption;
                getValueInHoursMinutes(timeLeft_VoiceCall, VOICE_CALL);

                // ETA FOR VIDEO WATCHING
                double videoPowerConsumption = getPowerConsumption_Videos();
                double timeLeft_Video = remainingBatteryCapacityInMah / videoPowerConsumption;
                getValueInHoursMinutes(timeLeft_Video, VIDEO);

                // Update the progress bar to display current battery charged percentage
                mProgressStatus = (int) ((batteryChargedPercentage) * 100);

                homeBinding.batteryChargingDonutProgressBar.setProgress(mProgressStatus);

                // SET BATTERY VOLTAGE
                homeBinding.textviewBatteryVoltage.setText(String.format("%.1f", voltageValueInDouble) + "V");
                // SET BATTERY TEMPERATURE
                homeBinding.textviewBatteryTemperature.setText(String.format("%.1f", temperatureValueInDouble) + (char) 0x00b0 + "C");
                // SET BATTERY TECHNOLOGY (Li-ion etc.)
                homeBinding.textviewBatteryTechnology.setText(batteryTechnology);

                Bundle bundle = intent.getExtras();
                LogUtil.d(TAG_HOME_FRAGMENT, bundle.toString());
                LogUtil.d(TAG_HOME_FRAGMENT, "Technology=" + batteryTechnology + "batteryVoltage=" + batteryVoltage + "Temperature=" + batteryTemperature);
            } else {
                showMessage(getString(R.string.no_battery_present));
            }

        }
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        homeBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_home, container, false);
        view = homeBinding.getRoot();

        // NATIVE AD 1
        final RelativeLayout nativeAdLayout = (RelativeLayout) homeBinding.nativeAd;
        final ImageView imageView_adIcon = (ImageView) nativeAdLayout.findViewById(R.id.native_ad_icon_imageview);
        final TextView textView_adTitle = (TextView) nativeAdLayout.findViewById(R.id.native_ad_title_textview);
        final MaterialRatingBar ratingBar = (MaterialRatingBar) nativeAdLayout.findViewById(R.id.native_ad_rating_bar);
        final Button button_callToAction = (Button) nativeAdLayout.findViewById(R.id.call_to_action_button);
        nativeAdLayout.setVisibility(View.GONE);

        final Long placementID = 1497109684434L;
        InMobiNative nativeAd = new InMobiNative(getActivity(), placementID, new InMobiNative.NativeAdListener() {
            @Override
            public void onAdLoadSucceeded(InMobiNative inMobiNative) {
                Log.e(TAG_HOME_FRAGMENT,  "onAdLoadSucceeded =>" + placementID);
                JSONObject content = null;
                try {
                    content = new JSONObject((String) inMobiNative.getAdContent());
                    if(content!=null)
                    {
                        nativeAdLayout.setVisibility(View.VISIBLE);
                        Log.e(TAG_HOME_FRAGMENT, "content => " + content.toString());
                        String title = content.optString(AD_TITLE);
                        String description = content.optString(AD_DESCRIPTION);
                        String cta = content.optString(AD_CALL_TO_ACTION);
                        final String landingUrl = content.optString(AD_LANDING_URL);
                        float rating = (float) content.optDouble(AD_RATING);
                        JSONObject jsonObject = content.getJSONObject(AD_ICON);
                        String imageUrl = jsonObject.optString(AD_IMAGE_URL);
                        if(title!=null)
                            textView_adTitle.setText(title);
                        if(rating!=0.0)
                            ratingBar.setRating(rating);
                        if(imageUrl!=null)
                            Picasso.with(getContext()).load(imageUrl).into(imageView_adIcon);
                        if(cta!=null)
                            button_callToAction.setText(cta.toUpperCase());

                        button_callToAction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (landingUrl != null){
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(landingUrl));
                                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(browserIntent);
                                }
                            }
                        });

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {
                Log.e(TAG_HOME_FRAGMENT, "onAdLoadFailed => " + placementID + "=> " + inMobiAdRequestStatus.getMessage());
                nativeAdLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAdDismissed(InMobiNative inMobiNative) {

            }

            @Override
            public void onAdDisplayed(InMobiNative inMobiNative) {

            }

            @Override
            public void onUserLeftApplication(InMobiNative inMobiNative) {

            }
        });
        nativeAd.load();
        InMobiNative.bind(nativeAdLayout, nativeAd);


        // NATIVE AD 2
        final RelativeLayout nativeAdLayout_1 = (RelativeLayout) homeBinding.nativeAd1;
        final ImageView imageView_adIcon_1 = (ImageView) nativeAdLayout_1.findViewById(R.id.native_ad_1_icon_imageview);
        final TextView textView_adTitle_1 = (TextView) nativeAdLayout_1.findViewById(R.id.native_ad_1_title_textview);
        final MaterialRatingBar ratingBar_1 = (MaterialRatingBar) nativeAdLayout_1.findViewById(R.id.native_ad_1_rating_bar);
        final Button button_callToAction_1 = (Button) nativeAdLayout_1.findViewById(R.id.call_to_action_button_1);
        nativeAdLayout_1.setVisibility(View.GONE);

        final Long placementID_1 = 1496570757077L ;
        InMobiNative nativeAd_1 = new InMobiNative(getActivity(), placementID_1, new InMobiNative.NativeAdListener() {
            @Override
            public void onAdLoadSucceeded(InMobiNative inMobiNative) {
                Log.e(TAG_HOME_FRAGMENT, "onAdLoadSucceeded =>" + placementID_1 );
                JSONObject content = null;
                try {
                    content = new JSONObject((String) inMobiNative.getAdContent());
                    if(content!=null)
                    {
                        nativeAdLayout_1.setVisibility(View.VISIBLE);
                        Log.e(TAG_HOME_FRAGMENT, "content => " + content.toString());
                        String title = content.optString(AD_TITLE);
                        String description = content.optString(AD_DESCRIPTION);
                        String cta = content.optString(AD_CALL_TO_ACTION);
                        final String landingUrl = content.optString(AD_LANDING_URL);
                        float rating = (float) content.optDouble(AD_RATING);
                        JSONObject jsonObject = content.getJSONObject(AD_ICON);
                        String imageUrl = jsonObject.optString(AD_IMAGE_URL);
                        if(title!=null)
                            textView_adTitle_1.setText(title);
                        if(rating!=0.0)
                            ratingBar_1.setRating(rating);
                        if(imageUrl!=null)
                            Picasso.with(getContext()).load(imageUrl).into(imageView_adIcon_1);
                        if(cta!=null)
                            button_callToAction_1.setText(cta.toUpperCase());

                        button_callToAction_1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (landingUrl != null){
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(landingUrl));
                                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(browserIntent);
                                }
                            }
                        });

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {
                Log.e(TAG_HOME_FRAGMENT, "onAdLoadFailed => " + placementID_1 + "==>" + inMobiAdRequestStatus.getMessage());
                nativeAdLayout_1.setVisibility(View.GONE);
            }

            @Override
            public void onAdDismissed(InMobiNative inMobiNative) {

            }

            @Override
            public void onAdDisplayed(InMobiNative inMobiNative) {

            }

            @Override
            public void onUserLeftApplication(InMobiNative inMobiNative) {

            }
        });
        nativeAd_1.load();
        InMobiNative.bind(nativeAdLayout_1, nativeAd_1);



        audioManagerMode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        userSessionManager = new UserSessionManager(context);
        checkIfOptimized();
        setOnClickListeners();
        getSystemData();


        return view;
    }

    private void checkIfOptimized() {
        if (userSessionManager.isOptimized()) {
            homeBinding.buttonOptimizeBattery.setBackgroundResource(R.drawable.optimizedbuttonbgcolor);
            homeBinding.textviewProblemsDetected.setText(getString(R.string.noproblemdetected));
            homeBinding.textviewProblemsDetected.setTextColor(ContextCompat.getColor(context, R.color.optimizedbtncolor));
            homeBinding.textviewIssueCount.setVisibility(View.INVISIBLE);
        } else {
            homeBinding.textviewProblemsDetected.setText(getString(R.string.problemText));
            homeBinding.buttonOptimizeBattery.setBackgroundResource(R.drawable.optimizerbuttonbgcolor);
            homeBinding.textviewProblemsDetected.setTextColor(ContextCompat.getColor(context, R.color.buttonColor));
            homeBinding.textviewIssueCount.setVisibility(View.VISIBLE);
            sendCustomNotification();
        }
    }

    private void sendCustomNotification() {
        try {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
            contentView.setImageViewResource(R.id.image, R.drawable.brush_notification);
            contentView.setTextViewText(R.id.title, getString(R.string.notification_title_optimize));
            contentView.setTextViewText(R.id.text, getString(R.string.notification_description_optimize));
            intent = new Intent(getContext(), OptimizerActivity.class);
            pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setAutoCancel(false)
                    .setContent(contentView);

            contentView.setOnClickPendingIntent(R.id.notificationOptimizerBtn, pendingIntent);

            Notification notification = mBuilder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notificationManager.notify(NOTIFICATION_ID, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        registerBatteryInfoReceiver();
        checkIfOptimized();

    }

    @Override
    public void onPause() {
        if (battery_info_receiver != null)
            context.unregisterReceiver(battery_info_receiver);
        super.onPause();
    }

    private void getSystemData() {
        getDeviceBrightnessLevel();
        checkIfBluetoothOnOrOff();
        getScreenTimeout();
        getDeviceRingerMode();
        getBatteryLevel();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void getBatteryLevel() {
        myBatteryManger = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
    }

    private void getDeviceRingerMode() {

        audioCurrentMode = audioManagerMode.getRingerMode();
        LogUtil.d(TAG_HOME_FRAGMENT, "Audio mode" + String.valueOf(audioCurrentMode));

        switch (audioCurrentMode) {

            case AudioManager.RINGER_MODE_SILENT:
                homeBinding.soundTextView.setText(R.string.silentsound);
                audioCurrentMode = AudioManager.RINGER_MODE_SILENT;
                homeBinding.soundImageView.setImageResource(R.drawable.sound_silent);
                inWhichSoundIndex = SILENT_MODE;
                break;

            case AudioManager.RINGER_MODE_NORMAL:
                homeBinding.soundTextView.setText(R.string.soundText);
                homeBinding.soundImageView.setImageResource(R.drawable.sound);
                audioCurrentMode = AudioManager.RINGER_MODE_NORMAL;
                inWhichSoundIndex = RINGTONE_MODE;
                break;

            case AudioManager.RINGER_MODE_VIBRATE:
                homeBinding.soundTextView.setText(R.string.soundvibrate);
                audioCurrentMode = AudioManager.RINGER_MODE_VIBRATE;
                homeBinding.soundImageView.setImageResource(R.drawable.sound_vibrate);
                inWhichSoundIndex = VIBRATE_MODE;
                break;
        }
    }

    /**
     * See {@link com.receptix.batterybuddy.helper.Constants.LockScreenTimeout} for different range of values for screen timeout
     */
    private void getScreenTimeout() {

        int inWhichIndexSeconds;
        int inWhichIndexMinutes;

        lockScreenTimeOut = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, DELAY);
        LogUtil.d(TAG_HOME_FRAGMENT, "Timeout=" + String.valueOf(lockScreenTimeOut));

        inWhichIndexSeconds = lockScreenTimeOut / 1000;

        // if lock screen timeout is less than (or equal to) 30 seconds
        if (inWhichIndexSeconds <= 30) {
            if (inWhichIndexSeconds < 1) {
                WhatisLockScreenTimeOut = TIMEOUT_AUTO_LOCK;
                homeBinding.lockscreenTextView.setText(R.string.autolock);
            } else if (inWhichIndexSeconds == 5) {
                WhatisLockScreenTimeOut = TIMEOUT_5_SECONDS;
                homeBinding.lockscreenTextView.setText(R.string.fivesecond);

            } else if (inWhichIndexSeconds == 15) {
                WhatisLockScreenTimeOut = TIMEOUT_15_SECONDS;
                homeBinding.lockscreenTextView.setText(R.string.fivesecond);

            } else if (inWhichIndexSeconds == 30) {
                WhatisLockScreenTimeOut = TIMEOUT_30_SECONDS;
                homeBinding.lockscreenTextView.setText(R.string.thirtysecond);
            }
        } else
        // if lock screen timeout is greater than 30 seconds
        {
            inWhichIndexMinutes = inWhichIndexSeconds / 60;
            if (inWhichIndexMinutes == 1) {
                WhatisLockScreenTimeOut = TIMEOUT_1_MINUTE;
                homeBinding.lockscreenTextView.setText(R.string.oneminute);

            } else if (inWhichIndexMinutes == 2) {
                WhatisLockScreenTimeOut = TIMEOUT_2_MINUTE;
                homeBinding.lockscreenTextView.setText(R.string.twominute);

            } else if (inWhichIndexMinutes == 5) {
                WhatisLockScreenTimeOut = TIMEOUT_5_MINUTES;
                homeBinding.lockscreenTextView.setText(R.string.fiveminute);

            } else if (inWhichIndexMinutes == 10) {
                WhatisLockScreenTimeOut = TIMEOUT_10_MINUTES;
                homeBinding.lockscreenTextView.setText(R.string.tenminute);

            } else if (inWhichIndexMinutes == 30) {
                WhatisLockScreenTimeOut = TIMEOUT_30_MINUTES;
                homeBinding.lockscreenTextView.setText(R.string.thirtyminute);

            }
        }

    }

    private void checkIfBluetoothOnOrOff() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        if (bluetoothAdapter != null) {

            if (bluetoothAdapter.isEnabled()) {
                isBluetoothOn = 0;
                homeBinding.bluetoothTextView.setText(getString(R.string.on));
            } else if (bluetoothAdapter.disable()) {
                isBluetoothOn = 1;
                homeBinding.bluetoothTextView.setText(getString(R.string.off));
            }
        }

        LogUtil.d("Bluetooth", String.valueOf(isBluetoothOn));

    }

    private void getDeviceBrightnessLevel() {

        int screenBrightnessLevel;

        screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, BRIGHTNESS_DEFAULT_VALUE);

        LogUtil.d(TAG_HOME_FRAGMENT, "The brightness is: " + screenBrightness);

        screenBrightnessLevel = screenBrightness;
        String brightnessTextViewString = "";

        if (screenBrightnessLevel <= BRIGHTNESS_LEVEL_0_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_0;
            brightnessTextViewString = getString(R.string.zerobright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_0_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_1_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_1;
            brightnessTextViewString = getString(R.string.onebright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_1_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_2_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_2;
            brightnessTextViewString = getString(R.string.twobright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_2_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_3_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_3;
            brightnessTextViewString = getString(R.string.threebright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_3_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_4_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_4;
            brightnessTextViewString = getString(R.string.fourbright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_4_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_5_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_5;
            brightnessTextViewString = getString(R.string.fivebright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_5_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_6_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_6;
            brightnessTextViewString = getString(R.string.sixbright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_6_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_7_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_7;
            brightnessTextViewString = getString(R.string.sevenbright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_7_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_8_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_8;
            brightnessTextViewString = getString(R.string.eightbright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_8_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_9_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_9;
            brightnessTextViewString = getString(R.string.ninebright);
        }

        homeBinding.brightnessTextView.setText(brightnessTextViewString);

    }

    private void registerBatteryInfoReceiver() {
        IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(battery_info_receiver, intentfilter);
    }

    private void setOnClickListeners() {
        homeBinding.buttonOptimizeBattery.setOnClickListener(this);
        homeBinding.brightnessLinearLayout.setOnClickListener(this);
        homeBinding.lockscreenLinearLayout.setOnClickListener(this);
        homeBinding.bluetoothLinearLayout.setOnClickListener(this);
        homeBinding.soundLinearLayout.setOnClickListener(this);
    }

    private void getValueInHoursMinutes(double remainingScreenTime, int statisticType) {

        int hours = (int) remainingScreenTime;
        //obtain actual number of minutes from double value of Hours
        String stringminutes = String.valueOf(String.format("%.2f", remainingScreenTime));
        String lasttwo = stringminutes.substring(stringminutes.length() - 2);
        int minute = Integer.parseInt(lasttwo);
        int realminutes = (minute * 60) / 100;

        String valueGreaterThanEqualToOneHour = String.valueOf(hours) + HOURS + " " + String.valueOf(realminutes) + MINUTES;
        String valueLessThanOneHour = String.valueOf(0) + HOURS + " " + String.valueOf(realminutes) + MINUTES;

        if (statisticType == OVERALL_BATTERY) {
            if (remainingScreenTime > 0.9) {
                homeBinding.textviewAvailableHoursBattery.setText(String.valueOf(hours));
                homeBinding.textviewAvailableMinutesBattery.setText(String.valueOf(realminutes));
            } else {
                homeBinding.textviewAvailableHoursBattery.setText("0");
                homeBinding.textviewAvailableMinutesBattery.setText(String.valueOf(realminutes));
            }
        } else if (statisticType == VOICE_CALL) {
            if (remainingScreenTime > 0.9) {
                homeBinding.textviewTimeLeftVoiceCalls.setText(valueGreaterThanEqualToOneHour);
            } else {
                homeBinding.textviewTimeLeftVoiceCalls.setText(valueLessThanOneHour);
            }
        } else if (statisticType == VIDEO) {
            if (remainingScreenTime > 0.9) {
                homeBinding.textviewTimeLeftVideo.setText(valueGreaterThanEqualToOneHour);
            } else {
                homeBinding.textviewTimeLeftVideo.setText(valueLessThanOneHour);
            }
        } else if (statisticType == WIFI) {
            if (remainingScreenTime > 0.9) {
                homeBinding.textviewTimeLeftWifi.setText(valueGreaterThanEqualToOneHour);
            } else {
                homeBinding.textviewTimeLeftWifi.setText(valueLessThanOneHour);
            }
        }
    }

    private void showMessage(String s) {
        // Toast.makeText(context, s, // Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        // BUTTON - OPTIMIZE
        if (v == homeBinding.buttonOptimizeBattery) {

            if (!userSessionManager.isOptimized()) {

                userSessionManager.setIsOptimized(true);
                Intent optintent = new Intent(getContext(), OptimizerActivity.class);
                optintent.putExtra(BATTERY_LEVEL, batteryLevel);
                context.startActivity(optintent);

            } else {

                Intent nointent = new Intent(context, OptimalStateActivity.class);
                context.startActivity(nointent);
            }
        }

        // BRIGHTNESS SELECTION OPTIONS
        if (v == homeBinding.brightnessLinearLayout) {
            showMessage("I am brightness");
            SetScreenBrightness();
        }

        // LOCK SCREEN TIMEOUT SELECTION OPTIONS
        if (v == homeBinding.lockscreenLinearLayout) {
            showMessage("I am Lock ");
            SetLockScreenTimer();
        }

        // BLUETOOTH TOGGLE OPTIONS
        if (v == homeBinding.bluetoothLinearLayout) {
            showMessage("I am Bluetooth");
            OnOffBluetooth();
        }

        // SOUND TOGGLE OPTIONS
        if (v == homeBinding.soundLinearLayout) {
            showMessage("I am Sound ");
            SoundStatusChange();
        }
    }

    private void SoundStatusChange() {
        new MaterialDialog.Builder(context)
                .titleColorRes(R.color.white)
                .contentColorRes(R.color.white)
                .positiveColorRes(R.color.white)
                .negativeColorRes(R.color.white)
                .title(R.string.soundtitle)
                .items(R.array.soundarray)
                .widgetColorRes(R.color.white)
                .backgroundColorRes(R.color.bgcolor)
                .itemsCallbackSingleChoice(inWhichSoundIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {


                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        // Toast.makeText(context, which + ": " + text + ", ID = " + view.getId(), // Toast.LENGTH_SHORT).show();

                        switch (which) {

                            case 0:
                                audioManagerMode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                homeBinding.soundTextView.setText(R.string.silentsound);
                                homeBinding.soundImageView.setImageResource(R.drawable.sound_silent);
                                break;
                            case 1:
                                audioManagerMode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                homeBinding.soundTextView.setText(R.string.soundText);
                                homeBinding.soundImageView.setImageResource(R.drawable.sound);
                                break;
                            case 2:
                                audioManagerMode.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                homeBinding.soundTextView.setText(R.string.soundvibrate);
                                homeBinding.soundImageView.setImageResource(R.drawable.sound_vibrate);
                                break;


                        }


                        getDeviceRingerMode();

                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .negativeText(R.string.cancel)
                .show();

    }

    private void SetScreenBrightness() {


        new MaterialDialog.Builder(context)
                .titleColorRes(R.color.white)
                .contentColorRes(R.color.white)
                .positiveColorRes(R.color.white)
                .negativeColorRes(R.color.white)
                .title(R.string.brightnesstitle)
                .items(R.array.brightness)
                .widgetColorRes(R.color.white)
                .backgroundColorRes(R.color.bgcolor)
                .itemsCallbackSingleChoice(inWhichBrightness, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        int setBrightness = 0;

                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        // Toast.makeText(context, which + ": " + text + ", ID = " + view.getId(), // Toast.LENGTH_SHORT).show();

                        switch (which) {

                            case 0:
                                setBrightness = 24;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setBrightness);
                                homeBinding.brightnessTextView.setText(R.string.zerobright);
                                break;

                            case 1:
                                setBrightness = 50;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setBrightness);
                                homeBinding.brightnessTextView.setText(R.string.onebright);
                                break;

                            case 2:
                                setBrightness = 76;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setBrightness);
                                homeBinding.brightnessTextView.setText(R.string.twobright);
                                break;

                            case 3:
                                setBrightness = 102;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setBrightness);
                                homeBinding.brightnessTextView.setText(R.string.threebright);
                                break;

                            case 4:
                                setBrightness = 127;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setBrightness);
                                homeBinding.brightnessTextView.setText(R.string.fourbright);
                                break;

                            case 5:
                                setBrightness = 152;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setBrightness);
                                homeBinding.brightnessTextView.setText(R.string.fivebright);
                                break;

                            case 6:
                                setBrightness = 178;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setBrightness);
                                homeBinding.brightnessTextView.setText(R.string.sixbright);
                                break;

                            case 7:
                                setBrightness = 203;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setBrightness);
                                homeBinding.brightnessTextView.setText(R.string.sevenbright);
                                break;

                            case 8:
                                setBrightness = 230;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setBrightness);
                                homeBinding.brightnessTextView.setText(R.string.eightbright);
                                break;
                            case 9:
                                setBrightness = 250;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setBrightness);
                                homeBinding.brightnessTextView.setText(R.string.ninebright);
                                break;
                        }

                        getDeviceBrightnessLevel();

                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .negativeText(R.string.cancel)
                .show();

        //   Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,200);


    }

    private void SetLockScreenTimer() {

        new MaterialDialog.Builder(context)
                .titleColorRes(R.color.white)
                .contentColorRes(R.color.white)
                .positiveColorRes(R.color.white)
                .negativeColorRes(R.color.white)
                .title(R.string.lockScreenTimeouttitle)
                .items(R.array.sreenlocktimer)
                .widgetColorRes(R.color.white)
                .backgroundColorRes(R.color.bgcolor)
                .itemsCallbackSingleChoice(WhatisLockScreenTimeOut, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        int timeout = 0;

                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        // Toast.makeText(context, which + ": " + text + ", ID = " + view.getId(), // Toast.LENGTH_SHORT).show();

                        switch (which) {

                            case 0:
                                timeout = 0;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                homeBinding.lockscreenTextView.setText(R.string.autolock);
                                break;

                            case 1:
                                timeout = 5000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                homeBinding.lockscreenTextView.setText(R.string.fivesecond);
                                break;

                            case 2:
                                timeout = 15000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                homeBinding.lockscreenTextView.setText(R.string.fiveteensecond);
                                break;

                            case 3:
                                timeout = 30000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                homeBinding.lockscreenTextView.setText(R.string.thirtysecond);

                                break;

                            case 4:
                                timeout = 60000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                homeBinding.lockscreenTextView.setText(R.string.oneminute);

                                break;

                            case 5:
                                timeout = 120000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                homeBinding.lockscreenTextView.setText(R.string.twominute);

                                break;

                            case 6:
                                timeout = 300000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                homeBinding.lockscreenTextView.setText(R.string.fiveminute);

                                break;

                            case 7:
                                timeout = 600000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                homeBinding.lockscreenTextView.setText(R.string.tenminute);

                                break;

                            case 8:
                                timeout = 1800000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                homeBinding.lockscreenTextView.setText(R.string.thirtyminute);

                                break;
                        }


                        getScreenTimeout();
                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .negativeText(R.string.cancel)
                .show();


    }

    private void OnOffBluetooth() {

        String onoffArray[] = {"On", "Off"};

        new MaterialDialog.Builder(context)
                .titleColorRes(R.color.white)
                .contentColorRes(R.color.white)
                .positiveColorRes(R.color.white)
                .title(R.string.bluetoothtitle)
                .items(onoffArray)
                .widgetColorRes(R.color.white)
                .backgroundColorRes(R.color.bgcolor)
                .itemsCallbackSingleChoice(isBluetoothOn, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        // Toast.makeText(context, which + ": " + text + ", ID = " + view.getId(), // Toast.LENGTH_SHORT).show();

                        try {

                            switch (which) {
                                case 0:
                                    if (bluetoothAdapter.disable()) {
                                        bluetoothAdapter.enable();
                                        homeBinding.bluetoothTextView.setText(getString(R.string.on));
                                        isBluetoothOn = 0;
                                    }
                                    break;
                                case 1:

                                    if (bluetoothAdapter.enable()) {
                                        bluetoothAdapter.disable();
                                        homeBinding.bluetoothTextView.setText(getString(R.string.off));
                                        isBluetoothOn = 1;
                                    }
                                    break;

                            }


                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .show();
    }

    public double getBatteryCapacity() {
        Object mPowerProfile = null;
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Method getAveragePower = Class.forName(POWER_PROFILE_CLASS).getMethod(METHOD_GET_AVERAGE_POWER, String.class);
            //Get total battery capacity in mAh.
            double batteryCapacity = (Double) getAveragePower.invoke(mPowerProfile, BATTERY_CAPACITY);
            return batteryCapacity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getPowerConsumption_radio() {

        Object mPowerProfile = null;
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Method getAveragePower = Class.forName(POWER_PROFILE_CLASS).getMethod(METHOD_GET_AVERAGE_POWER, String.class);
            double radioActive = (Double) getAveragePower.invoke(mPowerProfile, RADIO_ACTIVE);
            double radioScanning = (Double) getAveragePower.invoke(mPowerProfile, RADIO_SCANNING);
            double radioOn = (Double) getAveragePower.invoke(mPowerProfile, RADIO_ON);

            int radioPowerConsumption = (int) radioActive + (int) radioScanning + (int) radioOn;
            return radioPowerConsumption;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // default value to return if radio power values not obtained
        return 0;

    }

    public double getPowerConsumption_Total() {

        Object mPowerProfile = null;
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            Method getAveragePower = Class.forName(POWER_PROFILE_CLASS).getMethod(METHOD_GET_AVERAGE_POWER, String.class);

            double powerConsumption_wifiOn = (Double) getAveragePower.invoke(mPowerProfile, WIFI_ON);
            double powerConsumption_screenOn = (Double) getAveragePower.invoke(mPowerProfile, SCREEN_ON);
            double powerConsumption_cpuActive = (Double) getAveragePower.invoke(mPowerProfile, CPU_ACTIVE);
            double powerConsumption_cpuAwake = (Double) getAveragePower.invoke(mPowerProfile, CPU_AWAKE);

            double averageConsumption_Cpu = (powerConsumption_cpuActive + powerConsumption_cpuAwake) / 2;
            double powerConsumption_total = (averageConsumption_Cpu + powerConsumption_wifiOn + powerConsumption_screenOn);
            return powerConsumption_total;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0.0;


    }

    public double getPowerConsumption_Wifi() {

        Object mPowerProfile = null;
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            Method getAveragePower = Class.forName(POWER_PROFILE_CLASS).getMethod(METHOD_GET_AVERAGE_POWER, String.class);

            double wifiOn = (Double) getAveragePower.invoke(mPowerProfile, WIFI_ON);
            double wifiActive = (Double) getAveragePower.invoke(mPowerProfile, WIFI_ACTIVE);
            double wifiScan = (Double) getAveragePower.invoke(mPowerProfile, WIFI_SCANNING);

            double screenOn = (Double) getAveragePower.invoke(mPowerProfile, SCREEN_ON);
            double cpuAwake = (Double) getAveragePower.invoke(mPowerProfile, CPU_AWAKE);
            double cpuIdle = (Double) getAveragePower.invoke(mPowerProfile, CPU_IDLE);
            double cpuActive = (Double) getAveragePower.invoke(mPowerProfile, CPU_ACTIVE);

            double averageConsumption_Cpu = (cpuActive + cpuAwake + cpuIdle) / 3;
            double totalConsumption_WifiOnly = (wifiActive + wifiOn + wifiScan) / 3;
            // to get a rough estimate for
            // ETA for wifi usage, we also consider CPU Usage and Screen On (since both CPU and Screen are used when Wifi is used (mostly))
            double totalConsumptionOnWifi = (totalConsumption_WifiOnly + averageConsumption_Cpu + screenOn);
            return totalConsumptionOnWifi;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0.0;


    }

    public double getPowerConsumption_Videos() {

        Object mPowerProfile = null;
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            Method getAveragePower = Class.forName(POWER_PROFILE_CLASS).getMethod(METHOD_GET_AVERAGE_POWER, String.class);

            double cpuAwake = (Double) getAveragePower.invoke(mPowerProfile, CPU_AWAKE);
            double cpuActive = (Double) getAveragePower.invoke(mPowerProfile, CPU_ACTIVE);
            double wifiOn = (Double) getAveragePower.invoke(mPowerProfile, WIFI_ON);
            double screenOn = (Double) getAveragePower.invoke(mPowerProfile, SCREEN_ON);
            double dspAudio = (Double) getAveragePower.invoke(mPowerProfile, DSP_AUDIO);
            double dspVideo = (Double) getAveragePower.invoke(mPowerProfile, DSP_VIDEO);

            double averageConsumption_Cpu = (cpuActive + cpuAwake) / 2;

            // to get a rough estimate for
            // ETA for video usage, we also consider CPU Usage, Screen On
            // and Audio-Video Decoding Power Consumption.
            double totalConsumption_Videos = (averageConsumption_Cpu + wifiOn + screenOn + dspAudio + dspVideo);
            return totalConsumption_Videos;

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return 0.0;


    }

    public double getPowerConsumption_VoiceCalls() {

        Object mPowerProfile = null;
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            Method getAveragePower = Class.forName(POWER_PROFILE_CLASS).getMethod(METHOD_GET_AVERAGE_POWER, String.class);

            double cpuAwake = (Double) getAveragePower.invoke(mPowerProfile, CPU_AWAKE);
            double cpuActive = (Double) getAveragePower.invoke(mPowerProfile, CPU_ACTIVE);
            double wifiOn = (Double) getAveragePower.invoke(mPowerProfile, WIFI_ON);
            double screenOn = (Double) getAveragePower.invoke(mPowerProfile, SCREEN_ON);
            double dspAudio = (Double) getAveragePower.invoke(mPowerProfile, DSP_AUDIO);

            double averageConsumption_Cpu = (cpuActive + cpuAwake) / 2;
            // to get a rough estimate for
            // ETA for video usage, we also consider CPU Usage, Wifi (for voice calls through wifi), Screen
            // and Audio Decoding Power Consumption.
            double totalConsumption = (averageConsumption_Cpu + wifiOn + screenOn + dspAudio);
            return totalConsumption;
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return 0.0;


    }


}
