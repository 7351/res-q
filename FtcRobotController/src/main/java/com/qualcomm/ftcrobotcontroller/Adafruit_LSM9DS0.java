package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.ftcrobotcontroller.library.Wire;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Adafruit_LSM9DS0 {
    static final String logId   = "LSM9DS0:";       // Tag identifier in FtcRobotController.LogCat

    /*
    Manual at http://www.adafruit.com/datasheets/LSM9DS0.pdf
     */

    static final int
            GYRO_ADDRESS = 0xA80034,
            ACCELMAG_ADDRESS = 0x49,
            ACCEL_X_REG = 0x29,
            ACCEL_Y_REG = 0x2B,
            ACCEL_Z_REG = 0x2D;

    boolean isInitialized;
    private Wire accelMagSensor;
    public Adafruit_LSM9DS0(HardwareMap hardwareMap, String deviceName) {
        Wire gyroSensor = new Wire(hardwareMap, deviceName, GYRO_ADDRESS);
        accelMagSensor = new Wire(hardwareMap, deviceName, ACCELMAG_ADDRESS);

        accelMagSensor.beginWrite(ACCEL_X_REG);
        accelMagSensor.write(ACCEL_X_REG, 0);
        accelMagSensor.endWrite();

        accelMagSensor.beginWrite(ACCEL_Y_REG);
        accelMagSensor.write(ACCEL_Y_REG, 0);
        accelMagSensor.endWrite();

        accelMagSensor.beginWrite(ACCEL_Z_REG);
        accelMagSensor.write(ACCEL_Z_REG, 0);
        accelMagSensor.endWrite();
    }

    public int getXAccel() {
        accelMagSensor.requestFrom(ACCEL_X_REG, 1);
        while (accelMagSensor.available() < 1);
        int XAccel = accelMagSensor.read();
        return XAccel;
    }

    public int getYAccel() {
        accelMagSensor.requestFrom(ACCEL_Y_REG, 1);
        while (accelMagSensor.available() < 1);
        int XAccel = accelMagSensor.read();
        return XAccel;
    }

    public int getZAccel() {
        accelMagSensor.requestFrom(ACCEL_Z_REG, 1);
        while (accelMagSensor.available() < 1);
        int XAccel = accelMagSensor.read();
        return XAccel;
    }

}