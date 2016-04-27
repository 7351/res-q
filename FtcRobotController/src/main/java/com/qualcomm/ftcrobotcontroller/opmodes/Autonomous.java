package com.qualcomm.ftcrobotcontroller.opmodes;

/**
 * DriveToBeacon
 * <p>
 * Drive to the beacon
 */
public class Autonomous extends DriveTrainLayer {

    BLCloseMountainOnly BLCloseMountainOnly = new BLCloseMountainOnly();
    BLFarMountainOnly BLFarMountainOnly = new BLFarMountainOnly();
    BLCloseClimberOnly BLCloseClimbersOnly = new BLCloseClimberOnly();
    BLFarClimberOnly BLFarClimberOnly = new BLFarClimberOnly();
    BLCloseClimberDef BLCloseClimberDef = new BLCloseClimberDef();
    BLFarClimberDef BLFarClimberDef = new BLFarClimberDef();
    BLCloseClimberMountain BLCloseClimberMountain = new BLCloseClimberMountain();
    BLFarClimberMountain BLFarClimberMountain = new BLFarClimberMountain();

    RDCloseMountainOnly RDCloseMountainOnly = new RDCloseMountainOnly();
    RDFarMountainOnly RDFarMountainOnly = new RDFarMountainOnly();
    RDCloseClimberOnly RDCloseClimbersOnly = new RDCloseClimberOnly();
    RDFarClimberOnly RDFarClimberOnly = new RDFarClimberOnly();
    RDCloseClimberDef RDCloseClimberDef = new RDCloseClimberDef();
    RDFarClimberDef RDFarClimberDef = new RDFarClimberDef();
    RDCloseClimberMountain RDCloseClimberMountain = new RDCloseClimberMountain();
    RDFarClimberMountain RDFarClimberMountain = new RDFarClimberMountain();

    DASConnection dasc = new DASConnection() {};
    final boolean redMode = dasc.getBoolean(dasc.KEY_LIST[0]);
    final int delay = dasc.getInt(dasc.KEY_LIST[1]);
    final String targetGoal = dasc.getString(dasc.KEY_LIST[2]);
    final int startingPos = dasc.getInt(dasc.KEY_LIST[3]);
    final String defenseTarget = dasc.getString(dasc.KEY_LIST[4]);
    int stage = -1;

    /*
     * Code to run when the op mode is initialized goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#init()
     */
    @Override
    public void init() {

        super.init();

        if (!redMode) {
            if (targetGoal.equals("mnt")) {
                if (startingPos == 0) {
                    BLCloseMountainOnly.init();
                } if (startingPos == 1) {
                    BLFarMountainOnly.init();
                }

            }
            if (targetGoal.equals("brz")) {
                if (defenseTarget.equals("no")) {
                    if (startingPos == 0) {
                        BLCloseClimbersOnly.init();
                    }
                    if (startingPos == 1) {
                        BLFarClimberOnly.init();
                    }
                }
                if (defenseTarget.equals("beacon")) {
                    if (startingPos == 0) {
                        BLCloseClimberDef.init();
                    }
                    if (startingPos == 1) {
                        BLFarClimberDef.init();
                    }
                }
                if (defenseTarget.equals("mnt")) {
                    if (startingPos == 0) {
                        BLCloseClimberMountain.init();
                    }
                    if (startingPos == 1) {
                        BLFarClimberMountain.init();
                    }
                }
            }
        }
        if (redMode) {
            if (targetGoal.equals("mnt")) {
                if (startingPos == 0) {
                    RDCloseMountainOnly.init();
                }
                if (startingPos == 1) {
                    RDFarMountainOnly.init();
                }

            }
            if (targetGoal.equals("brz")) {
                if (defenseTarget.equals("no")) {
                    if (startingPos == 0) {
                        RDCloseClimbersOnly.init();
                    }
                    if (startingPos == 1) {
                        RDFarClimberOnly.init();
                    }
                }
                if (defenseTarget.equals("beacon")) {
                    if (startingPos == 0) {
                        RDCloseClimberDef.init();
                    }
                    if (startingPos == 1) {
                        RDFarClimberDef.init();
                    }
                }
                if (defenseTarget.equals("mnt")) {
                    if (startingPos == 0) {
                        RDCloseClimberMountain.init();
                    }
                    if (startingPos == 1) {
                        RDFarClimberMountain.init();
                    }
                }

            }
        }
    }

    @Override
    public void start() {
        super.start();

        if (!redMode) {
            if (targetGoal.equals("mnt")) {
                if (startingPos == 0) {
                    BLCloseMountainOnly.start();
                } if (startingPos == 1) {
                    BLFarMountainOnly.start();
                }

            }
            if (targetGoal.equals("brz")) {
                if (defenseTarget.equals("no")) {
                    if (startingPos == 0) {
                        BLCloseClimbersOnly.start();
                    }
                    if (startingPos == 1) {
                        BLFarClimberOnly.start();
                    }
                }
                if (defenseTarget.equals("beacon")) {
                    if (startingPos == 0) {
                        BLCloseClimberDef.start();
                    }
                    if (startingPos == 1) {
                        BLFarClimberDef.start();
                    }
                }
                if (defenseTarget.equals("mnt")) {
                    if (startingPos == 0) {
                        BLCloseClimberMountain.start();
                    }
                    if (startingPos == 1) {
                        BLFarClimberMountain.start();
                    }
                }
            }
        }
        if (redMode) {
            if (targetGoal.equals("mnt")) {
                if (startingPos == 0) {
                    RDCloseMountainOnly.start();
                }
                if (startingPos == 1) {
                    RDFarMountainOnly.start();
                }

            }
            if (targetGoal.equals("brz")) {
                if (defenseTarget.equals("no")) {
                    if (startingPos == 0) {
                        RDCloseClimbersOnly.start();
                    }
                    if (startingPos == 1) {
                        RDFarClimberOnly.start();
                    }
                }
                if (defenseTarget.equals("beacon")) {
                    if (startingPos == 0) {
                        RDCloseClimberDef.start();
                    }
                    if (startingPos == 1) {
                        RDFarClimberDef.start();
                    }
                }
                if (defenseTarget.equals("mnt")) {
                    if (startingPos == 0) {
                        RDCloseClimberMountain.start();
                    }
                    if (startingPos == 1) {
                        RDFarClimberMountain.start();
                    }
                }
            }
        }
    }

    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
    @Override
    public void loop() {
        // Waiting stage
        if (stage == -1) {
            if (this.time >= delay) {
                stage++;
            }
        }

        // Commence the autonomous
        if (stage == 0) {
            if (!redMode) {
                if (targetGoal.equals("mnt")) {
                    if (startingPos == 0) {
                        BLCloseMountainOnly.loop();
                    } if (startingPos == 1) {
                        BLFarMountainOnly.loop();
                    }

                }
                if (targetGoal.equals("brz")) {
                    if (defenseTarget.equals("no")) {
                        if (startingPos == 0) {
                            BLCloseClimbersOnly.loop();
                        }
                        if (startingPos == 1) {
                            BLFarClimberOnly.loop();
                        }
                    }
                    if (defenseTarget.equals("beacon")) {
                        if (startingPos == 0) {
                            BLCloseClimberDef.loop();
                        }
                        if (startingPos == 1) {
                            BLFarClimberDef.loop();
                        }
                    }
                    if (defenseTarget.equals("mnt")) {
                        if (startingPos == 0) {
                            BLCloseClimberMountain.loop();
                        }
                        if (startingPos == 1) {
                            BLFarClimberMountain.loop();
                        }
                    }
                }
            }
            if (redMode) {
                if (targetGoal.equals("mnt")) {
                    if (startingPos == 0) {
                        RDCloseMountainOnly.loop();
                    } if (startingPos == 1) {
                        RDFarMountainOnly.loop();
                    }

                }
                if (targetGoal.equals("brz")) {
                    if (defenseTarget.equals("no")) {
                        if (startingPos == 0) {
                            RDCloseClimbersOnly.loop();
                        }
                        if (startingPos == 1) {
                            RDFarClimberOnly.loop();
                        }
                    }
                    if (defenseTarget.equals("beacon")) {
                        if (startingPos == 0) {
                            RDCloseClimberDef.loop();
                        }
                        if (startingPos == 1) {
                            RDFarClimberDef.loop();
                        }
                    }
                    if (defenseTarget.equals("mnt")) {
                        if (startingPos == 0) {
                            RDCloseClimberMountain.loop();
                        }
                        if (startingPos == 1) {
                            RDFarClimberMountain.loop();
                        }
                    }
                }
            }
        }

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
