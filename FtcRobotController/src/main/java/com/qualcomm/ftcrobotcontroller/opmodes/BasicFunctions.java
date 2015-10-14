package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

public class BasicFunctions extends HardwareAccess {

    /*
    This file lists some basic functions on the robot that we can use without typing large amounts of code.
    To use this code in the opmode replace "extends OpMode" with "extends BasicFunctions" at the end of the class file
     */

    /*
    Setting drive power for both motors with one command. Run with setDrivePower() function
     */
    public void setDrivePower(Double power1, Double power2) {
        motorLeft.setPower(power1);
        motorRight.setPower(power2);

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

    public void resetEncoders(DcMotor motor1, DcMotor motor2) {
        motor1.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        motor2.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

    public void runToPosition(DcMotor motor1, DcMotor motor2){
        motor1.setChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motor2.setChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void loop() {

    }

}
