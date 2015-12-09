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

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * DriveToFloorGaol
 * <p>
 * Drive to the floor goal from the red side. closer to wall.
 */
public class DriveToFloorGoalRed extends DriveTrainLayer {

    ColorSensor lineColorSensor;

    GyroSensor gyro;

    int stage = 0;

    ElapsedTime manipTime = new ElapsedTime();

    final static int TOLERANCE = 0;

    public boolean isGyroInTolerance(int degree) {
        boolean returnValue = false;
        if ((gyro.getHeading() <= degree + TOLERANCE) && (gyro.getHeading() >= degree - TOLERANCE)) {
            returnValue = true;
        }
        return returnValue;
    }

    boolean goalReached[] = {false, false};

    public boolean aboveRedLine (){
        boolean returnValue = false;
        if ((lineColorSensor.red() > lineColorSensor.green() + 5) && (lineColorSensor.red() > lineColorSensor.blue() + 5)) {
            returnValue = true;
        }
        return returnValue;
    }

    double leftPower = 0;
    double rightPower = 0;

    boolean defaultPowerSet = false;


    /*
     * Code to run when the op mode is initialized goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#init()
     */
    @Override
    public void init() {

        super.init();

        gyro = hardwareMap.gyroSensor.get("gyro");

        lineColorSensor = hardwareMap.colorSensor.get("lineColorSensor");

    }

    /*
     * Function will be called when start button is pushed
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
    @Override
    public void start() {
        gyro.calibrate();
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
                stage++;
                manipTime.reset();
            }
        }
        if (stage == 1) {
            if (!gyro.isCalibrating()) {
                driveLeft(0.6);
                driveRight(0.6);
                if (manipTime.time() >= 0.3) {
                    driveLeft(0);
                    driveRight(0);
                    stage++;
                    manipTime.reset();
                }
            }
        } if (stage == 2) {
            if (manipTime.time() >= 1) {
                stage++;
            }
        } if (stage == 3) {
            if (!gyro.isCalibrating()) {
                double target_angle_degrees = 316;
                double error_degrees = target_angle_degrees - gyro.getHeading();
                if ( error_degrees > 20) {
                    driveLeft(-0.45);
                    driveRight(0.45);
                } else {
                    driveLeft(-0.33);
                    driveRight(0.33);
                }
                if (isGyroInTolerance((int) target_angle_degrees)) {
                    goalReached[0] = true;
                    stage++;
                }
                if (goalReached[0]) {
                    driveLeft(0);
                    driveRight(0);
                    manipTime.reset();
                }
            }
        }
        if (stage == 4) {
            if (manipTime.time() >= 1) {
                stage++;
            }
        }
        if (stage == 5) {
            if (aboveRedLine()) {
                leftPower = 0;
                rightPower = 0;
                stage++;
            } if (!aboveRedLine()) {
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

        telemetry.addData("stage", String.valueOf(stage));
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