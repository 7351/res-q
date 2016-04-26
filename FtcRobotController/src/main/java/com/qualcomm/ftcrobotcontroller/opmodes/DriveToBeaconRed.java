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
public class DriveToBeaconRed extends DriveTrainLayer {

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
            } if (RotationMode.equals("counterclockwise")) {
                powerLeft(-RawPower);
                powerRight(RawPower);
            } else {
                DbgLog.error("Program will not go on, rotation mode isn't specified");
            }

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

    public boolean aboveRedLine() {
        boolean returnValue = false;
        if ((lineColorSensor.red() > lineColorSensor.green() + 2) && (lineColorSensor.red() > lineColorSensor.blue() + 2)) {
            returnValue = true;
        }
        return returnValue;
    }

    public boolean aboveBlueLine(){
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
        gyro.calibrate();
        manipTime.reset();

        startTime.reset();
    }

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
        //Calabrtation stage
        if (stage == 0) {
            if (!gyro.isCalibrating()) {
                manipTime.reset();
                stage=61;
            }
        }
        //Drive out from wall
        if (stage == 1) {
            if (!gyro.isCalibrating()) {
                driveLeft(0.6);
                driveRight(0.6);
                if (manipTime.time() >= 0.7) {
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
                    driveOnHeading(324, power);
                    telemetry.addData("Hit the wall?", waitTime.time());
                    DbgLog.msg("Hit the wall?", waitTime.time());
                    //failsafe for missing white
                    DbgLog.msg("Hit wall check.  Wait time is:", waitTime.time());
                    //Check to set if Otter has been searching for the white line too long
                }
            }
        }

        if (stage == 103) {
            if (aboveWhiteLine()) {
                driveLeft(0);
                driveRight(0);
                stage=6;
            }
            if (!aboveWhiteLine()) {
                if (!gyro.isCalibrating()) {
                    driveOnHeading(320, -0.5);
                    //failsafe for missing white
                    DbgLog.msg("Hit wall check.  Wait time is:", waitTime.time());
                    //Check to set if Otter has been searching for the white line too long

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
        //Backup to the white line if otter went past (Rarely used)
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
                    rotateUsingSpoofed(270, 180, 162, "clockwise");
                }
                if (isGyroInTolerance2(90)) {
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
        //Prox sensor stages
        if (stage == 9) {
           // Drive forward
           driveOnHeading(270, -0.3);
           // motorLeft1.setPower(-.3);
          //  motorLeft2.setPower(-.3);
          //  motorRight1.setPower(-.3);
          //  motorRight2.setPower(-.3);
            //Decides if its safe to throw climbers
            //if the high byte is 8 you may not be close enough but, only if the low is greater than 200 throw climbers
            if (highByte >= 9 || (highByte == 8 && lowByte >= 200)) {
                //if the highByte is 9 you are close enough to the wall to throw
                telemetry.addData("Text", "Throw Climbers");
                stage = 15; //Goto to stage 15 to throw climbers
            } else {
                //Loop to count how many time otter doesn't move
                if (highByte <= 8) {

                    if (lastByte >= (lowByte - flux)) {//Otter didnt move much sincs last loop
                        counter++;
                        lastByte = lowByte;
                        if (counter >= 1000) {//checking how lomg Otters been stuck
                            stage = 17;//abort skip the stage to throw climbers
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
        if (stage == 15) {
            powerRight(0);
            manipTime.reset();
            waitTime.reset();
            stage++;
        }

        //Throws climbers and moves servo back
        if (stage == 16) {
            if (waitTime.time() > 0.3 && waitTime.time() < 4.5) {
                if (servotime.time() > servoDelayTime2) {
                    climbersServo.setPosition(Range.clip(servoPosition += servoDelta, restingPosition, 1));
                    servotime.reset();
                }
            } if (waitTime.time() > 1.5) {
                climbersServo.setPosition(0);
                manipTime.reset();
                stage=41;
            }
        }
        //Phase3
        //Backup out of the beacon repair zone to prepare to got mountain
        if (stage == 17) {
            if (waitTime.time() < 3.0) {
                powerLeft(0.7);
                powerRight(0.7);
            }
            if (waitTime.time() >= 3.0) {
                powerLeft(0);
                powerRight(0);
                waitTime.reset();
                manipTime.reset();
                stage++;
            }
        }
        //Turn #1 towards the red ramp on the blue side (Defense and points
        if (stage == 18) {
            if (!gyro.isCalibrating()) {
                double RateOfDepression = -0.015;
                double power = (RateOfDepression * manipTime.time()) + 1;
                driveOnHeading(72, power);
            }
            if (waitTime.time() >= 2) {
                motorLeft1.setPower(0);
                motorLeft2.setPower(0);
                motorRight1.setPower(0);
                motorRight2.setPower(0);
                telemetry.addData("Gyro", gyro.rawZ());
                DbgLog.msg("Gyro", gyro.rawZ());
                stage++;
            }
        }
        //Final heading and drive up ramp
        if (stage == 19) {
            if (!gyro.isCalibrating()) {
                double RateOfDepression = -0.015;
                double power = (RateOfDepression * manipTime.time()) + 1;
                driveOnHeading(78, power);
            }
            if (waitTime.time() >= 5.5) {
                motorLeft1.setPower(0);
                motorLeft2.setPower(0);
                motorRight1.setPower(0);
                motorRight2.setPower(0);
                telemetry.addData("Gyro", gyro.rawZ());
                DbgLog.msg("Gyro", gyro.rawZ());
                telemetry.addData("Gyro", gyro.rawZ());
                stage++;
            }
        }
        //drive out of the box
        if (stage == 41) {
            if (manipTime.time() <= 1){
                driveOnHeading(90,1);
            }
            if (manipTime.time() >= 1){
                powerRight(0);
                powerLeft(0);
                stage++;
            }
        }
        //Find the blue line in the middle of the field
        if (stage==42){
            if (lineColorSensor.blue() <= 0){
                driveOnHeading(90,1);
            }
            if(lineColorSensor.blue() >= 1 ){
                powerLeft(0);
                powerRight(0);
                manipTime.reset();
                waitTime.reset();
                stage++;
            }
        }
        if (stage == 43) {
            if (waitTime.time() <4.3) {
                driveOnHeading(70,.4);
            }
            if (waitTime.time() > 4.4) {
                powerLeft(0);
                powerRight(0);
                stage=943;
            }
        }




        //Code for driving to blue beacon and blocking for defense
        //drive out
        if (stage == 51) {
            if (manipTime.time() <= 1){
                powerLeft(.7);
                powerRight(.7);
            }
            if (manipTime.time() >= 1){
                powerRight(0);
                powerLeft(0);
                stage++;
            }
        }

        if (stage==52){
            DbgLog.msg("blue", lineColorSensor.blue());
            DbgLog.msg("red", lineColorSensor.red());
            telemetry.addData("red", lineColorSensor.red());
            telemetry.addData("blue", lineColorSensor.blue());
            telemetry.addData("white", lineColorSensor.red());
            telemetry.addData("stage", stage);

            if (lineColorSensor.blue() <= 0){
                driveOnHeading(2,1);
                stage=52;
            }
            if(lineColorSensor.blue() >= 1 ){
                powerLeft(0);
                powerRight(0);
                manipTime.reset();
                waitTime.reset();
                stage++;
            }
}
        if (stage == 53) {
            if (waitTime.time() > 0.3) {
                manipTime.reset();
                stage++;
            }
        }

        if (stage==54){
            //powerLeft(-1);
            //powerRight(1);
            //driveOnHeading(65,.7);
            //if(currentGyro >= 65){
               // powerRight(0);
                //powerLeft(0);
                stage++;
           // }
        }
        if (stage == 55) {
            if(!aboveWhiteLine()){
                driveOnHeading(69,1);
            }
            if (aboveWhiteLine()){
                powerLeft(0);
                powerRight(0);
                stage=955;
            }
        }
        //Drive to floor goal
        if (stage == 61) {
            if (waitTime.time() > 3) {
                manipTime.reset();
                stage++;
            }
        }
        //Drive on heading 180 to blue line
        if (stage==62){
            if (!aboveBlueLine()){
                driveOnHeading(0);
            }
            if(aboveBlueLine()){
                powerLeft(0);
                powerRight(0);
                manipTime.reset();
                waitTime.reset();
                stage++;
            }
        }

        if (stage==63){
            if (waitTime.time() < 4) {
                driveOnHeading(62, .4);
            }
            if (waitTime.time() > 4) {
                powerLeft(0);
                powerRight(0);
                stage=943;
            }
        }


        if (stage == 64) {
            if(!aboveRedLine()){
                driveOnHeading(245,1);
            }
            if (aboveRedLine()){
                powerLeft(0);
                powerRight(0);
                stage=963;
            }

        }

        //Drive to floor goal
        if (stage == 71) {
            if (waitTime.time() > 3) {
                manipTime.reset();
                stage++;
            }
        }
        //Drive on heading 180 to blue line
        if (stage==72){
            if (!aboveBlueLine()){
                driveOnHeading(0);
            }
            if(aboveBlueLine()){
                powerLeft(0);
                powerRight(0);
                manipTime.reset();
                waitTime.reset();
                stage++;
            }
        }

        if (stage==73){
            if (waitTime.time() < 4) {
                driveOnHeading(0, .4);
            }
            if (waitTime.time() > 4) {
                powerLeft(0);
                powerRight(0);
                stage=943;
            }
        }


        if (stage == 74) {
            if(!aboveRedLine()){
                driveOnHeading(245,1);
            }
            if (aboveRedLine()){
                powerLeft(0);
                powerRight(0);
                stage=963;
            }

        }



         ///Debug stage to stop
        if (stage == 999) {
            //otter is stuck short of the beacon ,cannot throw so stop motors
            powerLeft(0);
            powerRight(0);
            telemetry.addData("Status", "Stopping");
        }

        //Intake motor on and off
        if ( stage >= 1 && stage <= 5 || stage>=69 && stage>=  73 ) {
            intakeMotor.setPower(1);
        }
        if (stage > 5 && stage< 50 && stage <40) {
            intakeMotor.setPower(0);
        }
        //Lower bumpers
        double leftBumperRest = 0.7,
                leftBumperTilt = 0.35,
                rightBumperRest = 0.2,
                rightBumperTilt = 0.575;

        LBumper.setPosition(leftBumperRest);
        RBumper.setPosition(rightBumperRest);

        telemetry.addData("Gyro", gyro.rawZ());
        telemetry.addData("stage", String.valueOf(stage));
        telemetry.addData("motor", String.valueOf(motorRight1.getPower()));
        telemetry.addData("gyro", String.valueOf(gyro.getHeading()));
        //telemetry.addData("white", String.valueOf(aboveWhiteLine()));
        telemetry.addData("Prox", String.valueOf("hb: " + prox.getHb() + ", lb: " + prox.getLb()));

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