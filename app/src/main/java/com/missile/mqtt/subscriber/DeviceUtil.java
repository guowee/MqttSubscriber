package com.missile.mqtt.subscriber;


import android.os.Build;

public final class DeviceUtil {

    private DeviceUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String getSerialNo(){
        return  Build.SERIAL;
    }
}
