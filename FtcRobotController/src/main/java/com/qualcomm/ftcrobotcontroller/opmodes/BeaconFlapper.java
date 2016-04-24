package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;

/**
 * Beacon_Climbers_Bridge autonomous
 * <p>
 * Autonomous mode to dump climbers into bucket and hit the beacon with colorSensor
 */
public class BeaconFlapper extends OpMode {

    // Define objects

    ColorSensor colorSensor;

    DeviceInterfaceModule cdim;
    // we assume that the LED pin of the RGB Sensor is connected to
    // digital port 5 (zero indexed).
    static final int LED_CHANNEL = 5;

    //Servo colorServo;

    private final boolean redMode = false;

    // Function return a decimal from the inputed angle
    double getDecimalFromAngle (double angle) { return angle / 180; }

    boolean isRed() {
        boolean returnValue = false;
        if (colorSensor.red() > colorSensor.blue() + 75) {
            returnValue = true;
        }
        return returnValue;
    }

    boolean isBlue() {
        boolean returnValue = false;
        if (colorSensor.blue() > colorSensor.red() + 100) {
            returnValue = true;
        }
        return returnValue;
    }

    // Determines what color the robot is seeing in string form
    String colorROB () {
        String returnValue;
        if (isRed()) {
            return "Red";
        } if (isBlue()) {
            return "Blue";
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

        cdim = hardwareMap.deviceInterfaceModule.get("Device Interface Module");

        //colorServo = hardwareMap.servo.get("colorServo");
        //colorSensor.setI2cAddress(0x4c);

        // Set servo positions
        //colorServo.setPosition(getDecimalFromAngle(45));

        cdim.setDigitalChannelState(LED_CHANNEL, false);

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
            if (colorROB().equals("Red")) {
                DbgLog.msg(colorROB());
                //colorServo.setPosition(getDecimalFromAngle(75));
            } if (colorROB().equals("Blue")) {
                DbgLog.msg(colorROB());
                //colorServo.setPosition(getDecimalFromAngle(0));
            }
        } if (!redMode) {
            if (colorROB().equals("Red")) {
                DbgLog.msg(colorROB());
                //.setPosition(getDecimalFromAngle(0));
            }
            if (colorROB().equals("Blue")) {
                DbgLog.msg(colorROB());
                //colorServo.setPosition(getDecimalFromAngle(75));
            }
        }

        // Default position if no color is detected
        if (colorROB().equals("Unknown")) {
            //colorServo.setPosition(getDecimalFromAngle(45));
        }



        // Telementry return data
        telemetry.addData("color", colorROB());
        //DbgLog.msg("R: " + colorSensor.red() + " , G: " + colorSensor.green() + " , B: " + colorSensor.blue());

        //telemetry.addData("servoPos", String.valueOf(colorServo.getPosition()*180));


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