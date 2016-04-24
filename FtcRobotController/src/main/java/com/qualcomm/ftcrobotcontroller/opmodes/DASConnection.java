package com.qualcomm.ftcrobotcontroller.opmodes;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;

/**
 * Created by Leo on 1/4/2016.
 */
public abstract class DASConnection{

    public DASConnection() {}

    public final static String FILENAMEPREF = "preferences";
    public final String[] KEY_LIST = {
            "redMode", // true = Red alliance; false = Blue alliance
            "delay", // Time in seconds before match starts // int
            "targetGoal", // Where should the robot head to? // brz, fg
            "startingPos", // Where is the robot located during start // 0 - close 1 - far
            "defenseTarget" // where should we defende at, or no defense // String no - no defense | beacon - defend at beacon | mnt - defend at mountain
    };
    private SharedPreferences getPreferences() {

        SharedPreferences pref = null;
        try {
            Context con = FtcRobotControllerActivity.getContext().createPackageContext("tk.leoforney.dynamicchooser", 0);
            pref = con.getSharedPreferences(FILENAMEPREF, Context.MODE_PRIVATE);
        } catch (PackageManager.NameNotFoundException e) {
            DbgLog.error(e.toString());
        }
        return pref;
    }

    public String getString(String Key) {
        return getPreferences().getString(Key, null);
    }

    public int getInt(String Key) {
        return getPreferences().getInt(Key, 0);
    }

    public boolean getBoolean(String Key) {
        return getPreferences().getBoolean(Key, false);
    }

}
