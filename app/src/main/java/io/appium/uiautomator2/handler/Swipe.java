package io.appium.uiautomator2.handler;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Device;
import io.appium.uiautomator2.utils.Logger;

public class Swipe extends SafeRequestHandler {

    public Swipe(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        try {
            JSONObject payload = getPayload(request);
            boolean isActionPerformed;
            String actionMsg;
            Logger.info("Json Payload: ", payload.toString());

            // TODO Swipe on Element

            int startX = payload.getInt("startX");
            int startY = payload.getInt("startY");
            int endX = payload.getInt("endX");
            int endY = payload.getInt("endY");
            int steps = payload.getInt("steps");

            isActionPerformed = Device.getUiDevice().swipe(startX, startY, endX, endY, steps);

            if (isActionPerformed) {
                actionMsg = "Swiping from (" + startX + "," + startY + ") to (" + endX + "," + endY + ") with " + steps + " steps";
                Logger.info("Swipe Performed ", actionMsg);
                return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, actionMsg);
            } else {
                actionMsg = "Swipe failed to performed";
                Logger.info(actionMsg);
                return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, actionMsg);
            }
        } catch (JSONException e) {
            Logger.error("Exception while reading JSON: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.JSON_DECODER_ERROR, e);
        }
    }
}
