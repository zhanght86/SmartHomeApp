// IDeviceControlApi.aidl
package com.gatz.smarthomeapp;

// Declare any non-default types here with import statements

interface IDeviceControlApi {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void sendCommand(String type, String deviceId, String value);
    void sendAirCommand(String address, int value);
    void sendDeviceStatusRequset(String type);
    void sendAirTimeCommand(String oh, String om, String ch, String cm, String days);
    void sendAirTimeOffCmd();
}
