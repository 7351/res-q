/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
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

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * DriveToBeacon
 * <p>
 * Drive to the beacon
 */
public class DriveToBeacon extends DriveTrainLayer {

    ColorSensor lineColorSensor;

    GyroSensor gyro;

    int stage = 0;

    final static int TOLERANCE = 2 ;

    public boolean isGyroInTolerance(int degree) {
        boolean returnValue = false;
        if ((gyro.getHeading() <= degree + TOLERANCE) && (gyro.getHeading() >= degree - TOLERANCE)) {
            returnValue = true;
        }
        return returnValue;
    }

    public boolean aboveWhiteLine (){
        boolean returnValue = false;
        if ((lineColorSensor.red() >= 5) && (lineColorSensor.green() >= 5) && (lineColorSensor.blue() >= 5)) {
            returnValue = true;
        }
        return returnValue;
    }

    ElapsedTime manipTime = new ElapsedTime();

    double leftPower = 0;
    double rightPower = 0;

    boolean defaultPowerSet = false;

    ElapsedTime waitTime = new ElapsedTime();

    boolean goalReached[] = {false, false};


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

    }

    @Override
    public void start() {
        super.start();

        lineColorSensor.enableLed(true);
        gyro.calibrate();
        manipTime.reset();
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
                if (isGyroInTolerance(308)) {
                    goalReached[0] = true;
                    stage++;
                }
                if (goalReached[0]) {
                    driveLeft(0);
                    driveRight(0);
                    waitTime.reset();
                }
                if (!goalReached[0]) {
                    driveLeft(-0.5);
                    driveRight(0.5);
                }
            }

        }
        if (stage == 4) {
            if (waitTime.time() >= 1) {
                stage++;
            }
        }
        if (stage == 5) {
            if (aboveWhiteLine()) {
                leftPower = 0;
                rightPower = 0;
                stage++;
            } if (!aboveWhiteLine()) {
                // Starting power -0.6
                if (defaultPowerSet == false) {
                    leftPower = 0.55;
                    rightPower = 0.55;
                    defaultPowerSet = true;
                }
                if (defaultPowerSet == true) {
                    if (manipTime.time() > 0.1) {
                        leftPower -= 0.0095;
                        rightPower -= 0.0095;
                        manipTime.reset();
                    }
                }

            }

            driveLeft(leftPower);
            driveRight(rightPower);
        }
        if (stage == 6) {
            if (manipTime.time() >= 1) {
                stage++;
            }
        }
        if (stage == 7) {
            if (!aboveWhiteLine()) {
                driveLeft(-0.4);
                driveRight(-0.4);
            } if (aboveWhiteLine()) {
                driveLeft(0);
                driveRight(0);
                manipTime.reset();
                stage++;
            }
        }
        if (stage == 8) {
            if (!gyro.isCalibrating()) {
                if (isGyroInTolerance(90)) {
                    goalReached[1] = true;
                    stage++;
                }
                if (goalReached[1]) {
                    driveLeft(0);
                    driveRight(0);
                    waitTime.reset();
                }
                if (!goalReached[1]) {
                    driveLeft(0.55);
                    driveRight(-0.55);
                }
            }

        }

        telemetry.addData("stage", String.valueOf(stage));
        telemetry.addData("motor", String.valueOf(motorRight1.getPower()));
        telemetry.addData("gyro", String.valueOf(gyro.getHeading()));


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