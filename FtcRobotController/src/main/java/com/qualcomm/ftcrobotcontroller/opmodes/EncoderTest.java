package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

import java.lang.reflect.Array;

public class EncoderTest extends BasicFunctions {

    final static double WHEELPOWER = 0.20;
    
    final static int GEAR_RATIO = 2;
    final static int WHEEL_DIAMETER = 4;
    // Distance in inches array for multiple steps
    //              Amount of steps, starting from 0
    int[] distanceArray = new int[2];
    int state = 0;

    //                                   1440      x 12(inches)/Pi(3.14)*      4  All divided by 2
    // final static double ROTATION = (ENCODER_CPR * (DISTANCE/(Math.PI * WHEEL_DIAMETER)))/GEAR_RATIO;

    public double calculateEncoderRevolutions(int inch) {
        return (1440 * (inch/(Math.PI * WHEEL_DIAMETER))/GEAR_RATIO);
    }

    public boolean goalAchieved(int stateNum) {
        if ((motorLeft.getCurrentPosition() == distanceArray[stateNum]) && (motorRight.getCurrentPosition() == distanceArray[stateNum])) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void init() {
        distanceArray[0] = 20;
        distanceArray[1] = 5;
        distanceArray[2] = 10;
        resetEncoders(motorLeft, motorRight);
    }

    @Override
    public void start() {

    }



    @Override
    public void loop() {
        if (state == 0) {
            motorLeft.setTargetPosition(distanceArray[0]);
            motorRight.setTargetPosition(distanceArray[0]);

            runToPosition(motorLeft, motorRight);

            setDrivePower(WHEELPOWER, WHEELPOWER);

            if (goalAchieved(0)) {
                state++;
            }
        } if (state == 1) {
            resetEncoders(motorLeft, motorRight);

            motorLeft.setTargetPosition(distanceArray[1]);
            motorRight.setTargetPosition(distanceArray[1]);

            runToPosition(motorLeft, motorRight);

            setDrivePower(WHEELPOWER, WHEELPOWER);

            if (goalAchieved(1)) {
                state++;
            }
        } if (state == 2) {
            resetEncoders(motorLeft, motorRight);

            motorLeft.setTargetPosition(distanceArray[2]);
            motorRight.setTargetPosition(distanceArray[2]);

            runToPosition(motorLeft, motorRight);

            setDrivePower(WHEELPOWER, WHEELPOWER);

            if (goalAchieved(2)) {
                state++;
            }
        }
        telemetry.addData("Motor Target", calculateEncoderRevolutions(distanceArray[0]));
        telemetry.addData("Left Position", motorLeft.getCurrentPosition());
        telemetry.addData("Right Position", motorRight.getCurrentPosition());
    }
}