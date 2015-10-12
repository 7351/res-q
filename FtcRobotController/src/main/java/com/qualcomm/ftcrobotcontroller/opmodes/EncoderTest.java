package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

public class EncoderTest extends BasicFunctions {
    DcMotor motorleft;
    DcMotor motorright;

    final static double WHEELPOWER = 0.20;

    final static int ENCODER_CPR = 1440;
    final static int GEAR_RATIO = 2;
    final static int WHEEL_DIAMETER = 4;
    final static int DISTANCE = 20;

    final static double CIRCUMFRERENCE = Math.PI * WHEEL_DIAMETER;
    final static double ROATATIONS = DISTANCE/CIRCUMFRERENCE;
    final static double COUNTS = ENCODER_CPR * ROATATIONS;
    final static double FINAL_ROTATION = COUNTS/GEAR_RATIO;

    @Override
    public void init() {
        resetEncoders(motorleft, motorright);
    }

    @Override
    public void start() {
        motorleft.setTargetPosition((int) FINAL_ROTATION);
        motorright.setTargetPosition((int) FINAL_ROTATION);

        motorleft.setChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorright.setChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);

        setDrivePower(WHEELPOWER, WHEELPOWER);
    }



    @Override
    public void loop() {
        telemetry.addData("Motor Target", FINAL_ROTATION);
        telemetry.addData("Left Position", motorleft.getCurrentPosition());
        telemetry.addData("Right Position", motorright.getCurrentPosition());
    }
}