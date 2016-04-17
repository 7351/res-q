package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.ftcrobotcontroller.library.devices.VCNL4010;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.ftcrobotcontroller.opmodes.DriveTrainLayer;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Leo on 2/7/2016.
 */
public class ProxTest extends OpMode {
//declaring hardware
    VCNL4010 prox;
    DcMotor motorRight1;
    DcMotor motorRight2;
    DcMotor motorLeft1;
    DcMotor motorLeft2;
    Servo climbersServo;

//Intializing devices from the hardwareMap
    @Override
    public void init() {
        prox = new VCNL4010(hardwareMap, "prox");
        prox.setLEDSensitivity(20);
        prox.setProxRate(0x5);
        motorLeft1 = hardwareMap.dcMotor.get("motorleft1");
        motorLeft2 = hardwareMap.dcMotor.get("motorleft2");
        motorRight1 = hardwareMap.dcMotor.get("motorright1");
        motorRight2 = hardwareMap.dcMotor.get("motorright2");
        climbersServo = hardwareMap.servo.get("climbersServo");
        climbersServo.setDirection(Servo.Direction.REVERSE);
    }

    @Override
    public void start() {

    }//Declaring variables
    int stage=1;
    double powerLeft;
    double powerRight;
    double servoDelta = 0.01;
    double restingPosition = 0;
    double servoPosition = restingPosition;

    //This method will loop until driver station stops it.
    public void loop() {
        int highByte;
        int lowByte;

        //Get Prox Data
        prox.refreshData();
        highByte=prox.getHb();
        lowByte=prox.getLb();

//Stage Case/IF loops
if (stage==1) {
// Drive forward
    powerLeft=-.27;
    powerRight=-.2;
    motorLeft1.setPower(powerLeft);
    motorLeft2.setPower(powerLeft);
    motorRight1.setPower(powerRight);
    motorRight2.setPower(powerRight);
    //Decides if its safe to throw climbers
    if (highByte >= 9) {//if the highByte is 9 you are close enough to the wall to throw
        telemetry.addData("Text", "Throw Climbers");
        stage=2;
    } else {
        if (highByte == 8 && lowByte >= 150) {
        //if the high byte is 8 you may not be close enough but, only if the low is greater than 150 throw climbers
        stage=1;
            telemetry.addData("Text", "Throw Climbers >150");
        } else {
            telemetry.addData("Text", "DONT DO IT");
            stage=1;
        }
    }
}

        if (stage==2){
            //Sets motor power to zero and throws climbers
            powerLeft=0.0;
            powerRight=0.0;
            motorLeft1.setPower(powerLeft);
            motorLeft2.setPower(powerLeft);
            motorRight1.setPower(powerRight);
            motorRight2.setPower(powerRight);
            climbersServo.setPosition(Range.clip(servoPosition += servoDelta, restingPosition, 1));
        }




    }

    @Override
    public void stop() {
        prox.close();

    }
    
}
