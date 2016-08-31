package io.appium.uiautomator2.handler;

import android.support.test.InstrumentationRegistry;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.ServerInstrumentation;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

public class DeleteSession extends SafeRequestHandler {
    public static final int PORT = 8080;

    public DeleteSession(String mappedUri) {
        super(mappedUri);
    }

    @Override

    public AppiumResponse safeHandle(IHttpRequest request) {
        Logger.info("Delete session command");
        String sessionId = getSessionId(request);
        ServerInstrumentation.getInstance(InstrumentationRegistry.getInstrumentation().getContext(), PORT).stopServer();
        return new AppiumResponse(sessionId, WDStatus.SUCCESS, "Session deleted");
    }
}
