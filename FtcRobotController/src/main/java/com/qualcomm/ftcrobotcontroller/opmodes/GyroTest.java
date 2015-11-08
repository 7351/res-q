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
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * GyroTest
 * <p>
 * Testing op mode for gyroscope sensor
 */
public class GyroTest extends OpMode {

	GyroSensor gyro;

    DcMotor motorleft1;
    DcMotor motorleft2;
    DcMotor motorright1;
    DcMotor motorright2;

    boolean goalReached = false;

    public final int TOLERANCE = 10;

    double calcEncoderValue(double inches) {
        return (1440 * (inches/(Math.PI * 4)))/2;
    }

    public int stage = 0;

    public boolean isGyroInTolerance(int degree) {
        boolean returnValue = false;
        if ((gyro.getHeading() <= degree + TOLERANCE) && (gyro.getHeading() >= degree - TOLERANCE)) {
            returnValue = true;
        }
        return returnValue;
    }


	/*
	 * Code to run when the op mode is initialized goes here
	 *
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#init()
	 */
	@Override
	public void init() {

        gyro = hardwareMap.gyroSensor.get("gyro");

        motorleft1 = hardwareMap.dcMotor.get("motorleft1");
        motorleft2 = hardwareMap.dcMotor.get("motorleft2");
        motorright1 = hardwareMap.dcMotor.get("motorright1");
        motorright2 = hardwareMap.dcMotor.get("motorright2");
        motorleft1.setDirection(DcMotor.Direction.REVERSE);
        motorleft2.setDirection(DcMotor.Direction.REVERSE);
        gyro.calibrate();

	}

    /*
	 * Code to run when the start button is clicked
	 *
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
    @Override
    public void start() {

    }

	/*
	 * This method will be called repeatedly in a loop
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {
            if (!gyro.isCalibrating()) {
                if (isGyroInTolerance(90)) {
                    goalReached = true;
                    stage++;
                }
                if (goalReached) {
                    driveLeft(0);
                    driveRight(0);
                }
                if (!goalReached) {
                    driveLeft(-0.75);
                    driveRight(0.75);
                }
            }





        telemetry.addData("enc ps", String.valueOf(motorleft2.getCurrentPosition()) + ", " + String.valueOf(motorright2.getCurrentPosition()));
        telemetry.addData("gyroHead", String.valueOf(gyro.getHeading()));


	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {

	}

    public void driveLeft (double power) {
        motorleft1.setPower(power);
        motorleft2.setPower(power);
    }

    public void driveRight (double power) {
        motorright1.setPower(power);
        motorright2.setPower(power);
    }

}
