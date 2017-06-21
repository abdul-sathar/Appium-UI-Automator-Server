package io.appium.uiautomator2.model;

import io.appium.uiautomator2.utils.Device;
import io.appium.uiautomator2.utils.Logger;

import static io.appium.uiautomator2.utils.API.API_18;

public class CompressedLayoutHierarchy {
    public static final String  SETTING_NAME = "ignoreUnimportantViews";

    public static void updateSetting(boolean compressLayout) {
        try {
            // setCompressedLayoutHeirarchy doesn't exist on API <= 17
            if (API_18) {
                Device.getUiDevice().setCompressedLayoutHeirarchy(compressLayout);
                Logger.info("Set the Compressed Layout Hierarchy");
            } else {
                Logger.info("SetCompressedLayoutHeirarchy doesn't exist on API <= 17");
            }

        } catch (Exception e) {
            Logger.error("error setting compressLayoutHierarchy " + e.getMessage());
        }
    }
}
