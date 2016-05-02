package io.appium.uiautomator2.util;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;

public abstract class Device {
    private static UiDevice uiDevice;

    public static final UiDevice getUiDevice() {
        // if (uiDevice == null) {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        //  }
        return uiDevice;
    }
}
