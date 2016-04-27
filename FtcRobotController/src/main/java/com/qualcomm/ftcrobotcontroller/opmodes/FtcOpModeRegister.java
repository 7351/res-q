/* Copyright (c) 2014, 2015 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;

/**
 * Register Op Modes
 */
public class FtcOpModeRegister implements OpModeRegister {

    /**
     * The Op Mode Manager will call this method when it wants a list of all
     * available op modes. Add your op mode to the list to enable it.
     *
     * @param manager op mode manager
     */

    public void register(OpModeManager manager) {

    /*
     * register your op modes here.
     * The first parameter is the name of the op mode
     * The second parameter is the op mode class property
     *
     * If two or more op modes are registered with the same name, the app will display an error.
     */

        // Removed Qualcomm's code, view on the github provided by them.

    /*
     * The TeleOpDouble op mode
	 */
        manager.register("Empty", EmptyOp.class);

       manager.register("00. TeleOp", TeleOpDouble.class);
       manager.register("01. RD-Far-ClimberDef",RDFarClimberDef.class );
       manager.register("02. RD-Close-ClimberDef",RDCloseClimberDef.class );
       manager.register("03. BL-Far-ClimberDef",BLFarClimberDef.class );
       manager.register("04. BL-Close-ClimbersDef",BLCloseClimberDef.class);
       manager.register("05. RD-Far-MountainOnly " ,RDFarMountainOnly.class);
       manager.register("06. RD-Close-MountainOnly",RDCloseMountainOnly.class);
       manager.register("07. BL-Far-MountainOnly",BLFarMountainOnly.class);
       manager.register("08. BL-Close-MountainOnly",BLCloseMountainOnly.class);
       manager.register("09. RD-Far-Climber-Mountain",RDFarClimberMountain.class);
       manager.register("10. RD-Close-Climber-Mountain",RDCloseClimberMountain.class);
       manager.register("11. BL-Far-Climber-Mountain",BLFarClimberMountain.class);
       manager.register("12. BL-Close-Climber-Mountain",BLCloseClimberMountain.class);
       manager.register("13. RD-Far-ClimberOnly",RDFarClimberOnly.class);
      manager.register("14. RD-Close-ClimberOnly",RDCloseClimberOnly.class);
        manager.register("15. BL-Far-ClimberOnly",BLFarClimberOnly.class);
        manager.register("16. BL-Close-ClimberOnly",BLCloseClimberOnly.class);
        manager.register("17. BL-Close-ClimberOnly-Delay", BLCloseClimberOnlyDelay.class);
        manager.register("18. BL-Far-ClimberOnly-Delay", BLFarClimberOnlyDelay.class);
        manager.register("19. RD-Close-ClimberOnly-Delay", RDCloseClimberOnlyDelay.class);
        manager.register("20. RD-Far-ClimberOnly-Delay", RDFarClimberOnlyDelay.class);

        manager.register("ClimbersRed", DriveToBeaconRed.class);
        manager.register("ClimbersBlue", DriveToBeaconBlue.class);
        manager.register("Autonomous", Autonomous.class);

    /*
	 * Some testing op modes
	 */

        //manager.register("ProxTest", ProxTest.class);
        //manager.register("GyroDriveTest", GyroDriveTest.class);
        //manager.register("GyroTurnTest", GyroTurnTest.class);

    }
}
