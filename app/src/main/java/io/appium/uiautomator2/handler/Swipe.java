package io.appium.uiautomator2.handler;

import com.jayway.jsonpath.JsonPath;

import org.json.JSONException;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Device;
import io.appium.uiautomator2.utils.Logger;

public class Swipe extends BaseRequestHandler {
    public Swipe(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse handle(IHttpRequest request) throws JSONException {
        String json = getPayload(request).toString();
        boolean isActionPerformed;
        int startX, startY, endX, endY, steps;
        String actionMsg = "", options = "$.actions[*].options.";
        Logger.info("Json Payload: ", json);
        try {
            startX = JsonPath.compile(options + "x[0]").read(json);
            startY = JsonPath.compile(options + "y[0]").read(json);
            endX = JsonPath.compile(options + "x[1]").read(json);
            endY = JsonPath.compile(options + "y[1]").read(json);
            steps = JsonPath.compile(options + "ms[0]").read(json);

            isActionPerformed = Device.getUiDevice().swipe(startX, startY, endX, endY, steps);

            if (isActionPerformed) {
                actionMsg = "Swiping from (" + startX + "," + startY + ") to (" + endX + "," + endY + ") with " + steps + " steps";
                Logger.info("Swipe Performed ", actionMsg);
            } else actionMsg = "Swipe failed to performed";
        } catch (Exception e) {
            Logger.error(actionMsg, e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, actionMsg);
        }

        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, actionMsg);
    }
}
