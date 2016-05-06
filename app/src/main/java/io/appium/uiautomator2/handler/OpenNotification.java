package io.appium.uiautomator2.handler;


import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Device;
import io.appium.uiautomator2.utils.Logger;

import static io.appium.uiautomator2.utils.API.API_18;

public class OpenNotification extends SafeRequestHandler {

    public OpenNotification(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        Logger.info("Open Notification");
        // method was only introduced in API Level 18
        if (!API_18) {
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, "Unable to open notifications on device below API level 18");
        }

        if (Device.getUiDevice().openNotification()) {
            return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, "Open notification");
        } else {
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, "Device failed to open notifications.");
        }
    }

}
