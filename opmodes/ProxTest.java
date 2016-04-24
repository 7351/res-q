package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.ftcrobotcontroller.library.devices.VCNL4010;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.ftcrobotcontroller.opmodes.DriveTrainLayer;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Leo on 2/7/2016.
 */
public class ProxTest extends OpMode {
    //declaring hardware prox, motors and servo
    VCNL4010 prox;
    DcMotor motorRight1;
    DcMotor motorRight2;
    DcMotor motorLeft1;
    DcMotor motorLeft2;
    Servo climbersServo;
    GyroSensor gyro;

    public void init() {
        prox = new VCNL4010(hardwareMap, "prox");
        prox.setLEDSensitivity(20);
        prox.setProxRate(0x5);
        motorLeft1 = hardwareMap.dcMotor.get("motorleft1");
        motorLeft2 = hardwareMap.dcMotor.get("motorleft2");
        motorRight1 = hardwareMap.dcMotor.get("motorright1");
        motorRight2 = hardwareMap.dcMotor.get("motorright2");
        climbersServo = hardwareMap.servo.get("climbersServo");
        gyro = hardwareMap.gyroSensor.get("gyro");
        climbersServo.setDirection(Servo.Direction.REVERSE);
    }

    @Override
    public void start() {

    }//Declaring variables

   double power;
    double powerLeft;
    double powerRight;
    double servoDelta = 0.01;
    double restingPosition = 0;
    double servoPosition = restingPosition;
    int lastByte = -1;
    int flux = 10;
    int counter = 0;
    int stage=0;
    int offset=12;


    //This method will loop until driver station stops it.
    public void loop() {
        int highByte;
        int lowByte;
        int currentGyro;
        //Get Prox Data
        prox.refreshData();
        highByte = prox.getHb();
        lowByte = prox.getLb();
       //Store gyro
        currentGyro = gyro.getHeading();
//Stage Case/IF loops

        if (stage == 0){
            motorLeft1.setPower(-.25);
            motorLeft2.setPower(-.25);
            motorRight1.setPower(.25);
            motorRight2.setPower(.25);
            if (currentGyro >= (180-offset)) {
                motorLeft1.setPower(0);
                motorLeft2.setPower(0);
                motorRight1.setPower(0);
                motorRight2.setPower(0);
                telemetry.addData("Gyro" , currentGyro);
            }
            }
        }

        /*if (stage == 1) {
// Drive forward
            powerLeft = -.27;
            powerRight = -.2;
            motorLeft1.setPower(powerLeft);
            motorLeft2.setPower(powerLeft);
            motorRight1.setPower(powerRight);
            motorRight2.setPower(powerRight);
        }
        //Decides if its safe to throw climbers
        if (highByte >= 9) {
            //if the highByte is 9 you are close enough to the wall to throw
            telemetry.addData("Text", "Throw Climbers");
            stage = 2;
        } else {
            if (highByte == 8 && lowByte >= 150) {
                //if the high byte is 8 you may not be close enough but, only if the low is greater than 150 throw climbers
                stage = 1;
                if (lastByte >= (lowByte - flux)) {//Otter didnt move much sincs last loop
                    counter++;
                    lastByte = lowByte;
                    if (counter >= 25) {//checking how lomg Otters been stuck
                        stage = 3;
                    } else {
                        stage = 1;
                    }
                } else {//Otter is still moving
                    counter = 0;
                    stage = 1;
                    lastByte = lowByte;
                }


            } else {
                stage = 1;
            }
        }


        if (stage == 2) {
            //Sets motor power to zero and throws climbers
            powerLeft = 0.0;
            powerRight = 0.0;
            motorLeft1.setPower(powerLeft);
            motorLeft2.setPower(powerLeft);
            motorRight1.setPower(powerRight);
            motorRight2.setPower(powerRight);
            climbersServo.setPosition(Range.clip(servoPosition += servoDelta, restingPosition, 1));
        }
        if (stage == 3) {
            //otter is stuck short of the beacon ,cannot throw so stop motors
            powerLeft = 0.0;
            powerRight = 0.0;
            motorLeft1.setPower(powerLeft);
            motorLeft2.setPower(powerLeft);
            motorRight1.setPower(powerRight);
            motorRight2.setPower(powerRight);
            telemetry.addData("Text","Stopping");
        } */

    
    @Override
    public void stop() {
        prox.close();

    }

}

