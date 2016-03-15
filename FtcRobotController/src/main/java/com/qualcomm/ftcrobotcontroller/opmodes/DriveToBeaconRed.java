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
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * DriveToBeacon
 * <p/>
 * Drive to the beacon red side
 */
public class DriveToBeaconRed extends DriveTrainLayer {

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
        if ((gyro.getHeading() <= degree + 2) && (gyro.getHeading() >= degree - 4)) {
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
        if ((lineColorSensor.red() >= 2) && (lineColorSensor.green() >= 2) && (lineColorSensor.blue() >= 2)) {
            returnValue = true;
        }
        return returnValue;
    }

    public boolean aboveRedLine() {
        boolean returnValue = false;
        if ((lineColorSensor.red() > lineColorSensor.green() + 2) && (lineColorSensor.red() > lineColorSensor.blue() + 2)) {
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

    public void regulateMotorPower() {
        motorLeft1.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorLeft2.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight1.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight2.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
    }

    public void unRegulateMotorPower() {
        motorLeft1.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorLeft2.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorRight1.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorRight2.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
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
            if (waitTime.time() < 1) {
                unRegulateMotorPower();
            }
        }
        if (stage == 3) {
            if (!gyro.isCalibrating()) {
                double target_angle_degrees = 304; // 307 + 10
                // TODO Fix the gyro reaction motor issue thing
                double error_degrees = target_angle_degrees - gyro.getHeading();
                if (error_degrees > 15) {
                    driveLeft(0.21);
                    driveRight(-0.21);
                } else {
                    driveLeft(0.19);
                    driveRight(-0.19);
                }
                if (gyro.getHeading() <= target_angle_degrees + 2) {
                    if (gyro.getHeading() >= target_angle_degrees - 2) {
                        driveLeft(0);
                        driveRight(0);
                        stage++;
                        waitTime.reset();
                        regulateMotorPower();
                    }

                }
                }

            }

        if (stage == 4) {
            driveLeft(0);
            driveRight(0);
            regulateMotorPower();
            if (waitTime.time() >= 1) {
                stage++;
            }
        }
        if (stage == 5) {
            if (aboveWhiteLine()) {
                leftPower = 0;
                rightPower = 0;
                stage++;
            }
            if (!aboveWhiteLine()) {
                // Starting left power = 0.65
                // Starting right power = 0.8
                if (defaultPowerSet == false) {
                    rightPower = 1;
                    leftPower = 1;
                    defaultPowerSet = true;
            }
                if (defaultPowerSet == true) {
                    if (manipTime.time() > 0.1) {
                        leftPower -= 0.00225;
                        rightPower -= 0.00225;
                        manipTime.reset();
                }
            }

        }

            driveLeft(rightPower);
            driveRight(leftPower);
        }
        if (stage == 6) {
            if (waitTime.time() >= 0.5) {
                stage++;
                manipTime.reset();
            }
            if (waitTime.time() <= 0.5) {
                unRegulateMotorPower();
            }
        }
        if (stage == 7) {
            if (!gyro.isCalibrating()) {
                double target_angle_degrees3 = 90;
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

        telemetry.addData("stage", String.valueOf(stage));
        telemetry.addData("motor", String.valueOf(motorRight1.getPower()));
        telemetry.addData("gyro", String.valueOf(gyro.getHeading()));
        telemetry.addData("prox", String.valueOf(prox.convertProxToDistance()));
        DbgLog.msg("R: " + lineColorSensor.red() + ", G: " + lineColorSensor.green() + ", B: " + lineColorSensor.blue());
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