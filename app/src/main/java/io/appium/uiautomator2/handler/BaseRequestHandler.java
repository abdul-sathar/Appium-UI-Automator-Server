package io.appium.uiautomator2.handler;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.AppiumServlet;

public abstract class BaseRequestHandler {

    private final String mappedUri;

    public BaseRequestHandler(String mappedUri) {
        this.mappedUri = mappedUri;
    }

    public String getMappedUri() {
        return mappedUri;
    }

    public String getElementId(IHttpRequest request) {
        return (String) request.data().get(AppiumServlet.ELEMENT_ID_KEY);

    }

    public String getNameAttribute(IHttpRequest request) {

        return (String) request.data().get(AppiumServlet.NAME_ID_KEY);
    }

    public JSONObject getPayload(IHttpRequest request) throws JSONException {
        String json = request.body();
        if (json != null && !json.isEmpty()) {
            return new JSONObject(json);
        }
        return new JSONObject();
    }

    public String getSessionId(IHttpRequest request) {

        return (String) request.data().get(AppiumServlet.SESSION_ID_KEY);
    }

    public abstract AppiumResponse handle(IHttpRequest request) throws JSONException;

    public AppiumResponse safeHandle(IHttpRequest request) throws JSONException {
        return handle(request);
    }
}
