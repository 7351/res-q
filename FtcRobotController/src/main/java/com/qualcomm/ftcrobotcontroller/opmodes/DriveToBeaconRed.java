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

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * DriveToBeacon
 * <p>
 * Drive to the beacon red side
 */
public class DriveToBeaconRed extends DriveTrainLayer {

    final static int TOLERANCE = 2;
    ColorSensor lineColorSensor;
    GyroSensor gyro;
    int stage = 0;
    ElapsedTime manipTime = new ElapsedTime();
    double leftPower = 0;
    double rightPower = 0;
    boolean defaultPowerSet = false;
    ElapsedTime waitTime = new ElapsedTime();
    ElapsedTime startTime = new ElapsedTime();
    boolean goalReached[] = {false, false};
    DcMotor intakeMotor;

    public boolean isGyroInTolerance(int degree) {
        boolean returnValue = false;
        if ((gyro.getHeading() <= degree + TOLERANCE) && (gyro.getHeading() >= degree - TOLERANCE)) {
            returnValue = true;
        }
        return returnValue;
    }

    public boolean aboveWhiteLine (){
        boolean returnValue = false;
        if ((lineColorSensor.red() >= 3) && (lineColorSensor.green() >= 3) && (lineColorSensor.blue() >= 3)) {
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

    //VCNL4010 prox;

    /*
     * Code to run when the op mode is initialized goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#init()
     */
    @Override
    public void init() {

        //prox = new VCNL4010(hardwareMap, "prox");

        super.init();

        lineColorSensor = hardwareMap.colorSensor.get("lineColorSensor");

        gyro = hardwareMap.gyroSensor.get("gyro");
        lineColorSensor.enableLed(false);
        gyro.calibrate();

        intakeMotor = hardwareMap.dcMotor.get("intakeMotor");

    }

    @Override
    public void start() {
        super.start();

        lineColorSensor.enableLed(true);
        gyro.calibrate();
        manipTime.reset();

        startTime.reset();

        //prox.initializeSensor();
        //prox.setModeToPromitiy();
    }

    /*
         * This method will be called repeatedly in a loop
         *
         * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
         */
    @Override
    public void loop() {
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
                double target_angle_degrees = 309; // 307 + 10
                // TODO Fix the gyro reaction motor issue thing
                double error_degrees = target_angle_degrees - gyro.getHeading();
                if (error_degrees > 15) {
                    driveLeft(0.275);
                    driveRight(-0.275);
                } else {
                    driveLeft(0.2);
                    driveRight(-0.2);
                }
                if (gyro.getHeading() <= target_angle_degrees + 2) {
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
            if (aboveWhiteLine()) {
                leftPower = 0;
                rightPower = 0;
                stage++;
            } if (!aboveWhiteLine()) {
                // Starting left power = 0.65
                // Starting right power = 0.85
                // Decrease by .1
                if (defaultPowerSet == false) {
                    rightPower = 0.35;
                    leftPower = 0.35;
                    defaultPowerSet = true;
                }
                if (defaultPowerSet == true) {
                    if (manipTime.time() > 0.1) {
                        leftPower -= 0.002;
                        rightPower -= 0.002;
                        manipTime.reset();
                    }
                }

            }

            driveLeft(rightPower);
            driveRight(leftPower);
        }

        if (stage == 6) {
            if (manipTime.time() >= 1) {
                stage++;
            }
        }
        if (stage == 7) {
            if (!aboveWhiteLine()) {
                driveLeft(-0.3);
                driveRight(-0.3);
                waitTime.reset();
            } if (aboveWhiteLine()) {
                driveLeft(0);
                driveRight(0);
                manipTime.reset();
                stage++;
            }
        }
        if (stage == 8) {
            if (waitTime.time() >= 1) {
                stage++;
            }
        }
        if (stage == 9) {
            if (!gyro.isCalibrating()) {
                double target_angle_degrees2 = 90;
                double error_degrees = target_angle_degrees2 - gyro.getHeading();
                if ( error_degrees > 10) {
                    driveLeft(-0.60);
                    driveRight(0.60);
                } else {
                    driveLeft(-0.5);
                    driveRight(0.5);
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
        /*
        if (stage == 10) {
            if (prox.readProximity() > 13) {
                driveLeft(-0.25);
                driveRight(-0.25);
            } if (prox.readProximity() <= 13) {
                driveLeft(0);
                driveRight(0);
            }
        }*/

        telemetry.addData("stage", String.valueOf(stage));
        telemetry.addData("motor", String.valueOf(motorRight1.getPower()));
        telemetry.addData("gyro", String.valueOf(gyro.getHeading()));
        //telemetry.addData("prox", String.valueOf(prox.readProximity()));
        DbgLog.msg(String.valueOf(gyro.getHeading()) + ", " + startTime.time());


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