/*    if (stage == 0) {
            if (!gyro.isCalibrating()) {
                double target_angle_degrees = 135;
                double error_degrees = target_angle_degrees - gyro.getHeading();
                if ( error_degrees > 25) {
                    driveLeft(0.75);
                    driveRight(-0.75);
                } else {
                    driveLeft(0.57);
                    driveRight(-0.57);
                } if (isGyroInTolerance((int) target_angle_degrees)) {
                    driveLeft(0);
                    driveRight(0);
                    stage++;
                }
            } if (gyro.isCalibrating()) {
                driveLeft(0);
                driveRight(0);
            }
        }
         */
package com.qualcomm.ftcrobotcontroller.opmodes;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.ftcrobotcontroller.library.devices.VCNL4010;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * DriveToBeacon
 * <p/>
 * Drive to the beacon red side
 */
public class DriveToBeaconBlue extends DriveTrainLayer {

    public final static String FILENAMEPREF = "preferences";
    public final static String[] KEY_LIST = {
            "redMode", // true = Red alliance; false = Blue alliance
            "delay", // Time in seconds before match starts // int
            "targetGoal", // Where should the robot head to? // brz or fg
            "proxValMin" // How far away should the robot be? // int
    };
    final static int TOLERANCE = 1;
    ColorSensor lineColorSensor;
    GyroSensor gyro;
    int stage = 0;
    ElapsedTime manipTime = new ElapsedTime();
    double leftPower = 0;
    double rightPower = 0;
    boolean defaultPowerSet = false;
    ElapsedTime waitTime = new ElapsedTime();
    ElapsedTime startTime = new ElapsedTime();
    boolean goalReached[] = {false, false, false, false};
    DcMotor intakeMotor;
    Servo climbersServo;
    double restingPosition = 0.07;
    double servoPosition = restingPosition;
    Servo leftAngelArm;
    Servo rightAngelArm;
    VCNL4010 prox;
    private double servoDelta = 0.01;
    private ElapsedTime servotime = new ElapsedTime();
    private double servoDelayTime2 = 0.0001;

    public boolean isOutOfGyroGoal(int degree) {
        boolean returnValue = true;
        if ((gyro.getHeading() <= degree + 6) && (gyro.getHeading() >= degree - 2)) {
            returnValue = false;
        }
        return returnValue;
    }

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

    public boolean aboveBlueLine() {
        boolean returnValue = false;
        if ((lineColorSensor.blue() > lineColorSensor.red()) && (lineColorSensor.blue() > lineColorSensor.green())) {
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

    /*
     * Code to run when the op mode is initialized goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#init()
     */
    @Override
    public void init() {

        prox = new VCNL4010(hardwareMap, "prox");

        super.init();

        lineColorSensor = hardwareMap.colorSensor.get("lineColorSensor");

        gyro = hardwareMap.gyroSensor.get("gyro");
        lineColorSensor.enableLed(false);
        gyro.calibrate();

        intakeMotor = hardwareMap.dcMotor.get("intakeMotor");

        prox.setProxRate(0x5);
        prox.setLEDSensitivity(20);

        climbersServo = hardwareMap.servo.get("climbersServo");
        climbersServo.setDirection(Servo.Direction.REVERSE);

        leftAngelArm = hardwareMap.servo.get("leftAngelArm");
        rightAngelArm = hardwareMap.servo.get("rightAngelArm");

    }

    @Override
    public void start() {
        super.start();

        climbersServo.setPosition(restingPosition);

        leftAngelArm.setPosition(0.76);
        rightAngelArm.setPosition(0.17);

        lineColorSensor.enableLed(true);
        gyro.calibrate();
        manipTime.reset();

        startTime.reset();
    }

    /*
         * This method will be called repeatedly in a loop
         *
         * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
         */
    @Override
    public void loop() {
        int proxThreshold = getPreferences().getInt(KEY_LIST[3], 0);
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
        if (stage == 3) {
            if (!gyro.isCalibrating()) {
                double target_angle_degrees = 52; // 307 + 10
                // TODO Fix the gyro reaction motor issue thing
                double error_degrees = target_angle_degrees - gyro.getHeading();
                if (error_degrees > 15) {
                    driveLeft(-0.26);
                    driveRight(0.26);
                } else {
                    driveLeft(-0.21);
                    driveRight(0.21);
                }
                if (gyro.getHeading() <= target_angle_degrees + 2) {
                    if (gyro.getHeading() >= target_angle_degrees - 0) {
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
        if (stage == 4) {
            driveLeft(0);
            driveRight(0);
            DbgLog.msg("Reached degree of: " + String.valueOf(gyro.getHeading()) + ", Time of: " + startTime.time());
            if (waitTime.time() >= 1) {
                DbgLog.msg("Reached degree of: " + String.valueOf(gyro.getHeading()) + ", Time of: " + startTime.time());
                stage++;
            }
        }
        if (stage == 5) {
            if (isOutOfGyroGoal(52)) {
                if (aboveBlueLine()) {
                    leftPower = 0;
                    rightPower = 0;
                    stage = 666;
                }
                if (!aboveBlueLine()) {
                    // Starting left power = 0.65
                    // Starting right power = 0.85
                    // Decrease by .1
                    if (defaultPowerSet == false) {
                        rightPower = 0.4475;
                        leftPower = 0.39;
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
            }
            if (!isOutOfGyroGoal(52)) {
                if (aboveWhiteLine()) {
                    leftPower = 0;
                    rightPower = 0;
                    stage++;
                }
                if (!aboveWhiteLine()) {
                    // Starting left power = 0.65
                    // Starting right power = 0.85
                    // Decrease by .1
                    if (defaultPowerSet == false) {
                        rightPower = 0.445;
                        leftPower = 0.39;
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
            }

            driveLeft(rightPower);
            driveRight(leftPower);
        }
        if (stage == 6) {
            if (manipTime.time() >= 0.5) {
                stage = 9;
            }
        }
        if (stage == 7) {
            if (!aboveWhiteLine()) {
                driveLeft(-0.3);
                driveRight(-0.3);
                waitTime.reset();
            }
            if (aboveWhiteLine()) {
                driveLeft(0);
                driveRight(0);
                manipTime.reset();
                stage++;
            }
        }
        if (stage == 8) {
            if (waitTime.time() >= 0.5) {
                stage++;
            }
        }
        if (stage == 9) {
            if (!gyro.isCalibrating()) {
                double target_angle_degrees2 = 90;
                double error_degrees = target_angle_degrees2 - gyro.getHeading();
                // Left is right and right is left!
                if (error_degrees > 15) {
                    driveLeft(-0.28);
                    driveRight(0.28);
                } else {
                    driveLeft(-0.23);
                    driveRight(0.23);
                }
                if (isGyroInTolerance((int) target_angle_degrees2)) {
                    goalReached[1] = true;
                    stage++;
                }
                if (goalReached[1]) {
                    driveLeft(0);
                    driveRight(0);
                    waitTime.reset();
                }
            }
        }
        if (stage == 10) {
            if (waitTime.time() >= 0.5) {
                stage++;
                manipTime.reset();
            }
        }
        if (stage == 11) {
            if (manipTime.time() >= 0.75) {
                driveLeft(0);
                driveRight(0);
                stage++;
                waitTime.reset();
            }
            if (manipTime.time() < 0.75) {
                driveLeft(-1 * (0.34 + 0.08)); // Right
                driveRight(-0.34); // Left
            }
        }
        if (stage == 12) {
            if (waitTime.time() >= 0.5) {
                stage++;
                manipTime.reset();
            }
        }
        if (stage == 13) {
            if (!gyro.isCalibrating()) {
                double target_angle_degrees3 = 270;
                double error_degrees = target_angle_degrees3 - gyro.getHeading();
                // Left is right and right is left!
                if (error_degrees > 15) {
                    driveLeft(0.325);
                    driveRight(-0.265);
                } else {
                    driveLeft(0.325);
                    driveRight(-0.235);
                }
                if (isGyroInTolerance((int) target_angle_degrees3)) {
                    goalReached[2] = true;
                    stage++;
                }
                if (goalReached[2]) {
                    driveLeft(0);
                    driveRight(0);
                    waitTime.reset();
                }
            }
        }
        if (stage == 14) {
            if (waitTime.time() >= 0.5) {
                stage++;
                manipTime.reset();
            }
        }
        if (stage == 15) {
            if (prox.convertProxToDistance() > proxThreshold) { //This might be confusing but close to the wall, it is a - int
                //The Distance 13 returned from the sensor, is exactly 13 centimeters.
                driveLeft(-1 * (0.315 + 0.1)); // Right
                driveRight(-0.315);
            }
            if (prox.convertProxToDistance() <= proxThreshold) {
                driveLeft(0);
                driveRight(0);
                stage++;
                servotime.reset();
            }
        }
        if (stage == 16) {
            if (servotime.time() > servoDelayTime2) {
                climbersServo.setPosition(Range.clip(servoPosition += servoDelta, restingPosition, 1));
                servotime.reset();
            }
        }

        telemetry.addData("stage", String.valueOf(stage));
        telemetry.addData("motor", String.valueOf(motorRight1.getPower()));
        telemetry.addData("gyro", String.valueOf(gyro.getHeading()));
        telemetry.addData("prox", String.valueOf(prox.convertProxToDistance()));
        //DbgLog.msg(String.valueOf(gyro.getHeading()) + ", " + startTime.time());


        if (stage >= 1 && stage <= 5) {
            intakeMotor.setPower(1);
        } else {
            intakeMotor.setPower(0);
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