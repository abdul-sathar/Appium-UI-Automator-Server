package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.UiObjectNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.KnownElements;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.util.Logger;

public class Clear extends SafeRequestHandler {

    public Clear(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) throws JSONException {
        Logger.info("Clear element command");
        JSONObject payload = getPayload(request);
        String id = payload.getString("id");
        AndroidElement element = KnownElements.getElementFromCache(id);
        try {
            element.clear();
        } catch (UiObjectNotFoundException e) {
            Logger.error("Unable to Clear", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, e);
        }
        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, "Element Cleared");
    }
}
