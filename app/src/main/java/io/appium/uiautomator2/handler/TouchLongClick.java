package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.UiObjectNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.KnownElements;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

public class TouchLongClick extends BaseRequestHandler {
    public TouchLongClick(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse handle(IHttpRequest request) throws JSONException {
        JSONObject payload = getPayload(request);
        String id = payload.getString("id");

        AndroidElement element = KnownElements.getElementFromCache(id);
        try {
            element.longClick();
        } catch (UiObjectNotFoundException e) {
            Logger.error("Unable to Click on the element", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, e);
        }
        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, "Long Click action performed");
    }
}
