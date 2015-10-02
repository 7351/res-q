package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * An empty op mode serving as a template for custom OpModes
 */
abstract public class EncoderSpecial extends BasicFunctions {

    protected DcMotorController motorControllerDrive;

    private int loopVar = 0;

    DcMotorController.RunMode motorRightRunMode;
    DcMotorController.RunMode motorLeftRunMode;

    DcMotorController.DeviceMode devMode;


    // Specifying motors has been moved to HardwareAccess.class

    public void useEncoders () {

    // TODO Fix this function

        int loopVar = 0;

        while (true) {
            loopVar++;
            motorRightRunMode = DcMotorController.RunMode.RESET_ENCODERS;
            motorLeftRunMode = DcMotorController.RunMode.RESET_ENCODERS;
            if (loopVar == 6) {
                if (encoderResetStatus()) {
                    DbgLog.msg("Encoders have been reset");
                    loopVar = 0;
                    break;
                } else {
                    DbgLog.error("Encoders have failed to reset");
                    loopVar = 0;
                }
            }
        }

        while (true) {
            loopVar++;
            motorLeftRunMode = DcMotorController.RunMode.RUN_USING_ENCODERS;
            motorRightRunMode = DcMotorController.RunMode.RUN_USING_ENCODERS;
            if (loopVar == 6) {
                if (motorsInCorrectMode(DcMotorController.RunMode.RUN_USING_ENCODERS)) {
                    DbgLog.msg("Motors have been set to correct mode");
                    loopVar = 0;
                    break;
                } else {
                    loopVar = 0;
                }
            }
        }

        /*
        while (threadIsNotInterrupted()) {
            DbgLog.msg("Resetting Encoders now!");
            motorLeftRunMode = DcMotorController.RunMode.RESET_ENCODERS;
            motorRightRunMode = DcMotorController.RunMode.RESET_ENCODERS;
            if (motorsInCorrectMode(DcMotorController.RunMode.RESET_ENCODERS)) {
                DbgLog.msg("Encoders have been reset!");
                break;
            }
        }
        */


        while (threadIsNotInterrupted() && encoderResetStatus()) {
            DbgLog.msg("Running using encoders now!");
            motorLeftRunMode = DcMotorController.RunMode.RUN_USING_ENCODERS;
            motorRightRunMode = DcMotorController.RunMode.RUN_USING_ENCODERS;
            if (motorsInCorrectMode(DcMotorController.RunMode.RUN_USING_ENCODERS)) {
                DbgLog.msg("Encoders have been reset!");
                break;
            }
        }

    }

    boolean threadIsNotInterrupted() {
        boolean interrupted = !Thread.currentThread().isInterrupted();
        return interrupted;
    }

    public void resetDriveEncoders() {
        while (true) {
            loopVar++;
            motorRightRunMode = DcMotorController.RunMode.RESET_ENCODERS;
            motorLeftRunMode = DcMotorController.RunMode.RESET_ENCODERS;
            if (loopVar == 6) {
                if (encoderResetStatus()) {
                    DbgLog.msg("Encoders have been reset");
                    loopVar = 0;
                    break;
                } else {
                    DbgLog.error("Encoders have failed to reset");
                    loopVar = 0;
                }
            }
        }
    }

    boolean encoderResetStatus ()
    {
        //
        // Assume failure.
        //
        boolean l_status = false;

        //
        // Have the encoders reached zero?
        //
        if ((leftEncoderCount() == 0) && (rightEncoderCount() == 0))
        {
            //
            // Set the status to a positive indication.
            //
            l_status = true;
        }

        //
        // Return the status.
        //
        return l_status;

    }

    boolean motorsInCorrectMode(DcMotorController.RunMode mode) {
        if ((motorLeftRunMode == mode) && motorRightRunMode == mode) {
            return true;
        } else {
            return false;
        }
    }

    boolean driveEncodersReached
            ( double p_left_count
                    , double p_right_count
            )
    {
        //
        // Assume failure.
        //
        boolean l_status = false;

        //
        // Have the encoders reached the specified values?
        //
        // TODO Implement stall code using these variables.
        //
        if ((Math.abs(motorLeft.getCurrentPosition()) > p_left_count) &&
                (Math.abs(motorRight.getCurrentPosition()) > p_right_count))
        {
            //
            // Set the status to a positive indication.
            //
            l_status = true;
        }

        //
        // Return the status.
        //
        return l_status;

    }

    int leftEncoderCount ()
    {
        return motorLeft.getCurrentPosition ();

    }

    int rightEncoderCount ()
    {
        return motorRight.getCurrentPosition ();

    }


    /*
    * Constructor
    */
    public EncoderSpecial() {

    }

    /*
    * Code to run when the op mode is first enabled goes here
    * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
    */
    @Override
    public void init() {

        // Hardware assigns have been moved to the HardwareAccess.class file
        super.init();

    }

    /*
    * Code to run when the op mode is first disabled goes here
    * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
    */
    @Override
    public void stop() {

    }

    // Scaling input has been moved to BasicFunctions
}