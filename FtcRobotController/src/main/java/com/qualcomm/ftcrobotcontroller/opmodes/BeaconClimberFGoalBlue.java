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

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * BeaconClimbersSitBlue autonomous
 * <p>
 * Autonomous mode to dump climbers into bucket and hit the beacon with colorSensor, then move to floor goal
 */
public class BeaconClimberFGoalBlue extends OpMode {

    GyroSensor gyro;

    DcMotor motorleft1;
    DcMotor motorleft2;
    DcMotor motorright1;
    DcMotor motorright2;

    // Define objects

    ColorSensor colorSensor;

    Servo colorServo;

    Servo climbersServo;

    Servo ziplinersServo;

    // Variables for controlling speed on the climbersServo
    private ElapsedTime servotime = new ElapsedTime();
    private double servoPosition;

    //tweak these values for desired speed
    private double servoDelta = 0.01;
    private double servoDelayTime = 0.003;

    private final boolean redMode = false;

    // Determines what color the robot is seeing in string form
    String colorROB () {
        String returnString = "Unknown";
        if (colorSensor.red() > colorSensor.blue()) {
            returnString = "Red";
        } if (colorSensor.blue() > colorSensor.red()) {
            returnString = "Blue";
        }
        return returnString;
    }

    public final int TOLERANCE = 3;

    public int stage = 0;

    public boolean isGyroInTolerance(int degree) {
        boolean returnValue = false;
        if ((gyro.getHeading() <= degree + TOLERANCE) && (gyro.getHeading() >= degree - TOLERANCE)) {
            returnValue = true;
        }
        return returnValue;
    }

    ElapsedTime runTime = new ElapsedTime();



    /*
     * Code to run when the op mode is initialized goes here
     * 
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#init()
     */
    @Override
    public void init() {

        // Get variables from hardwaremap

        colorSensor = hardwareMap.colorSensor.get("colorSensor");

        colorServo = hardwareMap.servo.get("colorServo");
        climbersServo = hardwareMap.servo.get("climbersServo");
        colorSensor.enableLed(false);

        // Set servo positions
        climbersServo.setPosition(0);

        colorServo.setPosition(0.4);

        gyro = hardwareMap.gyroSensor.get("gyro");

        motorleft1 = hardwareMap.dcMotor.get("motorleft1");
        motorleft2 = hardwareMap.dcMotor.get("motorleft2");
        motorright1 = hardwareMap.dcMotor.get("motorright1");
        motorright2 = hardwareMap.dcMotor.get("motorright2");
        motorleft1.setDirection(DcMotor.Direction.REVERSE);
        motorleft2.setDirection(DcMotor.Direction.REVERSE);
        gyro.calibrate();

        ziplinersServo = hardwareMap.servo.get("ziplinersServo");

    }

    @Override
    public void start() {

        runTime.reset();


    }

    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
    @Override
    public void loop() {

        ziplinersServo.setPosition(1);


        if (stage == 0) {
        /*
        // Set climber servo with modified speed
        */
            if (servotime.time() > servoDelayTime) {
                climbersServo.setPosition(Range.clip(servoPosition += servoDelta, 0, 0.9));
                servotime.reset();
            }
            // Angle statements
            if (redMode) {
                if (colorSensor.red() > colorSensor.blue()) {
                    DbgLog.msg(colorROB());
                    colorServo.setPosition(0.85);
                }
                if (colorSensor.red() < colorSensor.blue()) {
                    DbgLog.msg(colorROB());
                    colorServo.setPosition(0);
                }
            }
            if (!redMode) {
                if (colorSensor.red() > colorSensor.blue()) {
                    DbgLog.msg(colorROB());
                    colorServo.setPosition(0);
                }
                if (colorSensor.red() < colorSensor.blue()) {
                    DbgLog.msg(colorROB());
                    colorServo.setPosition(0.85);
                }
            }
            // Default position if no color is detected
            if (colorROB().equals("Unknown")) {
                colorServo.setPosition(0.4);
            }
            if (runTime.time() >= 3.5) {
                stage++;
            }

            telemetry.addData("color", colorROB());
            telemetry.addData("servoPos", String.valueOf(colorServo.getPosition() * 180));
        }
        if (stage == 1) {
            climbersServo.setPosition(0);
            if (!redMode) {
                if (!gyro.isCalibrating()) {
                    double motor_output = ((90 - gyro.getHeading()) / 180.0) + ((90 - gyro.getHeading()) > 0 ? .35 : 0);
                    driveLeft(-motor_output);
                    driveRight(motor_output);
                    if (isGyroInTolerance(1)) {
                        driveLeft(0);
                        driveRight(0);
                        stage++;
                    }
                }
            }
            if (redMode) {
                if (!gyro.isCalibrating()) {
                    double motor_output = ((270 - gyro.getHeading()) / 180.0) + ((270 - gyro.getHeading()) > 0 ? .35 : 0);
                    driveLeft(motor_output);
                    driveRight(-motor_output);
                    if (isGyroInTolerance(1)) {
                        driveLeft(0);
                        driveRight(0);
                        stage++;
                    }
                }
            }

        } if (stage == 2) {
            if (runTime.time() < 1) {
                driveLeft(0.75);
                driveRight(0.75);
            }
            if (runTime.time() >= 1) {
                driveLeft(0);
                driveRight(0);
            }
        }





        // Telementry return data
        telemetry.addData("stage", String.valueOf(stage));
        telemetry.addData("heading", gyro.getHeading());


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