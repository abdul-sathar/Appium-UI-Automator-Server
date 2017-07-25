package io.appium.uiautomator2.model;

import io.appium.uiautomator2.utils.Logger;

public class AllowInvisibleElements {
    public static final String  SETTING_NAME = "allowInvisibleElements";

    public static void updateSetting(boolean allowInvisibleElements) {
        try {
            Logger.debug("Set the allowInvisibleElements to: " + String.valueOf(allowInvisibleElements));
        } catch (Exception e) {
            Logger.error("error setting allowInvisibleElements " + e.getMessage());
        }
    }
}
