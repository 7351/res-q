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

import android.graphics.Color;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

import java.lang.reflect.Array;

/**
 * EmptyOp Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
public class Scenario1 extends OpMode {

    DcMotor motorleft;
    DcMotor motorright;

    ColorSensor color;

    final static int TOLERANCE = 4;

    int stage = 0;

    final static int GEAR_RATIO = 2;
    final static int WHEEL_DIAMETER = 4;

    int[][] distanceArray = {{
    /* 0 | Left */        -24, -3, 23, 2, 48
    }, {
    /* 1 | Right */        -24, 3, 23, -2, 48
    }};//                   0   1   2  3   4

    double calcEncoderValue(double inches) {
        return (1440 * (inches/(Math.PI * WHEEL_DIAMETER)))/GEAR_RATIO;
    }

    public void setDriveChannelMode (DcMotorController.RunMode mode) {
        motorleft.setChannelMode(mode);
        motorright.setChannelMode(mode);
    }

    public void setDrivePower (double power) {
        motorleft.setPower(power);
        motorright.setPower(power);
    }

    // Tolerance up to 0.035 of an inch calculated by 2=(1440*(x/(3.14*4)))/2
    public boolean isWithinTolerance (double distanceLeft, double distanceRight) {
        boolean lefttolerance = false;
        boolean righttolerance = false;
        if ((motorleft.getCurrentPosition() <= distanceLeft + TOLERANCE) && (motorleft.getCurrentPosition() >= distanceLeft - TOLERANCE)) {
            lefttolerance = true;
        } if ((motorright.getCurrentPosition() <= distanceRight + TOLERANCE) && (motorright.getCurrentPosition() >= distanceRight - TOLERANCE)) {
            righttolerance = true;
        } if (lefttolerance && righttolerance) {
            return true;
        } else {
            return false;
        }
    }

    public String telementryTargetEnc(int arrayNumber) {
        return calcEncFromArray(0, arrayNumber) + ", " + calcEncFromArray(1, arrayNumber);
    }

    public double calcEncFromArray(int a, int b) {
        return calcEncoderValue(distanceArray[a][b]);
    }


	/*
	 * Code to run when the op mode is initialized goes here
	 *
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#init()
	 */
	@Override
	public void init() {

        motorleft = hardwareMap.dcMotor.get("motorleft");
        motorright = hardwareMap.dcMotor.get("motorright");
        motorleft.setDirection(DcMotor.Direction.REVERSE);

        color = hardwareMap.colorSensor.get("color");

        color.enableLed(false);

	}

	/*
	 * This method will be called repeatedly in a loop
	 *
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {

        if (stage == 0) {
            // Emptying the climbers code goes into here
            stage++;
        } if (stage == 1) {
            // Scanning for color and pushing button goes here
            int currentcolor = color.argb();
            if (currentcolor == Color.RED){
                // Code for having robot push red button
            } if (currentcolor == Color.BLUE) {
                // Code for having robot push blue button
            } else {
                DbgLog.msg("Could not find out color");
            }
            stage++;

        } if (stage == 2) {
            // Reset Encoders
            setDriveChannelMode(DcMotorController.RunMode.RESET_ENCODERS);

            // Set encoders to run to a certain position
            setDriveChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);


            // Set target positon by using calcEncoderValue
            motorleft.setTargetPosition((int) calcEncFromArray(0, 0));
            motorright.setTargetPosition((int) calcEncFromArray(1, 0));

            // Set motor power
            setDrivePower(0.2);
            stage++;


        } if (stage == 3) {
            if (isWithinTolerance(calcEncFromArray(0, 0), calcEncFromArray(1, 0))) {
                setDriveChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
                stage++;
            }
            telemetry.addData("enc target", telementryTargetEnc(0));


        } if (stage == 4) {
            // Reset Encoders
            setDriveChannelMode(DcMotorController.RunMode.RESET_ENCODERS);

            // Set encoders to run to a certain position
            setDriveChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);


            // Set target positon by using calcEncoderValue
            motorleft.setTargetPosition((int) calcEncFromArray(0, 1));
            motorright.setTargetPosition((int) calcEncFromArray(1, 1));

            // Set motor power
            setDrivePower(0.2);
            stage++;


        } if (stage == 5) {
            if (isWithinTolerance(calcEncFromArray(0, 1), calcEncFromArray(1, 1))) {
                setDriveChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
                stage++;
            }
            telemetry.addData("enc target", telementryTargetEnc(1));

        } if (stage == 6) {
            // Reset Encoders
            setDriveChannelMode(DcMotorController.RunMode.RESET_ENCODERS);

            // Set encoders to run to a certain position
            setDriveChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);


            // Set target positon by using calcEncoderValue
            motorleft.setTargetPosition((int) calcEncFromArray(0, 2));
            motorright.setTargetPosition((int) calcEncFromArray(1, 2));

            // Set motor power
            setDrivePower(0.2);
            stage++;


        } if (stage == 7) {
            if (isWithinTolerance(calcEncFromArray(0, 2), calcEncFromArray(1, 2))) {
                setDriveChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
                stage++;
            }
            telemetry.addData("enc target", telementryTargetEnc(2));


        } if (stage == 7) {
            // Reset Encoders
            setDriveChannelMode(DcMotorController.RunMode.RESET_ENCODERS);

            // Set encoders to run to a certain position
            setDriveChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);


            // Set target positon by using calcEncoderValue
            motorleft.setTargetPosition((int) calcEncFromArray(0, 3));
            motorright.setTargetPosition((int) calcEncFromArray(1, 3));

            // Set motor power
            setDrivePower(0.2);
            stage++;


        } if (stage == 8) {
            if (isWithinTolerance(calcEncFromArray(0, 3), calcEncFromArray(1, 3))) {
                setDriveChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
                stage++;
            }
            telemetry.addData("enc target", telementryTargetEnc(3));


        } if (stage == 9) {
            // Reset Encoders
            setDriveChannelMode(DcMotorController.RunMode.RESET_ENCODERS);

            // Set encoders to run to a certain position
            setDriveChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);


            // Set target positon by using calcEncoderValue
            motorleft.setTargetPosition((int) calcEncFromArray(0, 4));
            motorright.setTargetPosition((int) calcEncFromArray(1, 4));

            // Set motor power
            setDrivePower(0.2);
            stage++;


        } if (stage == 10) {
            if (isWithinTolerance(calcEncFromArray(0,4), calcEncFromArray(1, 4))) {
                setDriveChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
                stage++;
            }
            telemetry.addData("enc target", telementryTargetEnc(4));
        }

    telemetry.addData("stage", stage);


	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {

	}

	// Scaling input has been moved to BasicFunctions
}
