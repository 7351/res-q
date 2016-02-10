package com.qualcomm.ftcrobotcontroller.library.devices;

/**
 * Created by Leo on 2/8/2016.
 */
public final class VCNL4010_Addresses {
    public final static int I2CADDR_DEFAULT = 0x13;
    public final static int COMMAND = 0x80;
    public final static int PRODUCTID = 0x81;
    public final static int PROXRATE = 0x82;
    public final static int IRLED = 0x83;
    public final static int AMBIENTPARAMETER = 0x84;
    public final static int AMBIENTDATA = 0x85;
    public final static int PROXIMITYDATA = 0x87;
    public final static int INTCONTROL = 0x89;
    public final static int PROXINITYADJUST = 0x8A;
    public final static int INTSTAT = 0x8E;
    public final static int MODTIMING = 0x8F;
    public final static int MEASUREAMBIENT = 0x10;
    public final static int MEASUREPROXIMITY = 0x08;
    public final static int AMBIENTREADY = 0x40;
    public final static int PROXIMITYREADY = 0x20;
}
