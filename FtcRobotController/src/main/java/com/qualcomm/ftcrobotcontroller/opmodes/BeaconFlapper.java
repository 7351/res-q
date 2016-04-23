package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Beacon_Climbers_Bridge autonomous
 * <p>
 * Autonomous mode to dump climbers into bucket and hit the beacon with colorSensor
 */
public class BeaconFlapper extends OpMode {

    // Define objects

    ColorSensor colorSensor;

    Servo colorServo;

    private final boolean redMode = true;

    // Function return a decimal from the inputed angle
    double getDecimalFromAngle (double angle) { return angle / 180; }


    // Determines what color the robot is seeing in string form
    String colorROB () {
        String returnValue;
        if (colorSensor.red() > colorSensor.blue()) {
            returnValue = "Red";
        } if (colorSensor.blue() > colorSensor.red()) {
            returnValue = "Blue";
        } else {
            returnValue = "Unknown";
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

        // Get variables from hardwaremap

        colorSensor = hardwareMap.colorSensor.get("colorSensor");

        colorServo = hardwareMap.servo.get("colorServo");
        colorSensor.enableLed(false);

        // Set servo positions
        colorServo.setPosition(getDecimalFromAngle(45));

    }

    @Override
    public void start() {

    }

    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
    @Override
    public void loop() {

        // Your stage statement can go here.

        // Angle statements
        if (redMode) {
            if (colorROB() == "Red") {
                DbgLog.msg(colorROB());
                colorServo.setPosition(getDecimalFromAngle(75));
            } if (colorROB() == "Blue") {
                DbgLog.msg(colorROB());
                colorServo.setPosition(getDecimalFromAngle(0));
            }
        } if (!redMode) {
            if (colorROB() == "Red") {
                DbgLog.msg(colorROB());
                colorServo.setPosition(getDecimalFromAngle(0));
            }
            if (colorROB() == "Blue") {
                DbgLog.msg(colorROB());
                colorServo.setPosition(getDecimalFromAngle(75));
            }
        }

        // Default position if no color is detected
        if (colorROB() == "Unknown") {
            colorServo.setPosition(getDecimalFromAngle(45));
        }



        // Telementry return data
        telemetry.addData("color", colorROB());

        telemetry.addData("servoPos", String.valueOf(colorServo.getPosition()*180));


    }

    /*
     * Code to run when the op mode is first disabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
     */
    @Override
    public void stop() {

    }



    // Scaling input has been moved to BasicFunctions
}