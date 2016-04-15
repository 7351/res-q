package com.qualcomm.ftcrobotcontroller.library.devices;

import com.qualcomm.ftcrobotcontroller.library.Wire;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by Leo on 2/15/2016.
 */
public class VCNL4010 {
    Wire pr;
    int data;
    int hb;
    int lb;
    int distance;
    int prox;
    
    public VCNL4010(HardwareMap hardwareMap, String devName) {
        pr = new Wire(hardwareMap, devName, 2 * VCNL4010_Addresses.I2CADDR_DEFAULT);
    }

    public VCNL4010(HardwareMap hardwareMap, String devName, int address) {
        pr = new Wire(hardwareMap, devName, address);
    }

    public int getHb() {
        return hb;
    }

    public int getLb() {
        return lb;
    }

    public void refreshData() {
            pr.beginWrite(VCNL4010_Addresses.COMMAND);
            pr.write(VCNL4010_Addresses.MEASUREPROXIMITY);
            pr.endWrite();
            pr.requestFrom(VCNL4010_Addresses.PROXIMITYDATA, 2);
            if (pr.responseCount() > 0) {
                pr.getResponse();
                if (pr.isRead()) {
                    hb = pr.read();
                    lb = pr.read();
                }
            }
        }


    public void setProxRate(int proxRate) {
        pr.beginWrite(VCNL4010_Addresses.PROXRATE);
        pr.write(proxRate);
        pr.endWrite();
    }

    public void setLEDSensitivity(int ledSensitivity) {
        pr.beginWrite(VCNL4010_Addresses.IRLED);
        pr.write(ledSensitivity);
        pr.endWrite();
    }

    public void close() {
        pr.close();
    }

    public void beginWrite(int command) {
    }
}
