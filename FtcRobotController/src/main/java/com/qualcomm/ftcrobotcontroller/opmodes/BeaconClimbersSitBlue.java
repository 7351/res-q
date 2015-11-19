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
 * BeaconClimbersSitRed autonomous
 * <p>
 * Autonomous mode to dump climbers into bucket and hit the beacon with colorSensor
 */
public class BeaconClimbersSitBlue extends OpMode {

    // Define objects

    ColorSensor colorSensorBeacon;

    ColorSensor lineColorSensor;

    Servo colorServo;

    Servo climbersServo;

    // Declare left motor
    DcMotor motorLeft1;
    DcMotor motorLeft2;
    // Declare right motor
    DcMotor motorRight1;
    DcMotor motorRight2;

    GyroSensor gyro;

    // Variables for controlling speed on the climbersServo
    private ElapsedTime servotime = new ElapsedTime();
    private double servoPosition;
    private ElapsedTime startTime = new ElapsedTime();

    //tweak these values for desired speed
    private double servoDelta = 0.01;
    private double servoDelayTime = 0.003;

    private final boolean redMode = false;

    Servo ziplinersServo;

    int stage = 0;

    // Determines what color the robot is seeing in string form
    String colorROB () {
        String returnString = "Unknown";
        if (colorSensorBeacon.red() > colorSensorBeacon.blue()) {
            returnString = "Red";
        } if (colorSensorBeacon.blue() > colorSensorBeacon.red()) {
            returnString = "Blue";
        }
        return returnString;
    }

    boolean whiteLineReached = false;

    public boolean ifOverWhite() {
        boolean returnValue = false;
        if ((lineColorSensor.red() >= 5) && (lineColorSensor.green() >= 5) && (lineColorSensor.blue() >= 5)) {
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

        // Get variables from hardwaremap

        motorLeft1 = hardwareMap.dcMotor.get("motorleft1");
        motorLeft2 = hardwareMap.dcMotor.get("motorleft2");
        motorRight1 = hardwareMap.dcMotor.get("motorright1");
        motorRight2 = hardwareMap.dcMotor.get("motorright2");
        motorLeft1.setDirection(DcMotor.Direction.REVERSE);
        motorLeft2.setDirection(DcMotor.Direction.REVERSE);

        colorSensorBeacon = hardwareMap.colorSensor.get("colorSensorBeacon");

        lineColorSensor = hardwareMap.colorSensor.get("lineColorSensor");

        gyro = hardwareMap.gyroSensor.get("gyro");
        gyro.calibrate();

        colorServo = hardwareMap.servo.get("colorServo");
        climbersServo = hardwareMap.servo.get("climbersServo");
        colorSensorBeacon.enableLed(false);

        // Set servo positions
        climbersServo.setPosition(0);

        colorServo.setPosition(0.4);

        ziplinersServo = hardwareMap.servo.get("ziplinersServo");

    }

    @Override
    public void start() {
        startTime.reset();


    }

    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
    @Override
    public void loop() {
        if (stage == 0 ) {
            while (!whiteLineReached) {
                driveLeft(-0.65);
                driveRight(-0.65);
            }
            if (ifOverWhite()) {
                whiteLineReached = true;
                driveLeft(0);
                driveRight(0);
                stage++;
                gyro.calibrate();
            }
        } if (stage == 1) {

        }

        ziplinersServo.setPosition(1);
        if (startTime.time() <= 3) {
            /*
        // Set climber servo with modified speed

        */
            if( servotime.time() > servoDelayTime ) {
                climbersServo.setPosition(Range.clip(servoPosition += servoDelta, 0, 0.9));
                servotime.reset();
            }


            // Angle statements
            if (redMode) {
                if (colorSensorBeacon.red() > colorSensorBeacon.blue()) {
                    DbgLog.msg(colorROB());
                    colorServo.setPosition(0.85);
                } if (colorSensorBeacon.red() < colorSensorBeacon.blue()) {
                    DbgLog.msg(colorROB());
                    colorServo.setPosition(0);
                }
            } if (!redMode) {
                if (colorSensorBeacon.red() > colorSensorBeacon.blue()) {
                    DbgLog.msg(colorROB());
                    colorServo.setPosition(0);
                }
                if (colorSensorBeacon.red() < colorSensorBeacon.blue()) {
                    DbgLog.msg(colorROB());
                    colorServo.setPosition(0.85);
                }
            }

            // Default position if no color is detected
            if (colorROB().equals("Unknown")) {
                colorServo.setPosition(0.4);
            }
        } else {
            colorServo.setPosition(0.4);
            climbersServo.setPosition(0);
        }





        // Telementry return data
        telemetry.addData("White?", String.valueOf(ifOverWhite()));

        telemetry.addData("servoPos", String.valueOf(colorServo.getPosition()*180));
        telemetry.addData("time", String.valueOf(servotime));


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