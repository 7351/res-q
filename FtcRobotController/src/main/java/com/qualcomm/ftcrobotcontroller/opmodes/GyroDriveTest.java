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

    private int spoofedZero() {
        return 0;
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
                int targetDegrees = 0;
                double leftStartPower = 1;
                double rightStartPower = 1;
                double dividerNumber = 20.0;

                if (gyro.getHeading() > 0 && gyro.getHeading() <= 90) {
                    int error_degrees = Math.abs(targetDegrees - gyro.getHeading());
                    double subtractivePower = error_degrees / dividerNumber;
                    DbgLog.msg(String.valueOf(subtractivePower + ", " + error_degrees));
                    leftStartPower = Range.clip(1 - subtractivePower, -1, 1);
                }

                if (gyro.getHeading() >= 270 && gyro.getHeading() < 360) {
                    int error_degrees = Math.abs(90 - (gyro.getHeading() - 270));
                    double subtractivePower = error_degrees / dividerNumber;
                    DbgLog.msg(String.valueOf(subtractivePower + ", " + error_degrees));
                    rightStartPower = Range.clip(1 - subtractivePower, -1, 1);
                }

                driveLeft(rightStartPower);
                driveRight(leftStartPower);
                telemetry.addData("gyro", String.valueOf(gyro.getHeading()));
            }

        }

    }

    @Override
    public void stop() {

    }
}
