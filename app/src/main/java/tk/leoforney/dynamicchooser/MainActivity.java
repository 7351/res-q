package tk.leoforney.dynamicchooser;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/*
Created by Dynamic Signals FTC Team 7351
 */
public class MainActivity extends AppCompatActivity {

    // Import this into your opMode
    /*
    public final static String FILENAMEPREF = "preferences";
    public final static String[] KEY_LIST = {
            "redMode", // true = Red alliance; false = Blue alliance
            "delay", // Time in seconds before match starts // int                          // This part should always be the same
            "targetGoal", // Where should the robot head to? // brz or fg
    };


    public SharedPreferences getPreferences() {

        SharedPreferences pref = null;
        try {
            Context con = ApplicationContextProvider.getContext().createPackageContext("tk.leoforney.dynamicchooser", 0);
            pref = con.getSharedPreferences(FILENAMEPREF, Context.MODE_PRIVATE);
        } catch (PackageManager.NameNotFoundException e) {
            DbgLog.error(e.toString());
        }
        return pref;
    }

    // These variables should be changed to whatever desired
    // You can access your variables like this:
    (data type) (variable name) = getPreferences.get [data type](KEY_LIST[(the indexed number of what your key is called)], (your default value if not found));

    Example:
        boolean redMode = getPreferences().getBoolean(KEY_LIST[0], true);
        int delay = getPreferences().getInt(KEY_LIST[1], 0);
        String targetGoal = getPreferences().getString(KEY_LIST[2], "fg");
     */

    public final static String FILENAMEPREF = "preferences";
    // They keylist variable helps tell what variables are stored...
    public final static String[] KEY_LIST = {
            "redMode", // true = Red alliance; false = Blue alliance
            "delay", // Time in seconds before match starts // int
            "targetGoal", // Where should the robot head to? // brz or fg
            "proxValMin" // How far away should the robot be? // int
    };
    private static final String TAG = MainActivity.class.getName();
    RadioButton redAlliance;
    RadioButton blueAlliance;
    EditText delayTimeEditText;
    RadioButton floorGoalRadioButton;
    RadioButton beaconRepairZoneRadioButton;
    Button updateButton;
    RadioGroup allianceGroup;
    RadioGroup goalGroup;
    EditText proxValueEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        redAlliance = (RadioButton) findViewById(R.id.red);
        blueAlliance = (RadioButton) findViewById(R.id.blue);

        delayTimeEditText = (EditText) findViewById(R.id.delayTimeEditText);

        floorGoalRadioButton = (RadioButton) findViewById(R.id.floorGoalRadioButton);
        beaconRepairZoneRadioButton = (RadioButton) findViewById(R.id.beaconRepairZoneRadioButton);

        updateButton = (Button) findViewById(R.id.updateButton);

        allianceGroup = (RadioGroup) findViewById(R.id.allianceGroup);
        goalGroup = (RadioGroup) findViewById(R.id.goalGroup);

        proxValueEditText = (EditText) findViewById(R.id.proxValueEditText);

    }

    public void onUpdateButtonPressed(View view) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(FILENAMEPREF, Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = pref.edit();

        if (redAlliance.isChecked()) {
            editor.putBoolean(KEY_LIST[0], true);

        }
        if (blueAlliance.isChecked()) {
            editor.putBoolean(KEY_LIST[0], false);
        }

        int delayInt = Integer.parseInt(delayTimeEditText.getText().toString());

        editor.putInt(KEY_LIST[1], delayInt);

        if (floorGoalRadioButton.isChecked()) {
            editor.putString(KEY_LIST[2], "fg");
        }
        if (beaconRepairZoneRadioButton.isChecked()) {
            editor.putString(KEY_LIST[2], "brz");
        }

        int proxValueInt = Integer.parseInt(proxValueEditText.getText().toString());

        editor.putInt(KEY_LIST[3], proxValueInt);

        editor.apply();

        Toast.makeText(getApplicationContext(), "Succesfully pushed :)", Toast.LENGTH_LONG).show();

    }

    public void onGetButtonPressed(View view) {
        SharedPreferences pref = getSharedPreferences(FILENAMEPREF, Context.MODE_WORLD_READABLE);
        Log.d(TAG, String.valueOf(pref.getBoolean(KEY_LIST[0], false)));
        Log.d(TAG, String.valueOf(pref.getInt(KEY_LIST[1], 0)));
        Log.d(TAG, String.valueOf(pref.getString(KEY_LIST[2], "unknown")));
        Log.d(TAG, String.valueOf(pref.getInt(KEY_LIST[3], 100)));
    }

}












