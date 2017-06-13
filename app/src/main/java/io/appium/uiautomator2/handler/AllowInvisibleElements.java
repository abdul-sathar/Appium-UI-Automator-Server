package io.appium.uiautomator2.handler;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.ServerConfig;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;
import org.json.JSONObject;

public class AllowInvisibleElements extends SafeRequestHandler {

    public AllowInvisibleElements(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        boolean allowInvisibleElements;

        try {
            JSONObject payload = getPayload(request);
            allowInvisibleElements = (Boolean) payload.get("allowInvisibleElements");
            ServerConfig.setAllowInvisibleElements(allowInvisibleElements);
            Logger.debug("Set the allowInvisibleElements to: " + String.valueOf(allowInvisibleElements));
        } catch (Exception e) {
            Logger.error("error setting allowInvisibleElementsHierarchy " + e.getMessage());
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        }

        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, allowInvisibleElements);
    }
}
