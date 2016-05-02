package io.appium.uiautomator2.handler;

import org.json.JSONException;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.ServerInstrumentation;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.util.Logger;

public class DeleteSession extends SafeRequestHandler {

    public DeleteSession(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) throws JSONException {
        try {
            Logger.info("Delete session command");
            ServerInstrumentation.getInstance(null, 8080).stopServer();
        } catch (Exception e) {
            Logger.error("Unable to Delete Session ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_COMMAND, e);
        }
        return new AppiumResponse(getSessionId(request), "Session deleted");
    }
}
