package com.qualcomm.ftcrobotcontroller.opmodes;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.ftcrobotcontroller.ApplicationContextProvider;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * DriveToBeacon
 * <p>
 * Drive to the beacon
 */
public class Autonomous extends DriveTrainLayer {

    public final static String FILENAMEPREF = "preferences";
    public final static String[] KEY_LIST = {
            "redMode", // true = Red alliance; false = Blue alliance
            "delay", // Time in seconds before match starts // int
            "targetGoal", // Where should the robot head to? // brz or fg
            "motorPower" // Percent of motor power for autonomous
    };
    final static int TOLERANCE = 2;
    boolean redMode = getPreferences().getBoolean(KEY_LIST[0], true);
    int delay = getPreferences().getInt(KEY_LIST[1], 0);
    String targetGoal = getPreferences().getString(KEY_LIST[2], "fg");
    double motorPower = (getPreferences().getInt(KEY_LIST[3], 100))/100;


    ColorSensor lineColorSensor;

    //sDcMotor led;

    GyroSensor gyro;

    int stage = -1;
    ElapsedTime manipTime = new ElapsedTime();
    double leftPower = 0;
    double rightPower = 0;
    boolean defaultPowerSet = false;
    ElapsedTime waitTime = new ElapsedTime();
    ElapsedTime startTime = new ElapsedTime();
    ElapsedTime MatchStartTimer = new ElapsedTime();
    DcMotor intakeMotor;

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

    public boolean aboveRedLine (){
        boolean returnValue = false;
        if ((lineColorSensor.red() > lineColorSensor.green() + 1) && (lineColorSensor.red() > lineColorSensor.blue() + 1)) {
            returnValue = true;
        }
        return returnValue;
    }

    public boolean aboveBlueLine(){
        boolean returnValue = false;
        if ((lineColorSensor.blue() > lineColorSensor.red()) && (lineColorSensor.blue() > lineColorSensor.green())) {
            returnValue = true;
        }
        return returnValue;
    }

    /*
     * Code to run when the op mode is initialized goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#init()
     */
    @Override
    public void init() {

        super.init();

        lineColorSensor = hardwareMap.colorSensor.get("lineColorSensor");

        gyro = hardwareMap.gyroSensor.get("gyro");
        lineColorSensor.enableLed(false);
        gyro.calibrate();

        intakeMotor = hardwareMap.dcMotor.get("intakeMotor");

        //led = hardwareMap.dcMotor.get("led");

    }

    @Override
    public void start() {
        super.start();

        lineColorSensor.enableLed(true);
        gyro.calibrate();
        manipTime.reset();

        startTime.reset();

        MatchStartTimer.reset();
    }

    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
    @Override
    public void loop() {

        //led.setPower(100);
        if (stage == -1) {
            if (MatchStartTimer.time() >= delay) {
                stage++;
            }
        }
        if (targetGoal.equals("fg")) {
            if (stage == 0) {
                if (!gyro.isCalibrating()) {
                    manipTime.reset();
                    stage++;
                }
            }
            if (stage == 1) {
                if (!gyro.isCalibrating()) {
                    driveLeft(0.6);
                    driveRight(0.6);
                    if (manipTime.time() >= 0.5) {
                        driveLeft(0);
                        driveRight(0);
                        stage++;
                        waitTime.reset();
                    }
                }
            }
            if (stage == 2) {
                if (waitTime.time() >= 1) {
                    stage++;
                }
            }
            if (redMode) {
                if (stage == 3) {
                    if (!gyro.isCalibrating()) {
                        double target_angle_degrees = 309; // 307 + 10
                        // TODO Fix the gyro reaction motor issue thing
                        double error_degrees = target_angle_degrees - gyro.getHeading();
                        if ( error_degrees > 15) {
                            driveLeft(0.3);
                            driveRight(-0.3);
                        } else {
                            driveLeft(0.25);
                            driveRight(-0.25);
                        } if (gyro.getHeading() <= target_angle_degrees + 2) {
                            if (gyro.getHeading() >= target_angle_degrees - 2) {
                                DbgLog.msg("Reached degree of: " + String.valueOf(gyro.getHeading()) + ", Time of: " + startTime.time());
                                driveLeft(0);
                                driveRight(0);
                                stage++;
                                DbgLog.msg("Reached degree of: " + String.valueOf(gyro.getHeading()) + ", Time of: " + startTime.time());
                                waitTime.reset();
                            }

                        }
                    }

                }
            }
            if (!redMode) {
                if (stage == 3) {
                    if (!gyro.isCalibrating()) {
                        double target_angle_degrees = 51; // 307 + 10
                        // TODO Fix the gyro reaction motor issue thing
                        double error_degrees = target_angle_degrees - gyro.getHeading();
                        if ( error_degrees > 15) {
                            driveLeft(-0.3);
                            driveRight(0.3);
                        } else {
                            driveLeft(-0.25);
                            driveRight(0.25);
                        } if (gyro.getHeading() <= target_angle_degrees + 1) {
                            if (gyro.getHeading() >= target_angle_degrees - TOLERANCE) {
                                DbgLog.msg("Reached degree of: " + String.valueOf(gyro.getHeading()) + ", Time of: " + startTime.time());
                                driveLeft(0);
                                driveRight(0);
                                stage++;
                                DbgLog.msg("Reached degree of: " + String.valueOf(gyro.getHeading()) + ", Time of: " + startTime.time());
                                waitTime.reset();
                            }

                        }
                    }

                }
            }

            if (stage == 4) {
                driveLeft(0);
                driveRight(0);
                DbgLog.msg("Reached degree of: " + String.valueOf(gyro.getHeading()) + ", Time of: " + startTime.time());
                if (waitTime.time() >= 1) {
                    DbgLog.msg("Reached degree of: " + String.valueOf(gyro.getHeading()) + ", Time of: " + startTime.time());
                    stage++;
                }
            }
            if (redMode) {
                if (stage == 5) {
                    if (aboveRedLine()) {
                        leftPower = 0;
                        rightPower = 0;
                        stage++;
                    } if (!aboveRedLine()) {
                        // Starting left power = 0.65
                        // Starting right power = 0.85
                        // Decrease by .1
                        if (defaultPowerSet == false) {
                            rightPower = 0.375;
                            leftPower = 0.4;
                            defaultPowerSet = true;
                        }
                        if (defaultPowerSet == true) {
                            if (manipTime.time() > 0.1) {
                                leftPower -= 0.0015;
                                rightPower -= 0.0015;
                                manipTime.reset();
                            }
                        }

                    }

                    driveLeft(rightPower);
                    driveRight(leftPower);
                }
            }
            if (!redMode) {
                if (stage == 5) {
                    if (aboveBlueLine()) {
                        leftPower = 0;
                        rightPower = 0;
                        stage++;
                    } if (!aboveBlueLine()) {
                        // Starting left power = 0.65
                        // Starting right power = 0.8
                        if (defaultPowerSet == false) {
                            rightPower = 0.375;
                            leftPower = 0.4;
                            defaultPowerSet = true;
                        }
                        if (defaultPowerSet == true) {
                            if (manipTime.time() > 0.1) {
                                leftPower -= 0.0015;
                                rightPower -= 0.0015;
                                manipTime.reset();
                            }
                        }

                    }

                    driveLeft(rightPower);
                    driveRight(leftPower);
                }
            }

            if (stage >= 1 && stage <= 5) {
                intakeMotor.setPower(1);
            } else {
                intakeMotor.setPower(0);
            }
        }



        telemetry.addData("stage", String.valueOf(stage));
        telemetry.addData("motor", String.valueOf(motorRight1.getPower()));
        telemetry.addData("gyro", String.valueOf(gyro.getHeading()));
        DbgLog.msg(String.valueOf(gyro.getHeading()) + ", " + startTime.time());





    }

    /*
     * Code to run when the op mode is first disabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
     */
    @Override
    public void stop() {

    }

}
