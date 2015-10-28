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
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Beacon_Climbers_Bridge
 * <p>
 * Dumps climbers into beacon while pushing the correct button for color
 */
public class Beacon_Climbers_Bridge extends OpMode {

    // Initialize ColorSensor, Servos, and Motors
    ColorSensor colorSensor;

    Servo colorChooserServo;
    Servo climbersServo;

    DcMotor driveMotorLeft;
    DcMotor driveMotorRight;

    // ElapsedTime variable used for the slow speed on Climbers servo
    private ElapsedTime servotime = new ElapsedTime();
    private double servoPosition;

    // Change these variables for speed on the Climbers motor
    private double servoDelta = 0.01;
    private double servoDelayTime = 0.0035;

    // Alliance specific code is in this program
    // If you are on the Red Alliance change to true
    // If you are on the Blue Alliance change to false
    private final boolean redMode = true;

    // TODO duplicated stable code for both alliance, but for now just stick with Red Alliance

    // Convert degrees of the servo (out of 180) to decimal
    double getDecimalFromAngle (int angle) {
        return Range.clip((angle * 0.5)/180, 0, 1);
    }

    // What color is the colorSensor detecting, Red or Blue?
    public String colorSensorROB() {
        if (colorSensor.red() > colorSensor.blue()) {
            return "Red";
        } if (colorSensor.blue() > colorSensor.red()) {
            return "Blue";
        } else {
            return "Unknown";
        }
    }



    /*
     * Code to run when the op mode is initialized goes here
     * 
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#init()
     */
    @Override
    public void init() {

        colorSensor = hardwareMap.colorSensor.get("colorSensor");

        colorChooserServo = hardwareMap.servo.get("colorServo");
        colorSensor.enableLed(false);

        climbersServo = hardwareMap.servo.get("climbersServo");

        driveMotorLeft = hardwareMap.dcMotor.get("driveLeft");
        driveMotorRight = hardwareMap.dcMotor.get("driveRight");
        driveMotorLeft.setDirection(DcMotor.Direction.REVERSE);

        climbersServo.setPosition(getDecimalFromAngle(0));
        colorChooserServo.setPosition(getDecimalFromAngle(65));

    }

    /*
     * This code will be ran when the start button is hit
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
    @Override
    public void start() {

        // Slowly set the servo to 130 degrees
        if( servotime.time() > servoDelayTime ) {
            climbersServo.setPosition(Range.clip(servoPosition += servoDelta, 0, getDecimalFromAngle(130)));
            servotime.reset();
        }


    }

    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
    @Override
    public void loop() {

        // Code for red alliance
        if (redMode) {
            // If Red is detected, set servo to 150
            if (colorSensor.red() > colorSensor.blue()) {
                DbgLog.msg("Red");
                colorChooserServo.setPosition(getDecimalFromAngle(150));
            }
            // If Blue is detected, set servo to 0
            if (colorSensor.red() < colorSensor.blue()) {
                DbgLog.msg("Blue");
                colorChooserServo.setPosition(getDecimalFromAngle(0));
            }
            // If Green is detected, reset servo to median
            if ((colorSensor.green() > colorSensor.blue()) && (colorSensor.green() > colorSensor.red())) {
                DbgLog.msg("Blue");
                colorChooserServo.setPosition(getDecimalFromAngle(60));
            }
        }

        // Code for blue alliance
        if (!redMode) {
            // If Red is detected, set servo to 0
            if (colorSensor.red() > colorSensor.blue()) {
                DbgLog.msg("Red");
                colorChooserServo.setPosition(getDecimalFromAngle(0));
            }
            // If Blue is detected, set servo to 150
            if (colorSensor.red() < colorSensor.blue()) {
                DbgLog.msg("Blue");
                colorChooserServo.setPosition(getDecimalFromAngle(150));
            }
            // If Green is detected, reset servo to median
            if ((colorSensor.green() > colorSensor.blue()) && (colorSensor.green() > colorSensor.red())) {
                DbgLog.msg("Green");
                colorChooserServo.setPosition(getDecimalFromAngle(60));
            }
        }

        // Code for driving to the bridge will be added here when the Gyro sensor arrives

        // Telemetry the detected color in string form
        telemetry.addData("detected color", colorSensorROB());

        // Give the color servo position back in angele form
        telemetry.addData("color servo pos", String.valueOf((colorChooserServo.getPosition()*180)/0.5));


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