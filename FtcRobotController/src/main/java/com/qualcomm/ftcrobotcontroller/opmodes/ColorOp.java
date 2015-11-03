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

/**
 * ColorOp mode
 * <p>
 * Testing op mode that relates to color
 */
public class ColorOp extends OpMode {

    ColorSensor color;

	Servo servo;

    private final boolean redMode = false;

	double getDecimalFromAngle (int angle) { return angle / 180; }



    /*
     * Code to run when the op mode is initialized goes here
     * 
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#init()
     */
	@Override
	public void init() {

		color = hardwareMap.colorSensor.get("color");

		servo = hardwareMap.servo.get("servo");
	    color.enableLed(false);

        servo.setPosition(getDecimalFromAngle(45));

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

		if (redMode) {
            if (color.red() > color.blue()) {
                DbgLog.msg("Red");
                servo.setPosition(getDecimalFromAngle(75));
            } if (color.red() < color.blue()) {
                DbgLog.msg("Blue");
                servo.setPosition(getDecimalFromAngle(0));
            }
        } if (!redMode) {
            if (color.red() > color.blue()) {
                DbgLog.msg("Red");
                servo.setPosition(getDecimalFromAngle(0));
            } if (color.red() < color.blue()) {
                DbgLog.msg("Blue");
                servo.setPosition(getDecimalFromAngle(75));
            }
        }


        telemetry.addData("color", String.valueOf(color.argb()));

        telemetry.addData("servoPos", String.valueOf(servo.getPosition()*180));


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
