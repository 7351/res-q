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

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * DriveToBeacon
 * <p>
 * Drive to the beacon
 */
public class DriveToBeacon extends OpMode {

    // Declare left motor
    DcMotor motorLeft1;
    DcMotor motorLeft2;
    // Declare right motor
    DcMotor motorRight1;
    DcMotor motorRight2;

    ColorSensor lineColorSensor;

    GyroSensor gyro;

    int stage = 0;

    final static int TOLERANCE = 2;

    int calcEncoderValue(double inches) {
        return (int) ((int) (1440 * (inches/(Math.PI * 3)))/3.1);
    }

    public boolean isGyroInTolerance(int degree) {
        boolean returnValue = false;
        if ((gyro.getHeading() <= degree + TOLERANCE) && (gyro.getHeading() >= degree - TOLERANCE)) {
            returnValue = true;
        }
        return returnValue;
    }

    boolean goalReached = false;
    public double getLeftEncoderPos() {
        int sumOfEnc = motorLeft1.getCurrentPosition() + motorLeft2.getCurrentPosition();
        return sumOfEnc/2;
    }

    public double getRightEncoderPos() {
        int sumOfEnc = motorRight1.getCurrentPosition() + motorRight2.getCurrentPosition();
        return sumOfEnc/2;
    }

    public boolean isWithinTolerance(int target) {
        boolean returnValue = false;
        if (((getLeftEncoderPos() <= target + 10) && (getLeftEncoderPos() >= target - 10)) &&
                ((getRightEncoderPos() <= target + 10) && (getRightEncoderPos() >= target - 10))) {
            returnValue = true;
        }
        return returnValue;
    }

    public void runWithEncoders() {
        motorLeft1.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorLeft2.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorRight1.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorRight2.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
    }

    public void setDriveTarget(int target) {
        motorLeft1.setTargetPosition(motorLeft1.getCurrentPosition() + target);
        motorLeft2.setTargetPosition(motorLeft2.getCurrentPosition() + target);
        motorRight1.setTargetPosition(motorRight1.getCurrentPosition() + target);
        motorRight2.setTargetPosition(motorRight2.getCurrentPosition() + target);
    }

    /*
     * Code to run when the op mode is initialized goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#init()
     */
    @Override
    public void init() {

        motorLeft1 = hardwareMap.dcMotor.get("motorleft1");
        motorLeft2 = hardwareMap.dcMotor.get("motorleft2");
        motorRight1 = hardwareMap.dcMotor.get("motorright1");
        motorRight2 = hardwareMap.dcMotor.get("motorright2");
        motorLeft1.setDirection(DcMotor.Direction.REVERSE);
        motorLeft2.setDirection(DcMotor.Direction.REVERSE);

        lineColorSensor = hardwareMap.colorSensor.get("lineColorSensor");

        gyro = hardwareMap.gyroSensor.get("gyro");

    }

    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
    @Override
    public void loop() {

        if (stage == 0) {

            setDriveTarget((int) 103.2);

            runWithEncoders();

            driveLeft(0.95);
            driveRight(0.95);
            stage++;
        } if (stage == 1) {
            if (isWithinTolerance(calcEncoderValue(103.2))) {
                stage++;
                driveLeft(0);
                driveRight(0);
            }
            telemetry.addData("mtrpwrL", "Left: " + motorLeft2.getPower() + ", " + motorLeft1.getPower());
            telemetry.addData("mtrpwrR", "Right: " + motorRight2.getPower() + ", " + motorLeft1.getPower());
        } if (stage == 2) {
            if (!gyro.isCalibrating()) {
                if (isGyroInTolerance(315)) {
                    goalReached = true;
                    stage++;
                }
                if (goalReached) {
                    driveLeft(0);
                    driveRight(0);
                    stage++;
                }
                if (!goalReached) {
                    driveLeft(-0.75);
                    driveRight(0.75);
                }
            }
        } if (stage == 3) {
            setDriveTarget(calcEncoderValue(24));

            runWithEncoders();

            driveLeft(0.75);
            driveRight(0.75);

            stage++;
        } if (stage == 4) {
            if (isWithinTolerance(calcEncoderValue(24))) {
                driveLeft(0);
                driveRight(0);
                stage++;
            }
        } if (stage == 5) {
            
        }

        telemetry.addData("stage", String.valueOf(stage));


    }

    /*
     * Code to run when the op mode is first disabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
     */
    @Override
    public void stop() {

    }

    public void driveLeft(double powerLeft) {
        motorLeft1.setPower(powerLeft);
        motorLeft2.setPower(powerLeft);
    }

    public void driveRight(double powerRight) {
        motorRight1.setPower(powerRight);
        motorRight2.setPower(powerRight);
    }

}