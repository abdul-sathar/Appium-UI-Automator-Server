package io.appium.uiautomator2.handler;

import android.graphics.Rect;
import android.support.test.uiautomator.UiObjectNotFoundException;

import net.minidev.json.JSONObject;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.KnownElements;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

public class Location extends BaseRequestHandler {
    public Location(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse handle(IHttpRequest request) {
        final JSONObject response = new JSONObject();
        try {
            String id = getElementId(request);
            AndroidElement element = KnownElements.getElementFromCache(id);
            Rect bounds = element.getBounds();
            response.put("x", bounds.left);
            response.put("y", bounds.top);
            Logger.info("Element found at location " + "(" + bounds.left + "," + bounds.top + ")");
        } catch (UiObjectNotFoundException e) {
            Logger.error("Element Location not found", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, e);
        }
        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, response);
    }
}
