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

public class MainActivity extends AppCompatActivity {

    public final static String FILENAMEPREF = "preferences";
    public final static String[] KEY_LIST = {
            "redMode", // true = Red alliance; false = Blue alliance
            "delay", // Time in seconds before match starts // int
            "targetGoal", // Where should the robot head to? // brz or fg
            "motorPower" // Percent of motor power for autonomous
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
    EditText motorPowerEditText;

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

        motorPowerEditText = (EditText) findViewById(R.id.motorPowerEditText);

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

        int motorPowerInt = Integer.parseInt(motorPowerEditText.getText().toString());

        editor.putInt(KEY_LIST[3], motorPowerInt);

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
