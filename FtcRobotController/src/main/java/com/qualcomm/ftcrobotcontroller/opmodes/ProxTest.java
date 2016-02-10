package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.library.devices.VCNL4010;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

/**
 * Created by Leo on 2/7/2016.
 */
public class ProxTest extends OpMode {

    VCNL4010 prox;

    @Override
    public void init() {
        prox = new VCNL4010(hardwareMap, "prox");
        prox.setLEDSensitivity(10);
        prox.setProxRate(0x5);

    }

    @Override
    public void start() {

    }


    @Override
    public void loop() {

        telemetry.addData("prox", String.valueOf(prox.getProximity()));

    }

    @Override
    public void stop() {
        prox.close();

    }
}
