package io.appium.uiautomator2.handler;

import org.json.JSONException;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.util.Logger;

public class PressBack extends SafeRequestHandler {

    public PressBack(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) throws JSONException {
        Logger.info("Go Back");
        try {
            AndroidElement.back();
        } catch (Exception e) {
            Logger.error("Unable to Press Back", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, e);
        }

        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, "Pressed Back");
    }
}
