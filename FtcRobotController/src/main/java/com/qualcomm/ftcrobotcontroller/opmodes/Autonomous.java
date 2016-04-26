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
 * <p>
 * Drive to the beacon
 */
public class Autonomous extends DriveTrainLayer {

    final static int TOLERANCE = 1;
    DASConnection dasc = new DASConnection() {
    };
    boolean redMode = dasc.getBoolean(dasc.KEY_LIST[0]);
    int delay = dasc.getInt(dasc.KEY_LIST[1]);
    String targetGoal = dasc.getString(dasc.KEY_LIST[2]);
    int startingPos = dasc.getInt(dasc.KEY_LIST[3]);
    String defenseTarget = dasc.getString(dasc.KEY_LIST[4]);
    ColorSensor lineColorSensor;
    double intakePower = 0;
    GyroSensor gyro;
    int stage = -1;
    ElapsedTime manipTime = new ElapsedTime();
    Servo climbersServo;
    ElapsedTime waitTime = new ElapsedTime();
    ElapsedTime startTime = new ElapsedTime();
    DcMotor intakeMotor;
    Servo LBumper;
    Servo RBumper;
    Servo leftAngelArm;
    Servo rightAngelArm;
    double leftBumperRest = 0.7,
            leftBumperTilt = 0.35,
            rightBumperRest = 0.2,
            rightBumperTilt = 0.575;

    VCNL4010 prox;
    double restingPosition = 0;
    double servoPosition = restingPosition;
    //int out of the loop
    int lastByte = -1;
    int flux = 10;
    int counter = 0;
    private double servoDelta = 0.01;
    private ElapsedTime servotime = new ElapsedTime();
    private double servoDelayTime2 = 0.0001;

    // Smaller tolerance
    public boolean isGyroInTolerance(int degree) {
        boolean returnValue = false;
        if ((gyro.getHeading() <= degree + TOLERANCE) && (gyro.getHeading() >= degree - TOLERANCE)) {
            returnValue = true;
        }
        return returnValue;
    }

    // Bigger tolerance
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

    private void rotateUsingSpoofed(int ZeroDegree, int TargetDegree, double DivisionNumber, String RotationMode) {
        int CurrentSpoofedDegree = spoofedZero(ZeroDegree); //An expected 39 gyro value from fake zero
        if (!isGyroInTolerance(TargetDegree)) {
            double DegreesOff = Math.abs(TargetDegree - CurrentSpoofedDegree);
            double RawPower = Range.clip(DegreesOff / DivisionNumber, 0, 1);
            if (DegreesOff < 20) {
                RawPower += 0.2;
            }
            if (RotationMode.equals("clockwise")) {
                powerLeft(RawPower);
                powerRight(-RawPower);
            }
            if (RotationMode.equals("counterclockwise")) {
                powerLeft(-RawPower);
                powerRight(RawPower);
            } else {
                DbgLog.error("Program will not go on, rotation mode isn't specified");
            }

        }


    }

    private void driveOnHeading(int desiredDegree, double power) {
        int gyroDegree = spoofedZero(desiredDegree);
        int targetDegrees = 0;
        double leftStartPower = power;
        double rightStartPower = power;
        double dividerNumber = 13.0;

        if (gyroDegree > 0 && gyroDegree <= 170) {
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

        if (gyroDegree >= 190 && gyroDegree < 360) {
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

    public boolean aboveBlueLine() {
        boolean returnValue = false;
        if ((lineColorSensor.blue() > lineColorSensor.red()) && (lineColorSensor.blue() > lineColorSensor.green())) {
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

        lineColorSensor = hardwareMap.colorSensor.get("lineColorSensor");

        prox = new VCNL4010(hardwareMap, "prox");

        gyro = hardwareMap.gyroSensor.get("gyro");
        lineColorSensor.enableLed(false);
        gyro.calibrate();

        intakeMotor = hardwareMap.dcMotor.get("intakeMotor");

        RBumper = hardwareMap.servo.get("RBumper");
        LBumper = hardwareMap.servo.get("LBumper");
        leftAngelArm = hardwareMap.servo.get("leftAngelArm");
        rightAngelArm = hardwareMap.servo.get("rightAngelArm");

        prox.setProxRate(0x5);
        prox.setLEDSensitivity(20);

        //led = hardwareMap.dcMotor.get("led");

    }

    @Override
    public void start() {
        super.start();

        lineColorSensor.enableLed(true);
        gyro.calibrate();
        manipTime.reset();

        startTime.reset();

        RBumper.setPosition(rightBumperRest);
        LBumper.setPosition(leftBumperRest);

        leftAngelArm.setPosition(0);
        rightAngelArm.setPosition(0.8);
    }

    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
    @Override
    public void loop() {

        int highByte;
        int lowByte;

        //Get Prox Data
        prox.refreshData();
        highByte = prox.getHb();
        lowByte = prox.getLb();

        // Waiting stage
        if (stage == -1) {
            if (this.time >= delay) {
                stage++;

            }
        }

        // Calibration stage
        if (stage == 0) {
            if (!gyro.isCalibrating()) {
                manipTime.reset();
                stage++;
            }
        }

        if (targetGoal.equals("mnt")) {



            // Mountain defense waiting for gyro calibration
            if (stage == 1) {
                if (waitTime.time() > 3) {
                    manipTime.reset();
                    stage++;
                }
            }

            // Drive on heading 0 until we reach blue line
            if (stage == 2) {
                if (!aboveBlueLine()) {
                    driveOnHeading(0);
                }
                if (aboveBlueLine()) {
                    powerLeft(0);
                    powerRight(0);
                    manipTime.reset();
                    waitTime.reset();
                    stage++;
                }
            }

            // Follow heading until you are up the ramp
            if (stage == 3) {

                int targetAngle = 0;
                // This is a temporary number ^^

                int stage_61LongRed = 50;
                // TODO get long degree RED
                int stage_61ShortRed = 62;

                int stage_61LongBlue = 310;
                // TODO get long & short degree BLUE
                int stage_61ShortBlue = 308;

                if (redMode) {
                    if (startingPos == 1) {
                        targetAngle = stage_61LongRed;
                    } if (startingPos == 0) {
                        targetAngle = stage_61ShortRed;
                    }
                } if (!redMode) {
                    if (startingPos == 1) {
                        targetAngle = stage_61LongBlue;
                    } if (startingPos == 0) {
                        targetAngle = stage_61ShortBlue;
                    }
                }

                if (waitTime.time() < 4) {
                    driveOnHeading(targetAngle, .4);
                }
                if (waitTime.time() > 4) {
                    powerLeft(0);
                    powerRight(0);
                    stage = 999;
                }
            }

        }

        if (targetGoal.equals("brz")) {

            double travelDistanceTime = 0;
            int targetAngle = 0;

            // Red alliance
            if (redMode) {
                // If we are closer to the mountain
                if (startingPos == 0) {
                    travelDistanceTime = 0.7;
                    targetAngle = 325;
                }

                // If we are farther from the mountain
                if (startingPos == 1) {
                    travelDistanceTime = 0.4;
                    targetAngle = 307;
                }
            }

            // Blue alliance
            if (!redMode) {

                // If we are closer to the mountain
                if (startingPos == 0) {
                    travelDistanceTime = 0.7;
                    targetAngle = 36;
                }

                // If we are farther from the mountain
                if (startingPos == 1) {
                    travelDistanceTime = 0.45;
                    targetAngle = 51;
                }
            }

            // Drive out from wall
            if (stage == 1) {
                intakePower = 1;
                if (!gyro.isCalibrating()) {
                    powerLeft(0.6);
                    powerRight(0.6);
                    if (manipTime.time() >= travelDistanceTime) {
                        powerLeft(0);
                        powerRight(0);
                        stage++;
                        waitTime.reset();
                    }
                }
            }

            // Pause for .25 seconds
            if (stage == 2) {
                if (waitTime.time() >= 0.25) {
                    manipTime.reset();
                    waitTime.reset();
                    stage++;
                }
            }
            // Drive on heading until you find the white line with slower power
            if (stage == 3) {
                if (aboveWhiteLine()) {
                    powerLeft(0);
                    powerRight(0);
                    stage++;
                }
                if (!aboveWhiteLine()) {
                    if (!gyro.isCalibrating()) {
                        double RateOfDepression = -0.015;
                        double power = (RateOfDepression * manipTime.time()) + 1;
                        driveOnHeading(targetAngle, power);
                    }
                }
            }

            // Pause for .5 seconds to stop and stabilize
            if (stage == 4) {
                if (waitTime.time() >= 0.5) {
                    stage++;
                    manipTime.reset();
                }
            }

            // Backup to the white line if otter went past (Rarely used)
            if (stage == 5) {
                if (!aboveWhiteLine()) {
                    powerLeft(-0.4);
                    powerRight(-0.4);

                }
                if (aboveWhiteLine()) {
                    powerLeft(0);
                    powerRight(0);
                    stage++;
                    waitTime.reset();
                }

            }

            // Pause for .5 seconds to stop and stablize
            if (stage == 6) {
                if (waitTime.time() >= 0.5) {
                    stage++;
                    manipTime.reset();
                }
            }

            // Turning Otter around to 90 degrees to prep for climbers
            if (stage == 7) {
                intakePower = 0;
                if (!gyro.isCalibrating()) {
                    int targetDegree = 90;
                    String rotation = null;
                    if (redMode) {
                        targetDegree = 90;
                        rotation = "clockwise";
                    }
                    if (!redMode) {
                        targetDegree = 270;
                        rotation = "counterclockwise";
                    }
                    if (!isGyroInTolerance2(targetDegree)) {
                        rotateUsingSpoofed((360 - targetDegree), 180, 162, rotation);
                    }
                    if (isGyroInTolerance2(targetDegree)) {
                        powerLeft(0);
                        powerRight(0);
                        stage++;
                    }
                }
            }

            // Pause for 0.5 seconds to stabilize
            if (stage == 8) {
                if (waitTime.time() >= 0.5) {
                    stage++;
                    manipTime.reset();
                }
            }

            // Prox sensor stages
            if (stage == 9) {
                // Degree for comming out of box
                int targetDegree = 0;
                if (redMode) {
                    targetDegree = 270;
                } if (!redMode) {
                    targetDegree = 90;
                }
                // Drive forward
                driveOnHeading(targetDegree, -0.3);
                // Decides if its safe to throw climbers
                // if the high byte is 8 you may not be close enough but, only if the low is greater than 200 throw climbers
                if (highByte >= 9 || (highByte == 8 && lowByte >= 200)) {
                    // if the highByte is 9 you are close enough to the wall to throw
                    telemetry.addData("Text", "Throw Climbers");
                    stage = 15; // Goto to stage 15 to throw climbers
                } else {
                    // Loop to count how many time otter doesn't move
                    if (highByte <= 8) {

                        if (lastByte >= (lowByte - flux)) { // Otter didnt move much sincs last loop
                            counter++;
                            lastByte = lowByte;
                            if (counter >= 1000) { // checking how lomg Otters been stuck
                                stage = 17; // abort skip the stage to throw climbers
                            } else {
                                stage = 9;
                            }
                        } else { // Otter is still moving
                            counter = 0;
                            stage = 9; // re-check
                            lastByte = lowByte;
                        }
                    }
                }
            }

            // Sets motor power to zero and throws climbers
            if (stage == 15) {
                powerRight(0);
                manipTime.reset();
                waitTime.reset();
                stage++;
            }

            // Throws climbers and moves servo back
            if (stage == 16) {
                if (waitTime.time() > 0.3 && waitTime.time() < 4.5) {
                    if (servotime.time() > servoDelayTime2) {
                        climbersServo.setPosition(Range.clip(servoPosition += servoDelta, restingPosition, 1));
                        servotime.reset();
                    }
                }
                if (waitTime.time() > 1.5) {
                    climbersServo.setPosition(0);
                    manipTime.reset();
                    if (defenseTarget.equals("no")) {
                        stage = 999;
                    }
                    if (defenseTarget.equals("beacon")) {
                        stage = 51;
                    }
                    if (defenseTarget.equals("mnt")) {
                        stage = 41;
                    }
                }
            }

            // Drive out of box
            if (stage == 41) {
                int targetDegrees = 0;
                if (redMode) {
                    targetDegrees = 90;
                } if (!redMode) {
                    targetDegrees = 270;
                }
                if (manipTime.time() <= 1) {
                    driveOnHeading(targetDegrees, 1);
                }
                if (manipTime.time() >= 1) {
                    powerRight(0);
                    powerLeft(0);
                    stage++;
                }
            }
            // Find the blue line in the middle of the field
            if (stage == 42) {
                int targetDegrees = 0;
                if (redMode) {
                    targetDegrees = 90;
                } if (!redMode) {
                    targetDegrees = 270;
                }
                if (!aboveBlueLine()) {
                    driveOnHeading(targetDegrees);
                }
                if (aboveBlueLine()) {
                    powerLeft(0);
                    powerRight(0);
                    manipTime.reset();
                    waitTime.reset();
                    stage++;
                }
            }
            if (stage == 43) {
                // TODO find correct heading for mountain defense
                int targetDegree = 0;
                if (redMode) {
                    targetDegree = 70;
                } if (!redMode) {
                    targetDegree = 290;
                }
                if (waitTime.time() < 4.3) {
                    driveOnHeading(targetDegree, .4);
                }
                if (waitTime.time() > 4.4) {
                    powerLeft(0);
                    powerRight(0);
                    stage = 999;
                }
            }

            // Code for driving to blue beacon and blocking for defense
            // drive out
            if (stage == 51) {
                if (manipTime.time() <= 1) {
                    powerLeft(.7);
                    powerRight(.7);
                }
                if (manipTime.time() >= 1) {
                    powerRight(0);
                    powerLeft(0);
                    stage++;
                }
            }

            if (stage == 52) {

                // TODO find correct angle on first heading beacon defense
                int targetDegree = 0;
                if (redMode) {
                    targetDegree = 2;
                } if (!redMode) {
                    targetDegree = 358;
                }

                if (!aboveBlueLine()) {
                    driveOnHeading(targetDegree);
                }
                if (aboveBlueLine()) {
                    powerLeft(0);
                    powerRight(0);
                    manipTime.reset();
                    waitTime.reset();
                    stage++;
                }
            }

            // Stop and wait to stabilize
            if (stage == 53) {
                if (waitTime.time() > 0.25) {
                    manipTime.reset();
                    stage++;
                }
            }

            // Drive on heading 69 until we see white line
            if (stage == 54) {
                int targetDegree = 0;
                if (redMode) {
                    targetDegree = 69;
                } if (!redMode) {
                    targetDegree = 291;
                }
                // TODO find blue angle heading for part two of beacon defense
                if (!aboveWhiteLine()) {
                    driveOnHeading(targetDegree);
                }
                if (aboveWhiteLine()) {
                    powerLeft(0);
                    powerRight(0);
                    stage = 999;
                }
            }

            intakeMotor.setPower(intakePower);

            LBumper.setPosition(leftBumperRest);
            RBumper.setPosition(rightBumperRest);
        }


        // End of the program
        if (stage == 999) {
            powerLeft(0);
            powerRight(0);
            telemetry.addData("Status", "Robot is complete :)");
        }
        telemetry.addData("stage", String.valueOf(stage));
        telemetry.addData("time", String.valueOf(this.time));
        DbgLog.msg("L:" + String.valueOf(motorRight1.getPower()) + ", R: " + String.valueOf(motorLeft1.getPower()));
        telemetry.addData("gyro", String.valueOf(gyro.getHeading()));


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
