package io.appium.uiautomator2.handler;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

import static io.appium.uiautomator2.utils.Device.back;

public class PressBack extends SafeRequestHandler {

    public PressBack(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        boolean status = back();
        if (status) {
            Logger.info("Pressed Back");
            return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, status);
        } else {
            Logger.info("Unable to Press Back");
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, status);
        }
    }
}
