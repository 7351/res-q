package com.qualcomm.ftcrobotcontroller.opmodes;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Leo on 1/4/2016.
 */
public abstract class DASLayer extends DriveTrainLayer {
    public final static String FILENAMEPREF = "preferences";
    public final static String[] KEY_LIST = {
            "redMode", // true = Red alliance; false = Blue alliance
            "delay", // Time in seconds before match starts // int
            "targetGoal", // Where should the robot head to? // brz or fg
    };
    public SharedPreferences getPreferences() {

        SharedPreferences pref = null;
        try {
            Context con = FtcRobotControllerActivity.getContext().createPackageContext("tk.leoforney.dynamicchooser", 0);
            pref = con.getSharedPreferences(FILENAMEPREF, Context.MODE_PRIVATE);
        } catch (PackageManager.NameNotFoundException e) {
            DbgLog.error(e.toString());
        }
        return pref;
    }

    boolean redMode = getPreferences().getBoolean(KEY_LIST[0], true);
    int delay = getPreferences().getInt(KEY_LIST[1], 0);
    String targetGoal = getPreferences().getString(KEY_LIST[2], "fg");

    ElapsedTime delayTimer = new ElapsedTime();
}
