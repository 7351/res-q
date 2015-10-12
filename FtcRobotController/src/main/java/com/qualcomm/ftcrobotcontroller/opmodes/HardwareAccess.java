package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

abstract public class HardwareAccess extends OpMode {

    /*
    This class lists the hardware that is currently on the robot with the configuration file
     */


    // Declare left motor
    DcMotor motorLeft;
    // Declare right motor
    DcMotor motorRight;

    public void init() {

        motorLeft = hardwareMap.dcMotor.get("motorleft");
        motorRight = hardwareMap.dcMotor.get("motorright");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);




    }

}