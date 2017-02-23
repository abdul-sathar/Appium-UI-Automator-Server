package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

public class ScrollToId extends SafeRequestHandler {

    public ScrollToId(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        UiScrollable uiScrollable = new UiScrollable(new UiSelector().scrollable(true).instance(0));

        try {
            ScrollToIdArguments arguments = new ScrollToIdArguments(request);
            Logger.info(arguments);

            boolean result = uiScrollable.scrollIntoView(new UiSelector().resourceId(arguments.id));

            return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, result);
        } catch (JSONException e) {
            Logger.error("Exception while reading JSON: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.JSON_DECODER_ERROR, e);
        } catch (UiObjectNotFoundException e) {
            Logger.error("Element not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        }
    }

    private class ScrollToIdArguments {
        final String id;

        ScrollToIdArguments(final IHttpRequest request) throws JSONException {
            JSONObject payload = getPayload(request);
            id = String.valueOf(payload.get("id"));
        }

        @Override
        public String toString() {
            return "ScrollToIdArguments{" +
                    "id='" + id + '\'' +
                    '}';
        }
    }
}
