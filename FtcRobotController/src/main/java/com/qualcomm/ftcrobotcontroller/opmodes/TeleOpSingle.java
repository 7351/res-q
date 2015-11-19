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
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * TeleOpDouble Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
public class TeleOpSingle extends OpMode {


    // Initial scaling power
    double scalePower = 0.65;

    // Declare left motor
    DcMotor motorLeft1;
    DcMotor motorLeft2;
    // Declare right motor
    DcMotor motorRight1;
    DcMotor motorRight2;

    GyroSensor gyro;

    Servo climbersServo;

    Servo ziplinerServo;

    Servo boxServo;

    DcMotor intakeMotor;
    DcMotor liftMotor;

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

        gyro = hardwareMap.gyroSensor.get("gyro");

        climbersServo = hardwareMap.servo.get("climbersServo");

        gyro.calibrate();

        climbersServo.setPosition(0);

        ziplinerServo = hardwareMap.servo.get("ziplinersServo");

        boxServo = hardwareMap.servo.get("boxServo");
        intakeMotor = hardwareMap.dcMotor.get("intakeMotor");
        liftMotor = hardwareMap.dcMotor.get("liftMotor");
        boxServo.setPosition(1);

        liftMotor.setDirection(DcMotor.Direction.REVERSE);

        ziplinerServo.setPosition(1);

    }

    @Override
    public void start() {
        climbersServo.setPosition(0.25);

    }

    /*
         * This method will be called repeatedly in a loop
         *
         * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
         */
    @Override
    public void loop() {


        float leftTriger = gamepad1.left_trigger;
        float rightTrigger = gamepad1.right_trigger;
        float leftYStick = gamepad1.left_stick_y;
        boolean buttonXPressed = gamepad1.dpad_left;
        boolean leftBumper = gamepad1.left_bumper;


        liftMotor.setPower(scaleInput(leftYStick));
        if (scaleInput(leftYStick) <= 0) {
            liftMotor.setPower(scaleInput(leftYStick));
        } else {
            liftMotor.setPower(scaleInput(leftYStick * 0.25));
        }

        if (rightTrigger != 0) {
            intakeMotor.setPower(1);
        } if (rightTrigger == 0) {
            intakeMotor.setPower(0);
        }

        if (leftTriger != 0) {
            intakeMotor.setPower(-1);
        } if (leftTriger == 0) {
            intakeMotor.setPower(0);
        }

        if (buttonXPressed) {
            ziplinerServo.setPosition(0.05);
        } if (!buttonXPressed) {
            ziplinerServo.setPosition(1);
        }

        if (leftBumper) {
            scalePower = 0.75;
        } if (!leftBumper) {
            scalePower = 1;
        }
		/*
		 * Gamepad 1
		 *
		 * Gamepad 1 controls the motors via the right stick
		 */

        // throttle: right_stick_y ranges from -1 to 1, where -1 is full up, and
        // 1 is full down
        // direction: right_stick_x ranges from -1 to 1, where -1 is full left
        // and 1 is full right
        float throttle = gamepad1.right_stick_y;
        float direction = -gamepad1.right_stick_x;
        float right = throttle - direction;
        float left = throttle + direction;

        // clip the right/left values so that the values never exceed +/- 1
        right = Range.clip(right, -1, 1);
        left = Range.clip(left, -1, 1);

        // scale the joystick value to make it easier to control
        // the robot more precisely at slower speeds.
        right = (float)scaleInput(right * scalePower);
        left =  (float)scaleInput(left * scalePower);

        // write the values to the motors
        setDrivePower((double) left, (double) right);

		/*
		 * Send telemetry data back to driver station. Note that if we are using
		 * a legacy NXT-compatible motor controller, then the getPower() method
		 * will return a null value. The legacy NXT-compatible motor controllers
		 * are currently write only.
		 */
        telemetry.addData("left tgt pwr", "left  pwr: " + String.format("%.2f", left));
        telemetry.addData("right tgt pwr", "right pwr: " + String.format("%.2f", right));
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

    public void setDrivePower(Double power1, Double power2) {
        motorLeft1.setPower(power1);
        motorLeft2.setPower(power1);
        motorRight1.setPower(power2);
        motorRight2.setPower(power2);

    }

    /*
    Stop all drive motor power in one simple command
     */
    public void stopDriveMotors(){
        setDrivePower(0.0, 0.0);
    }

    /*
     * This method scales the joystick input so for low joystick values, the
	 * scaled value is less than linear.  This is to make it easier to drive
	 * the robot more precisely at slower speeds.
	 */
    double scaleInput(double dVal) {
        double[] scaleArray = {0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00};

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);
        if (index < 0) {
            index = -index;
        } else if (index > 16) {
            index = 16;
        }

        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        return dScale;
    }
}