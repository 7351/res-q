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

    @Override
    public void loop() {
        int degree = spoofedZero(345);
        if (stage == 0) {
            if (this.time > 4) {
                stage++;
            }
        }
        if (stage == 1) {

            if (!gyro.isCalibrating()) {
                int gyroDegree = spoofedZero(309);
                int targetDegrees = 0;
                double leftStartPower = 1;
                double rightStartPower = 1;
                double dividerNumber = 15.0;

                if (gyroDegree > 0 && gyroDegree <= 90) {
                    int error_degrees = Math.abs(targetDegrees - gyroDegree);
                    double subtractivePower = error_degrees / dividerNumber;
                    DbgLog.msg(String.valueOf(subtractivePower + ", " + error_degrees));
                    leftStartPower = Range.clip(1 - subtractivePower, -1, 1);
                }

                if (gyroDegree >= 270 && gyroDegree < 360) {
                    int error_degrees = Math.abs(90 - (gyroDegree - 270));
                    double subtractivePower = error_degrees / dividerNumber;
                    DbgLog.msg(String.valueOf(subtractivePower + ", " + error_degrees));
                    rightStartPower = Range.clip(1 - subtractivePower, -1, 1);
                }

                driveLeft(rightStartPower);
                driveRight(leftStartPower);
                telemetry.addData("gyro", String.valueOf(gyro.getHeading()));
                telemetry.addData("spoofed", String.valueOf(spoofedZero(345)));
            }

        }

    }

    @Override
    public void stop() {

    }
}
