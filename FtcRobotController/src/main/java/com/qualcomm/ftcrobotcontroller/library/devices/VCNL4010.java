package com.qualcomm.ftcrobotcontroller.library.devices;

import com.qualcomm.ftcrobotcontroller.library.Wire;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by Leo on 2/15/2016.
 */
public class VCNL4010 {
    Wire pr;
    int prox;

    public VCNL4010(HardwareMap hardwareMap, String devName) {
        pr = new Wire(hardwareMap, devName, VCNL4010_Addresses.I2CADDR_DEFAULT);
    }

    public VCNL4010(HardwareMap hardwareMap, String devName, int address) {
        pr = new Wire(hardwareMap, devName, address);
    }

    public int getProximity() {
        writeVCNL(VCNL4010_Addresses.COMMAND, VCNL4010_Addresses.MEASUREPROXIMITY);

        pr.requestFrom(VCNL4010_Addresses.PROXIMITYDATAHIGH, 2);
        if (pr.responseCount() > 0) {
            pr.getResponse();
            if (pr.isRead()) {
                prox = pr.readHL();
            }
        }
        return prox;
    }

    public void setProxRate(int proxRate) {
        writeVCNL(VCNL4010_Addresses.PROXRATE, proxRate);
    }

    public void setLEDSensitivity(int ledSensitivity) {
        writeVCNL(VCNL4010_Addresses.IRLED, ledSensitivity);
    }

    private void writeVCNL(int register, int value) {
        pr.write(register, value);
    }

    public void close() {
        pr.close();
    }
}
