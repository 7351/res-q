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
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Beacon_Climbers_Bridge autonomous
 * <p>
 * Autonomous mode to dump climbers into bucket and hit the beacon with colorSensor
 */
public class Beacon_Climbers_Bridge extends OpMode {

    // Define objects

    ColorSensor colorSensor;

    Servo colorServo;

    Servo climbersServo;

    // Variables for controlling speed on the climbersServo

    private ElapsedTime servotime = new ElapsedTime();
    private double servoPosition;

    //tweak these values for desired speed
    private double servoDelta = 0.01;
    private double servoDelayTime = 0.0035;


    private final boolean redMode = true;

    // Function return a decimal from the inputed angle
    double getDecimalFromAngle (int angle) { return angle / 180; }


    // Determines what color the robot is seeing in string form
    String colorROB () {
        String returnValue;
        if (colorSensor.red() > colorSensor.blue()) {
            returnValue = "Red";
        } if (colorSensor.blue() > colorSensor.red()) {
            returnValue = "Blue";
        } else {
            returnValue = "Unknown";
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

        colorSensor = hardwareMap.colorSensor.get("colorSensor");

        colorServo = hardwareMap.servo.get("colorServo");
        climbersServo = hardwareMap.servo.get("climbersServo");
        colorSensor.enableLed(false);

        // Set servo positions
        climbersServo.setPosition(0);

        colorServo.setPosition(getDecimalFromAngle(45));

    }

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

        // Set climber servo with modified speed

        if( servotime.time() > servoDelayTime ) {
            climbersServo.setPosition(Range.clip(servoPosition += servoDelta, 0, getDecimalFromAngle(110)));
            servotime.reset();
        }

        // Angle statements
        if (redMode) {
            if (colorROB() == "Red") {
                DbgLog.msg(colorROB());
                colorServo.setPosition(getDecimalFromAngle(75));
            } if (colorROB() == "Blue") {
                DbgLog.msg(colorROB());
                colorServo.setPosition(getDecimalFromAngle(0));
            }
        } if (!redMode) {
            if (colorROB() == "Red") {
                DbgLog.msg(colorROB());
                colorServo.setPosition(getDecimalFromAngle(0));
            }
            if (colorROB() == "Blue") {
                DbgLog.msg(colorROB());
                colorServo.setPosition(getDecimalFromAngle(75));
            }
        }

        // Default position if no color is detected
        if (colorROB() == "Unknown") {
            colorServo.setPosition(getDecimalFromAngle(45));
        }



        // Telementry return data
        telemetry.addData("color", colorROB());

        telemetry.addData("servoPos", String.valueOf(colorServo.getPosition()*180));


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