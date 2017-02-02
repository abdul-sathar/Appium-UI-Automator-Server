package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

/**
 * This handler used for checking for scrollable view presented or not.
 */
public class IsScrollable extends SafeRequestHandler {

    public IsScrollable(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        Logger.info("Check for presented scrollable view");

        UiScrollable uiScrollable = new UiScrollable(new UiSelector().scrollable(true));

        try {
            return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, uiScrollable.isScrollable());
        } catch (UiObjectNotFoundException e) {
            Logger.error("Element not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        }
    }
}
