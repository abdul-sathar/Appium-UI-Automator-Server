package io.appium.uiautomator2.handler;

import android.os.Environment;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;

public class GetDataDir extends SafeRequestHandler {

    public GetDataDir(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {

        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, Environment.getDataDirectory());
    }
}
