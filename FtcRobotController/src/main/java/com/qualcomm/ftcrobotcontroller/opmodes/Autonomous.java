package com.qualcomm.ftcrobotcontroller.opmodes;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
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
            "targetGoal", // Where should the robot head to? // brz, fg, or mnt
            "startingPos", // Where is the robot located during start
            "proxValMin" // How far away should the robot be? // int
    };
    final static int TOLERANCE = 2;
    boolean redMode = getPreferences().getBoolean(KEY_LIST[0], true);
    int delay = getPreferences().getInt(KEY_LIST[1], 0);
    String targetGoal = getPreferences().getString(KEY_LIST[2], "fg");
    int startingPos = getPreferences().getInt(KEY_LIST[3], 0);



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
    boolean goalReached[] = {false, false, false, false};
    Servo LBumper;
    Servo RBumper;
    Servo leftAngelArm;
    Servo rightAngelArm;
    double leftBumperRest = 0.69,
            leftBumperTilt = 0.2,
            rightBumperRest = 0.48,
            rightBumperTilt = 1;
    double leftAngelHome = 0.76,
            leftAngelScore = 0.2,
            rightAngelHome = 0.17,
            rightAngelScore = 0.72,
            restingPosition = 0.07;

    public boolean isGyroInTolerance(int degree) {
        boolean returnValue = false;
        if ((gyro.getHeading() <= degree + TOLERANCE) && (gyro.getHeading() >= degree - TOLERANCE)) {
            returnValue = true;
        }
        return returnValue;
    }

    public boolean aboveWhiteLine() {
        boolean returnValue = false;
        if ((lineColorSensor.red() >= 3) && (lineColorSensor.green() >= 3) && (lineColorSensor.blue() >= 3)) {
            returnValue = true;
        }
        return returnValue;
    }

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

        RBumper = hardwareMap.servo.get("RBumper");
        LBumper = hardwareMap.servo.get("LBumper");
        leftAngelArm = hardwareMap.servo.get("leftAngelArm");
        rightAngelArm = hardwareMap.servo.get("rightAngelArm");

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

        RBumper.setPosition(rightBumperRest);
        LBumper.setPosition(leftBumperRest);
        leftAngelArm.setPosition(leftAngelHome);
        rightAngelArm.setPosition(rightAngelHome);
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
        if (targetGoal.equals("mnt")) {
            if (stage == 0) {
                if (redMode) {
                    LBumper.setPosition(leftBumperTilt);
                } else {
                    RBumper.setPosition(rightBumperTilt);
                }
                double driveTime = 1.2;
                if (startingPos == 1) {
                    driveTime = 2.9;
                }
                if (startingPos == 0) {
                    driveTime = 2;
                }

                if (this.time < driveTime + delay) {
                    driveLeft(0.60);
                    driveRight(0.56);
                }
                if (this.time >= driveTime + delay) {
                    driveLeft(0);
                    driveRight(0);
                    stage = 6;
                }
            }
            telemetry.addData("Time", String.valueOf(this.time));


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
                            driveLeft(0.275);
                            driveRight(-0.275);
                        } else {
                            driveLeft(0.225);
                            driveRight(-0.225);
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
                        double target_angle_degrees = 49; // 307 + 10
                        // TODO Fix the gyro reaction motor issue thing
                        double error_degrees = target_angle_degrees - gyro.getHeading();
                        if ( error_degrees > 15) {
                            driveLeft(-0.275);
                            driveRight(0.275);
                        } else {
                            driveLeft(-0.225);
                            driveRight(0.225);
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
                            rightPower = 1;
                            leftPower = 1;
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
                            rightPower = 1;
                            leftPower = 1;
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


            telemetry.addData("stage", String.valueOf(stage));
            telemetry.addData("motor", String.valueOf(motorRight1.getPower()));
            DbgLog.msg("L:" + String.valueOf(motorRight1.getPower()) + ", R: " + String.valueOf(motorLeft1.getPower()));
            telemetry.addData("gyro", String.valueOf(gyro.getHeading()));
        }






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
