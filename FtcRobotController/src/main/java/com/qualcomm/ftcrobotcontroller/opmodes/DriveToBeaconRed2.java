/*    if (stage == 0) {
            if (!gyro.isCalibrating()) {
                double target_angle_degrees = 135;
                double error_degrees = target_angle_degrees - gyro.getHeading();
                if ( error_degrees > 25) {
                    driveLeft(0.75);
                    driveRight(-0.75);
                } else {
                    driveLeft(0.57);
                    driveRight(-0.57);
                } if (isGyroInTolerance((int) target_angle_degrees)) {
                    driveLeft(0);
                    driveRight(0);
                    stage++;
                }
            } if (gyro.isCalibrating()) {
                driveLeft(0);
                driveRight(0);
            }
        }
         */
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
public class DriveToBeaconRed2 extends DriveTrainLayer {

    final static int TOLERANCE = 1;
    ColorSensor lineColorSensor;
    GyroSensor gyro;
    int stage = 0;
    ElapsedTime manipTime = new ElapsedTime();
    ElapsedTime waitTime = new ElapsedTime();
    ElapsedTime startTime = new ElapsedTime();
    DcMotor intakeMotor;
    Servo climbersServo;
    double restingPosition = 0;
    Servo leftAngelArm;
    Servo rightAngelArm;
    VCNL4010 prox;
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
            powerLeft(RawPower);
            powerRight(-RawPower);
        }
    }


    private void driveOnHeading(int desiredDegree, double power) {
        int gyroDegree = spoofedZero(desiredDegree);
        int targetDegrees = 0;
        double leftStartPower = power;
        double rightStartPower = power;
        double dividerNumber = 15;

        if (gyroDegree > 0 && gyroDegree <= 90) {
            int error_degrees = Math.abs(targetDegrees - gyroDegree);
            double subtractivePower = error_degrees / dividerNumber;
            DbgLog.msg(String.valueOf(subtractivePower + ", " + error_degrees));
            if (power > 0) {
                leftStartPower = Range.clip(1 - subtractivePower, -1, 1);
            }
            if (power < 0) {
                leftStartPower = Range.clip(1 + subtractivePower, -1, 1);
            }

        }

        if (gyroDegree >= 270 && gyroDegree < 360) {
            int error_degrees = Math.abs(90 - (gyroDegree - 270));
            double subtractivePower = error_degrees / dividerNumber;
            DbgLog.msg(String.valueOf(subtractivePower + ", " + error_degrees));
            if (power > 0) {
                rightStartPower = Range.clip(1 - subtractivePower, -1, 1);
            }
            if (power < 0) {
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

    public boolean aboveRedLine() {
        boolean returnValue = false;
        if ((lineColorSensor.red() > lineColorSensor.green() + 2) && (lineColorSensor.red() > lineColorSensor.blue() + 2)) {
            returnValue = true;
        }
        return returnValue;
    }

    double servoPosition = restingPosition;


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
        gyro.calibrate();

        intakeMotor = hardwareMap.dcMotor.get("intakeMotor");

        prox.setProxRate(0x5);
        prox.setLEDSensitivity(20);

        climbersServo = hardwareMap.servo.get("climbersServo");
        climbersServo.setDirection(Servo.Direction.REVERSE);

        leftAngelArm = hardwareMap.servo.get("leftAngelArm");
        rightAngelArm = hardwareMap.servo.get("rightAngelArm");

    }

    @Override
    public void start() {
        super.start();

        climbersServo.setPosition(restingPosition);

        leftAngelArm.setPosition(0);
        rightAngelArm.setPosition(0.8);

        lineColorSensor.enableLed(true);
        gyro.calibrate();
        manipTime.reset();

        startTime.reset();
    }

    int currentGyro;
    int lastByte = -1;
    int flux = 10;
    int counter = 0;
    int offset = 15;

    /*d
         * This method will be called repeatedly in a loop
         *
         * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
         */
    @Override
    public void loop() {

        //calibrate
        if (stage == 0) {
            if (!gyro.isCalibrating()) {
                manipTime.reset();
                stage = 1;          }
        }
// drive out straight from wall
        if (stage == 101) {
            motorRight1.setPower(.6);
            motorRight2.setPower(.6);
            motorLeft1.setPower(.6);
            motorLeft2.setPower(.6);
            if (manipTime.time() >= 0.3) {
                motorRight1.setPower(0);
                motorRight2.setPower(0);
                motorLeft1.setPower(0);
                motorLeft2.setPower(0);
                stage = 102;
                waitTime.reset();
            }
        }
        // Turn to heading
        if (stage == 102) {
            //Start turning clockwise
            motorLeft1.setPower(1);
            motorLeft2.setPower(1);
            motorRight1.setPower(-.5);
            motorRight2.setPower(-.5);
            currentGyro = gyro.getHeading();
            //Until 311 degrees
            if (currentGyro <= (311 - offset)) {
                motorLeft1.setPower(0);
                motorLeft2.setPower(0);
                motorRight1.setPower(0);
                motorRight2.setPower(0);
                telemetry.addData("Gyro", currentGyro);
                telemetry.addData("Stage", stage);
                waitTime.reset();
                stage = 999;
            }
        }

        //drive forward
        if (stage == 103) {
            motorLeft1.setPower(-.4);
            motorLeft2.setPower(-.4);
            motorRight1.setPower(-.4);
            motorRight2.setPower(-.4);
            if (manipTime.time() >= 0.3) {
                motorRight1.setPower(0);
                motorRight2.setPower(0);
                motorLeft1.setPower(0);
                motorLeft2.setPower(0);
                stage = 104;
                waitTime.reset();
            }
        }
        // Turn to heading
        if (stage == 104) {
            motorLeft1.setPower(.5);
            motorLeft2.setPower(.5);
            motorRight1.setPower(-.25);
            motorRight2.setPower(-.25);
            currentGyro = gyro.getHeading();
            if (currentGyro <= (311 - offset)) {
                motorLeft1.setPower(0);
                motorLeft2.setPower(0);
                motorRight1.setPower(0);
                motorRight2.setPower(0);
                telemetry.addData("Gyro", currentGyro);
                telemetry.addData("Stage", stage);
                waitTime.reset();
                stage = 105;
            }
        }
        // drive forward
        if (stage == 105) {
            motorLeft1.setPower(-.2);
            motorLeft2.setPower(-.2);
            motorRight1.setPower(-0.2);
            motorRight2.setPower(-0.2);
            if (aboveRedLine()) {
                motorLeft1.setPower(0);
                motorLeft2.setPower(0);
                motorRight1.setPower(0);
                motorRight2.setPower(0);
                stage = 999;

            }
        }

        if (stage == 106) {
            motorLeft1.setPower(.4);
            motorLeft2.setPower(.4);
            motorRight1.setPower(.4);
            motorRight2.setPower(.4);
            if (manipTime.time() >= 0.3) {
                motorRight1.setPower(0);
                motorRight2.setPower(0);
                motorLeft1.setPower(0);
                motorLeft2.setPower(0);
                stage = 999;
                waitTime.reset();
            }
        }

        if (stage == 107) {
            if (aboveWhiteLine()) {
                motorLeft1.setPower(0);
                motorLeft2.setPower(0);
                motorRight1.setPower(0);
                motorRight2.setPower(0);
                stage = 108;
            }
            if (!aboveWhiteLine()) {
                if (!gyro.isCalibrating()) {
                    double RateOfDepression = -0.015;
                    double power = (RateOfDepression * manipTime.time()) + 1;
                    driveOnHeading(307, power);
                }
            }
        }

        // drives backwards to red line
        if (stage == 108) {
            motorLeft1.setPower(.2);
            motorLeft2.setPower(.2);
            motorRight1.setPower(0.2);
            motorRight2.setPower(0.2);
            if (aboveRedLine()) {
                motorLeft1.setPower(0);
                motorLeft2.setPower(0);
                motorRight1.setPower(0);
                motorRight2.setPower(0);
                stage = 999;

            }
        }

        if (stage == 5) {
            motorLeft1.setPower(-1);
            motorLeft2.setPower(-1);
            motorRight1.setPower(.5);
            motorRight2.setPower(.5);
            //storing current gyro reading
            currentGyro = gyro.getHeading();
            if (currentGyro <= (90 - offset)) {
                motorLeft1.setPower(0);
                motorLeft2.setPower(0);
                motorRight1.setPower(0);
                motorRight2.setPower(0);
                telemetry.addData("Gyro", currentGyro);
                telemetry.addData("Stage", stage);
                stage++;
            }
        }

        int highByte;
        int lowByte;

        //Get Prox Data
        prox.refreshData();
        highByte = prox.getHb();
        lowByte = prox.getLb();

        //Stage Case/IF loops
        if (stage == 6) {
            telemetry.addData("Prox", highByte);
// Drive forward
            powerRight(-0.27);
            powerLeft(-0.2);
            //Decides if its safe to throw climbers
            if (highByte >= 9) {
                //if the highByte is 9 you are close enough to the wall to throw
                telemetry.addData("Text", "Throw Climbers");
                stage = 7;
            } else {
                if (highByte <= 8) {
                    //if the high byte is 8 you may not be close enough but, only if the low is greater than 150 throw climbers
                    if (lastByte >= (lowByte - flux)) {//Otter didnt move much sincs last loop
                        counter++;
                        lastByte = lowByte;
                        if (counter >= 100) {//checking how lomg Otters been stuck
                            stage = 6;
                        } else {
                            stage = 6;
                        }
                    } else {//Otter is still moving
                        counter = 0;
                        stage = 6;
                        lastByte = lowByte;
                    }


                }


            }
        }

        if (stage == 7) {
            //Sets motor power to zero and throws climbers
            telemetry.addData("stage", stage);
            powerLeft(0);
            powerRight(0);
            climbersServo.setPosition(Range.clip(servoPosition += servoDelta, restingPosition, 1));
        }
        if (stage == 8) {
            //otter is stuck short of the beacon ,cannot throw so stop motors
            powerLeft(0);
            powerRight(0);
            telemetry.addData("Text", "Stopping");
        }


        telemetry.addData("stage", String.valueOf(stage));
        telemetry.addData("motor", String.valueOf(motorRight1.getPower()));
        telemetry.addData("gyro", String.valueOf(gyro.getHeading()));
        telemetry.addData("white", String.valueOf(aboveWhiteLine()));


    /*
     * Code to run when the op mode is first disabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
     */

    }

    @Override
    public void stop() {

    }


}