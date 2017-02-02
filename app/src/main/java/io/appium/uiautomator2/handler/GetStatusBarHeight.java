package io.appium.uiautomator2.handler;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

public class GetStatusBarHeight extends SafeRequestHandler {

    public GetStatusBarHeight(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        Logger.info("Get status bar height of the device");

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

        int height = getStatusBarHeight(instrumentation);

        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, height);
    }

    private int getStatusBarHeight(Instrumentation instrumentation) {
        int result = 0;
        int resourceId = instrumentation.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = instrumentation.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
