package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.ftcrobotcontroller.library.devices.VCNL4010;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * DriveToBeacon
 * <p/>
 * Drive to the beacon red side
 */
public class RDFarClimberOnlyDelay extends DriveTrainLayer {

    final static int TOLERANCE = 1;
    ColorSensor lineColorSensor;
    GyroSensor gyro;
    int stage = -1;
    ElapsedTime manipTime = new ElapsedTime();
    ElapsedTime waitTime = new ElapsedTime();
    ElapsedTime startTime = new ElapsedTime();
    DcMotor intakeMotor;
    Servo climbersServo;
    double restingPosition = 0;
    Servo leftAngelArm;
    Servo rightAngelArm;
    Servo LBumper;
    Servo RBumper;
    VCNL4010 prox;
    double servoPosition = restingPosition;
    //int out of the loop
    int lastByte = -1;
    int flux = 10;
    int counter = 0;
    int whiteCounter=1;
    private double servoDelta = 0.01;
    private ElapsedTime servotime = new ElapsedTime();
    private double servoDelayTime2 = 0.0001;

    public boolean isGyroInTolerance(int degree) {
        boolean returnValue = false;
        if ((gyro.getHeading() <= degree + TOLERANCE) && (gyro.getHeading() >= degree - TOLERANCE)) {
            returnValue = true;
        }
        return returnValue;
    }

    public boolean isGyroInTolerance2(int degree) {
        boolean returnValue = false;
        if ((gyro.getHeading() <= degree + 3) && (gyro.getHeading() >= degree - 3)) {
            returnValue = true;
        }
        return returnValue;
    }

    private int spoofedZero(int zeroDegree) {
        int ActualDegree = gyro.getHeading();
        int degree = ActualDegree - zeroDegree;
        if (degree > 360) {
            degree = degree - 360;
        }
        if (degree < 0) {
            degree = degree + 360;
        }
        return degree;
    }
    private void rotateUsingSpoofed(int ZeroDegree, int TargetDegree, double DivisionNumber) {
        int CurrentSpoofedDegree = spoofedZero(ZeroDegree); //An expected 39 gyro value from fake zero
        if (!isGyroInTolerance(TargetDegree)) {
            double DegreesOff = Math.abs(TargetDegree - CurrentSpoofedDegree);
            double RawPower = Range.clip(DegreesOff / DivisionNumber, 0, 1);
            if (DegreesOff < 20) {
                RawPower += 0.2;
            }
            powerLeft(-RawPower);
            powerRight(RawPower);
        }


    }

    private double getDivideNumber(double CurrentDegreesOff) {
        double divideNumber = 15;
        if (divideNumber < 0) {
            divideNumber = 1;
        }
        return divideNumber;
    }

    private void driveOnHeading(int desiredDegree, double power) {
        int gyroDegree = spoofedZero(desiredDegree);
        int targetDegrees = 0;
        double leftStartPower = power;
        double rightStartPower = power;
        double dividerNumber = 13.0;

        if (gyroDegree > 0 && gyroDegree <= 90) {
            int error_degrees = Math.abs(targetDegrees - gyroDegree);
            double subtractivePower = error_degrees / dividerNumber;
            DbgLog.msg(String.valueOf(subtractivePower + ", " + error_degrees));
            if (power > 0) {
                leftStartPower = Range.clip(1 - subtractivePower, -1, 1);
            } if (power < 0) {
                leftStartPower = Range.clip(1 + subtractivePower, -1, 1);
            }

        }

        if (gyroDegree >= 270 && gyroDegree < 360) {
            int error_degrees = Math.abs(90 - (gyroDegree - 270));
            double subtractivePower = error_degrees / dividerNumber;
            DbgLog.msg(String.valueOf(subtractivePower + ", " + error_degrees));
            if (power > 0) {
                rightStartPower = Range.clip(1 - subtractivePower, -1, 1);
            } if (power < 0) {
                rightStartPower = Range.clip(1 + subtractivePower, -1, 1);
            }

        }

        powerRight(rightStartPower);
        powerLeft(leftStartPower);
    }

    private void driveOnHeading(int desiredDegree) {
        driveOnHeading(desiredDegree, 1);
    }

    public boolean aboveWhiteLine() {
        boolean returnValue = false;
        if ((lineColorSensor.red() >= 2) && (lineColorSensor.green() >= 2) && (lineColorSensor.blue() >= 2)) {
            returnValue = true;
        }
        return returnValue;
    }

    public boolean aboveBlueLine() {
        boolean returnValue = false;
        if ((lineColorSensor.blue() > lineColorSensor.red()) && (lineColorSensor.blue() > lineColorSensor.green())) {
            returnValue = true;
        }
        return returnValue;
    }

    public boolean aboveRedLine() {
        boolean returnValue = false;
        if ((lineColorSensor.red() > lineColorSensor.green() + 2) && (lineColorSensor.red() > lineColorSensor.blue() + 2)) {
            returnValue = true;
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
        super.init();

        prox = new VCNL4010(hardwareMap, "prox");

        lineColorSensor = hardwareMap.colorSensor.get("lineColorSensor");

        gyro = hardwareMap.gyroSensor.get("gyro");
        lineColorSensor.enableLed(false);

        intakeMotor = hardwareMap.dcMotor.get("intakeMotor");

        prox.setProxRate(0x5);
        prox.setLEDSensitivity(20);

        climbersServo = hardwareMap.servo.get("climbersServo");
        climbersServo.setDirection(Servo.Direction.REVERSE);

        leftAngelArm = hardwareMap.servo.get("leftAngelArm");
        rightAngelArm = hardwareMap.servo.get("rightAngelArm");

        LBumper = hardwareMap.servo.get("LBumper");
        RBumper = hardwareMap.servo.get("RBumper");
        gyro.calibrate();
    }

    @Override
    public void start() {
        super.start();

        climbersServo.setPosition(restingPosition);

        leftAngelArm.setPosition(0);
        rightAngelArm.setPosition(0.8);

        lineColorSensor.enableLed(true);
        manipTime.reset();

        startTime.reset();
    }

    final static int DELAY = 4;

    /*
         * This method will be called repeatedly in a loop
         *
         * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
         */
    @Override
    //LoopStart Phase1
    public void loop() {
        int highByte;
        int lowByte;
        int currentGyro;
        currentGyro = gyro.getHeading();
        //Get Prox Data
        prox.refreshData();
        highByte = prox.getHb();
        lowByte = prox.getLb();

        if (stage == -1) {
            if (this.time > DELAY) {
                stage++;
            }
        }

        //Calabrtation stage
        if (stage == 0) {
            if (!gyro.isCalibrating()) {
                manipTime.reset();
                stage++;
            }
        }
        //Drive out from wall
        if (stage == 1) {
            intakeMotor.setPower(1);
            if (!gyro.isCalibrating()) {
                driveLeft(0.6);
                driveRight(0.6);
                if (manipTime.time() >= 0.4) {
                    driveLeft(0);
                    driveRight(0);
                    stage++;
                    waitTime.reset();
                }
            }
        }
        //Pause for .5 seconds
        if (stage == 2) {
            if (waitTime.time() >= 0.5) {
                manipTime.reset();
                waitTime.reset();
                stage++;
            }
        }
        //Drive on heading until you find the white line
        if (stage == 3) {
            if (aboveWhiteLine()) {
                driveLeft(0);
                driveRight(0);
                stage++;
            }
            if (!aboveWhiteLine()) {
                if (!gyro.isCalibrating()) {
                    double RateOfDepression = -0.015;
                    double power = (RateOfDepression * manipTime.time()) + 1;
                    driveOnHeading(307, power);
                    intakeMotor.setPower(1);
                }
            }
        }

        //Pause for .5 seconds to stop and stabilize
        if (stage == 4) {
            if (waitTime.time() >= 0.5) {
                stage++;
                manipTime.reset();
            }
        }
        //Backup to the white line if otter went past (Fail safe Rarely used)
        if (stage == 5) {
            if (!aboveWhiteLine()) {
                driveLeft(-0.4);
                driveRight(-0.4);
                }
            if (aboveWhiteLine()) {
                driveLeft(0);
                driveRight(0);
                stage++;
                waitTime.reset();
            }
        }
        //Pause for .5 seconds to stop and stablize
        if (stage == 6) {
            if (waitTime.time() >= 0.5) {
                stage++;
                manipTime.reset();
            }
        }
        //Phase2
        //Turning Otter around to 90 degrees to prep for climbers
        if (stage == 7) {
            if (!gyro.isCalibrating()) {
                if (!isGyroInTolerance2(90)) {
                    rotateUsingSpoofed(270, 180, 162);
                } if (isGyroInTolerance2(90)) {
                    powerLeft(0);
                    powerRight(0);
                    stage++;
                }

            }
        }
        if (stage == 8) {
            if (waitTime.time() >= 0.5) {
                stage++;
                manipTime.reset();
            }
        }
        //Prox sensor Phase
        //Drive forward
        if (stage == 9) {
            driveOnHeading(270, -0.3);
            //Decides if its safe to throw climbers
            //if the high byte is 8 you may not be close enough but, only if the low is greater than 200 throw climbers
            if (highByte >= 10 || (highByte == 9 && lowByte >= 120)) {
                //if the highByte is 9 you are close enough to the wall to throw
                telemetry.addData("Text", "Throw Climbers");
                stage++; //Goto to stage 15 to throw climbers
            } else {
                //Loop to count how many time otter doesn't move
                if (highByte <= 9) {

                    if (lastByte >= (lowByte - flux)) {//Otter didnt move much sincs last loop
                        counter++;
                        lastByte = lowByte;
                        if (counter >= 400) {//checking how long Otters been stuck
                            stage =999;//abort skip the stages to throw climbers
                        } else {
                            stage = 9;
                        }
                    } else {//Otter is still moving
                        counter = 0;
                        stage = 9;//recheck
                        lastByte = lowByte;
                    }
                }
            }
        }
        //Sets motor power to zero and throws climbers
        if (stage == 10) {
            powerRight(0);
            powerLeft(0);
            manipTime.reset();
            waitTime.reset();
            servotime.reset();
            intakeMotor.setPower(0);
            stage++;
        }
        //Throws climbers and moves servo back
        if (stage == 11) {
            if (waitTime.time() < 1.4) {
                if (servotime.time() > servoDelayTime2) {
                    climbersServo.setPosition(Range.clip(servoPosition += servoDelta, restingPosition, 1));
                }
            } if (waitTime.time() > 1.5) {
                climbersServo.setPosition(0);
                manipTime.reset();
                stage=999;
            }
        }


         ///End stage or Debug stage to stop
        if (stage == 999) {
            intakeMotor.setPower(0);
            powerLeft(0);
            powerRight(0);
        }

        //Lower bumpers
        double leftBumperRest = 0.7,
                leftBumperTilt = 0.35,
                rightBumperRest = 0.2,
                rightBumperTilt = 0.575;

            LBumper.setPosition(leftBumperRest);
            RBumper.setPosition(rightBumperRest);
    }

    /*
     * Code to run when the op mode is first disabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
     */
    @Override
    public void stop() {

    }

}