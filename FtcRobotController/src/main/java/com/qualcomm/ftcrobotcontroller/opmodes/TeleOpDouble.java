package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/*
 */

/**
 * TeleOpDouble Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
public class TeleOpDouble extends DriveTrainLayer {

    // Create objects for hardware

    DcMotor intakeMotor;

    DcMotor liftMotor;

    Servo LeftRightServo;

    Servo UpDownServo;

    DcMotor pistonMotor;

    Servo climbersServo;

    Servo leftAngelArm;

    Servo rightAngelArm;

    // Variables for controlling speed on the climbersServo
    private ElapsedTime servotime = new ElapsedTime();
    private double servoPosition;

    //tweak these values for desired speed
    private double servoDelta = 0.01;
    private double servoDelayTime2 = 0.0001;

    boolean YButton = false;
    boolean AButton = false;
    boolean XButton = false;
    boolean BButton = false;

    double scalePower = 1;

    ElapsedTime manipTime = new ElapsedTime();

    public boolean DPadUp = false;

    /*
     * Code to run when the op mode is initialized goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#init()
     */
    @Override
    public void init() {

        super.init();

        /*
        Declare hardware devices from configuration files
         */

        intakeMotor = hardwareMap.dcMotor.get("intakeMotor");

        LeftRightServo = hardwareMap.servo.get("LeftRightServo");

        UpDownServo = hardwareMap.servo.get("UpDownServo");

        pistonMotor = hardwareMap.dcMotor.get("pistonMotor");

        climbersServo = hardwareMap.servo.get("climbersServo");
        climbersServo.setDirection(Servo.Direction.REVERSE);

        leftAngelArm = hardwareMap.servo.get("leftAngelArm");
        rightAngelArm = hardwareMap.servo.get("rightAngelArm");

        liftMotor = hardwareMap.dcMotor.get("liftMotor");



    }

    @Override
    public void start() {

        servotime.reset();

        /*
         * Set the default servo positions
         */

        LeftRightServo.setPosition(0.45);
        UpDownServo.setPosition(0.7);

        climbersServo.setPosition(0.19);

        leftAngelArm.setPosition(0);
        rightAngelArm.setPosition(0.75);

    }

    /*
         * This method will be called repeatedly in a loop
         *
         * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
         */
    @Override
    public void loop() {

        /*
         * Gamepad 2
         *
         * Gamepad 2 Controls
         *
         */

        // Variables for shorter code on the buttons

        /*
         * Intake Sections | Left - In, Right - Out
         */
        double leftTrigger = gamepad2.left_trigger;
        boolean leftBumper = gamepad2.left_bumper;

        if (leftTrigger > 0.1) {
            intakeMotor.setPower(-1);
        }

        if (leftBumper) {
            intakeMotor.setPower(1);
        }
        if (leftTrigger == 0 && !leftBumper) {
            intakeMotor.setPower(0);
        }

        /*
         * Lift motor | Y axis on the right joystick makes lift go up and down
         */

        float leftYStick = -gamepad2.left_stick_y;

        // Power to decrease motor

        double ScalingPower = 1;

        if (leftYStick == 0) {
            liftMotor.setPower(0);
        } else {
            if ((leftYStick > 0) && (leftYStick <= 1)) {
                liftMotor.setPower(scaleInput(leftYStick));
            }
            if ((leftYStick < -0.01) && (leftYStick >= -1)) {
                liftMotor.setPower(scaleInput(leftYStick * ScalingPower));
            }
        }


        /*
         * Collector Box
         * Y - Tilt
         * A - Home
         * X - Left Score
         * B - Right Score
         * Bumper - Manual Mode
         * Trigger - Home Value
         * Down on right Joystick - Drop for ground
         * Up on right Joystick - Tilt
         */

        boolean YButtonPressed = gamepad2.y;
        boolean AButtonPressed = gamepad2.a;
        boolean XButtonPressed = gamepad2.x;
        boolean BButtonPressed = gamepad2.b;
        float RightTriggerPressed = gamepad2.right_trigger;
        boolean ManualMode = gamepad2.right_bumper;
        float RightJoystick2 = -gamepad2.right_stick_y;

        /*
         CHANGEABLE: Values for servo positions
         */

            final double centerLine = 0.45; // LR Value

            double[] homeValues = {
                    centerLine, 0.75
            }; //   LR    UD

            double[] flatValues = {
                    centerLine, 0.635
            }; //   LR    UD
            double[] tiltValues = {
                    centerLine, 0.89
            }; //   LR    UD
            double scorePositionRamp = 0.529; // UD Value
            double dropPositionFloor = 0.67; // UD Value
            double[] leftValues = {
                    0.67, scorePositionRamp
            }; //   LR    UD
            double[] rightValues = {
                    0.185, scorePositionRamp
            }; //   LR    UD
            double manualIncrement = 0.03; // How much the manual mode should increase or decrease the servo postion by
            double servoDelayTimeMultiplier = 0.7; // Delay between dumping the positions on the field



        if (RightTriggerPressed == 1) {
            LeftRightServo.setPosition(flatValues[0]);
            UpDownServo.setPosition(flatValues[1]);
        }

        if (RightJoystick2 == 1) {
            LeftRightServo.setPosition(tiltValues[0]);
            UpDownServo.setPosition(tiltValues[1]);
        }
        if (RightJoystick2 == -1) {
            UpDownServo.setPosition(dropPositionFloor);
        }

        /*
         * Manual mode for scoring box
         */

        if (ManualMode) {
            if (YButtonPressed) {
                if (!YButton) {
                    UpDownServo.setPosition(Range.clip(UpDownServo.getPosition() + manualIncrement, 0, 1));
                    YButton = true;
                }
            } if (!YButtonPressed) {
                YButton = false;
            }

            if (AButtonPressed) {
                if (!AButton) {
                    UpDownServo.setPosition(Range.clip(UpDownServo.getPosition() - manualIncrement, 0, 1));
                    AButton = true;
                }
            } if (!AButtonPressed) {
                AButton = false;
            }

            if (XButtonPressed) {
                if (!XButton) {
                    LeftRightServo.setPosition(Range.clip(LeftRightServo.getPosition() + manualIncrement, 0, 1));
                    XButton = true;
                }
            } if (!XButtonPressed) {
                XButton = false;
            }

            if (BButtonPressed) {
                if (!BButton) {
                    LeftRightServo.setPosition(Range.clip(LeftRightServo.getPosition() - manualIncrement, 0, 1));
                    BButton = true;
                }
            } if (!BButtonPressed) {
                BButton = false;
            }
        } else {

            // Tilt
            if (YButtonPressed) {
                if (!YButton) {
                    UpDownServo.setPosition(tiltValues[1]);
                    LeftRightServo.setPosition(tiltValues[0]);
                    YButton = true;
                }
            } if (!YButtonPressed) {
                YButton = false;
            }

            // Home
            if (AButtonPressed) {
                if (!AButton) {
                    LeftRightServo.setPosition(homeValues[0]);
                    UpDownServo.setPosition(homeValues[1]);
                    AButton = true;
                }
            } if (!AButtonPressed) {
                AButton = false;
            }

            /*
            manipTime.reset();
            if (manipTime.time() < servoDelayTime) {
                LeftRightServo.setPosition(leftValues[0]);
            } if (manipTime.time() >= servoDelayTime + .03) {
                UpDownServo.setPosition(leftValues[1]);
            }
            */

            // Left score
            if (XButtonPressed) {
                if (!XButton) {
                    manipTime.reset();
                    XButton = true;
                }
            } if (!XButtonPressed) {
                XButton = false;
            }
            if (XButtonPressed) {
                if (manipTime.time() <= servoDelayTimeMultiplier) {
                    UpDownServo.setPosition(flatValues[1]);
                    LeftRightServo.setPosition(flatValues[0]);
                } if (manipTime.time() > servoDelayTimeMultiplier + 0.01 && manipTime.time() <= (servoDelayTimeMultiplier*2) + 0.02) {
                    LeftRightServo.setPosition(leftValues[0]);
                } if (manipTime.time() > (servoDelayTimeMultiplier*2) + 0.03) {
                    UpDownServo.setPosition(leftValues[1]);
                }

            }

            // Right score
            if (BButtonPressed) {
                if (!BButton) {
                    manipTime.reset();
                    BButton = true;
                }
            } if (!BButtonPressed) {
                BButton = false;
            }
            if (BButtonPressed) {
                if (manipTime.time() <= servoDelayTimeMultiplier) {
                    UpDownServo.setPosition(flatValues[1]);
                    LeftRightServo.setPosition(flatValues[0]);
                } if (manipTime.time() > servoDelayTimeMultiplier + 0.01 && manipTime.time() <= (servoDelayTimeMultiplier*2) + 0.02) {
                    LeftRightServo.setPosition(rightValues[0]);
                } if (manipTime.time() > (servoDelayTimeMultiplier*2) + 0.03) {
                    UpDownServo.setPosition(rightValues[1]);
                }

            }
        }




        telemetry.addData("LRServo", String.valueOf(LeftRightServo.getPosition()));
        telemetry.addData("UDServo", String.valueOf(UpDownServo.getPosition()));




        /*
         * Accessories - Includes zipliners, and climbers servo
         * D-Pad Up - Throw climbers into bucket
         * D-Pad Down - Put climbers servo back down
         * D-Pad Left - Put left zip liners servo out
         * D-Pad Right - Put right zip liners servo out
         */

        /*
         * Left Servo Angel home servo position = 0
         * Left Servo Angel score servo positon = 0.75
         *
         * Right Servo Angel home servo position = 0.75
         * Right Servo Angel score servo position - 0
         */
        /*
         * CHANGEABLE: Angel arms scoring positions
         */

            double leftAngelHome = 0;
            double leftAngelScore = 0.75;
            double rightAngelHome = 0.75;
            double rightAngelScore = 0;
            double restingPosition = 0.19;

        boolean DPadLeftPressed = gamepad2.dpad_left;
        boolean DPadRightPressed = gamepad2.dpad_right;
        boolean DPadUpPressed = gamepad2.dpad_up;
        boolean DPadDownPressed = gamepad2.dpad_down;



        if (DPadLeftPressed) {
            leftAngelArm.setPosition(leftAngelScore);
        } else {
            leftAngelArm.setPosition(leftAngelHome);
        }

        if (DPadRightPressed) {
            rightAngelArm.setPosition(rightAngelScore);
        } else {
            rightAngelArm.setPosition(rightAngelHome);
        }


        if (DPadUp) {
            if (servotime.time() > servoDelayTime2) {
                climbersServo.setPosition(Range.clip(servoPosition += servoDelta, restingPosition, 1));
                servotime.reset();
            }
        }

        if (!DPadUpPressed) {
            servotime.reset();
            climbersServo.setPosition(restingPosition);
            servoPosition = restingPosition;
        }

        if (DPadUpPressed) {
            if (!DPadUp) {
                DPadUp = true;
            }

        }
        if (!DPadUpPressed) {
            if (DPadUp) {
                DPadUp = false;
            }
        }



//-------------------------------------------------------------------------------------------------//


		/*
		 * Gamepad 1
		 * 
		 * Gamepad 1 controls
		 *
		 */


        /*
         * Driving code extracted from First Tech Challenge example code
         * Right joystick - Drive
         * Left Trigger - Slow mode
         */

    float leftTrigger1 = gamepad1.left_trigger;

    if (leftTrigger1 == 1) {
        scalePower = 0.7;
    } else {
        scalePower = 1;
    }

    // throttle: right_stick_y ranges from -1 to 1, where -1 is full up, and
    // 1 is full down
    // direction: right_stick_x ranges from -1 to 1, where -1 is full left
    // and 1 is full right
    float throttle = -gamepad1.right_stick_y;
    float direction = -gamepad1.right_stick_x;
    float right = throttle - direction;
    float left = throttle + direction;

    // clip the right/left values so that the values never exceed +/- 1
    right = Range.clip(right, -1, 1);
    left = Range.clip(left, -1, 1);

    // scale the joystick value to make it easier to control
    // the robot more precisely at slower speeds.
    right = (float)scaleInput(right * scalePower);
    left =  (float)scaleInput(left * scalePower);

    // write the values to the motors
    driveLeft(left);
    driveRight(right);

    telemetry.addData("drive power", "L: "  + String.valueOf(left) + ", R: " + String.valueOf(right));



}

    /*
     * Code to run when the op mode is first disabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
     */
    @Override
    public void stop() {

    }
    /*
     * This method scales the joystick input so for low joystick values, the
	 * scaled value is less than linear.  This is to make it easier to drive
	 * the robot more precisely at slower speeds.
	 */
    double scaleInput(double dVal) {double[] scaleArray = {0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24, 0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00};
        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0); if (index < 0) {index = -index;} else if (index > 16) {index = 16;}
        double dScale = 0.0; if (dVal < 0) {dScale = -scaleArray[index];} else {dScale = scaleArray[index];}
        return dScale;
    }
}