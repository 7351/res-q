package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

public class EncoderTest extends OpMode {
    DcMotor motorleft;
    DcMotor motorright;

    final static double WHEELPOWER = 0.1;

    final static int ENCODER_CPR = 1440;
    final static double GEAR_RATIO = 2;
    final static int WHEEL_DIAMETER = 4;
    final static int DISTANCE = 6;

    final static  double CIRCUMFRERENCE = Math.PI * WHEEL_DIAMETER;
    final static double ROATATIONS = DISTANCE/CIRCUMFRERENCE;
    final static double COUNTS = ENCODER_CPR * ROATATIONS * GEAR_RATIO;

    double calcEncoderValue(double inches) {
        return (1440 * (inches/(Math.PI * WHEEL_DIAMETER)))/GEAR_RATIO;
    }

    @Override
    public void init() {
        motorleft = hardwareMap.dcMotor.get("motorleft");
        motorright = hardwareMap.dcMotor.get("motorright");

        motorleft.setDirection(DcMotor.Direction.REVERSE);

        motorleft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorright.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

    @Override
    public void start() {
        motorleft.setTargetPosition((int) calcEncoderValue(-3));
        motorright.setTargetPosition((int) calcEncoderValue(3));

        motorleft.setChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorright.setChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);

        motorleft.setPower(-WHEELPOWER);
        motorright.setPower(WHEELPOWER);
    }



    @Override
    public void loop() {
        telemetry.addData("Left Position", motorleft.getCurrentPosition());
        telemetry.addData("Right Position", motorright.getCurrentPosition());
    }
}