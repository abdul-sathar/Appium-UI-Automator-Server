package io.appium.uiautomator2.handler;

import android.graphics.Rect;
import android.support.test.uiautomator.UiObjectNotFoundException;

import com.jayway.jsonpath.JsonPath;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.KnownElements;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

public abstract class TouchEvent extends SafeRequestHandler {
    protected int clickX, clickY;
    private AndroidElement element;

    public TouchEvent(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        String path = "$.params.";
        try {
            JSONObject json = getPayload(request);
            if (json.has("id")) {
                String id = json.getString("elementId");
                element = KnownElements.getElementFromCache(id);

                if (element != null) {
                    final Rect bounds = element.getBounds();
                    clickX = bounds.centerX();
                    clickY = bounds.centerY();
                } else {
                    throw new UiObjectNotFoundException("TouchEvent element does not exist.");
                }
            } else { // no element so extract x and y from params
                clickX = JsonPath.compile(path + "x").read(json.toString());
                clickY = JsonPath.compile(path + "y").read(json.toString());
            }

            if (executeTouchEvent())
                return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, true);
            else
                return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, false);


        } catch (JSONException e) {
            return new AppiumResponse(getSessionId(request), WDStatus.JSON_DECODER_ERROR, e);
        } catch (UiObjectNotFoundException e) {
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (UiAutomator2Exception e) {
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        }
    }

    protected abstract boolean executeTouchEvent() throws UiObjectNotFoundException, UiAutomator2Exception;

    protected void printEventDebugLine(final String methodName,
                                       final Integer... duration) {
        String extra = "";
        if (duration.length > 0) {
            extra = ", duration: " + duration[0];
        }
        Logger.debug("Performing " + methodName + " x: " + clickX + ", y: " + clickY + extra);
    }
}
