package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Leo on 3/29/2016.
 */
public class GyroDriveTest extends DriveTrainLayer {
    GyroSensor gyro;
    private int stage = 0;
    private int targetDegrees;

    public GyroDriveTest(int desiredDegree) {
        this.targetDegrees = desiredDegree;
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

    private double getDivideNumber(int CurrentDegreesOff) {
        double divideNumber = -0.5 * CurrentDegreesOff + 20;
        return divideNumber;
    }

    @Override
    public void loop() {
        if (stage == 0) {
            if (this.time > 4) {
                stage++;
            }
        }
        if (stage == 1) {

            if (!gyro.isCalibrating()) {
                int gyroDegree = spoofedZero(targetDegrees);
                double leftStartPower = 1;
                double rightStartPower = 1;

                if (gyroDegree > 0 && gyroDegree <= 90) {
                    int error_degrees = Math.abs(targetDegrees - gyroDegree);
                    double dividerNumber = getDivideNumber(error_degrees);
                    double subtractivePower = error_degrees / dividerNumber;
                    DbgLog.msg(String.valueOf(subtractivePower + ", " + error_degrees));
                    leftStartPower = Range.clip(1 - subtractivePower, -1, 1);
                }

                if (gyroDegree >= 270 && gyroDegree < 360) {
                    int error_degrees = Math.abs(90 - (gyroDegree - 270));
                    double dividerNumber = getDivideNumber(error_degrees);
                    double subtractivePower = error_degrees / dividerNumber;
                    DbgLog.msg(String.valueOf(subtractivePower + ", " + error_degrees));
                    rightStartPower = Range.clip(1 - subtractivePower, -1, 1);
                }

                powerLeft(leftStartPower);
                powerRight(rightStartPower);
                telemetry.addData("gyro", String.valueOf(gyro.getHeading()));
                telemetry.addData("spoofed", String.valueOf(spoofedZero(targetDegrees)));
            }

        }

    }

    @Override
    public void stop() {

    }
}
