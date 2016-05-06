package io.appium.uiautomator2.handler;


import org.json.JSONException;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AppiumUiAutomatorDriver;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

public class NewSession extends SafeRequestHandler {

    public NewSession(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) throws JSONException {

        String sessionID;
        try {
            sessionID = new AppiumUiAutomatorDriver().initializeSession();
        } catch (Exception e) {
            Logger.error("Error creating session ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.SESSION_NOT_CREATED_EXCEPTION, e);
        }
        return new AppiumResponse(sessionID, WDStatus.SUCCESS, "Created Session");
    }

}
