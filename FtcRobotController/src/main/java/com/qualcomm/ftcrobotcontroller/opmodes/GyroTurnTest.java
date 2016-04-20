package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Leo on 4/9/2016.
 */
public class GyroTurnTest extends DriveTrainLayer {

    GyroSensor gyro;
    private int stage = 0;
    private final int TOLERANCE = 1;

    private int spoofedZero(int zeroDegree) {
        int ActualDegree = gyro.getHeading();
        int degree = ActualDegree - zeroDegree;
        if (degree > 360) {
            degree = degree - 360;
        }
        if (degree < 0) {
            degree = degree + 360;
        }
        return degree;
    }

    private boolean isGyroInTolerance(int degree) {
        boolean returnValue = false;
        if ((gyro.getHeading() <= degree + TOLERANCE) && (gyro.getHeading() >= degree - TOLERANCE)) {
            returnValue = true;
        }
        return returnValue;
    }

    private double getDivisionNumber(int DegreesOff) {
        double divisionNumber = (0.106382979 * DegreesOff) + 113.5;
        return divisionNumber;
    }

    @Override
    public void init() {

        super.init();

        gyro = hardwareMap.gyroSensor.get("gyro");

    }

    @Override
    public void start() {

        gyro.calibrate();

    }

    @Override
    public void loop() {
        if (stage == 0) {
            if (this.time > 4) {
                stage++;
            }
        } if (stage == 1) {
            if (!gyro.isCalibrating()) {
                if (isGyroInTolerance(90)) {
                    powerLeft(0);
                    powerRight(0);
                    stage++;
                } if (!isGyroInTolerance(90)) {
                    final int TargetDegree = 180;

                    int CurrentSpoofedDegree = spoofedZero(270); //An expected 39 gyro value from fake zero
                    if (!isGyroInTolerance(TargetDegree)) {
                        int DegreesOff = Math.abs(TargetDegree - CurrentSpoofedDegree);
                        double DivisionNumber = getDivisionNumber(DegreesOff);
                        double RawPower = Range.clip(DegreesOff / DivisionNumber, 0, 1);
                        powerLeft(RawPower);
                        powerRight(-RawPower);
                    }
                }

            }
        }

    }
}
