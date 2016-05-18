package io.appium.uiautomator2.handler;

import com.jayway.jsonpath.JsonPath;

import org.json.JSONException;

import io.appium.uiautomator2.handler.request.BaseRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Device;
import io.appium.uiautomator2.utils.Logger;

public class Drag extends BaseRequestHandler {
    public Drag(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse handle(IHttpRequest request) {

        boolean isActionPerformed;
        int startX, startY, endX, endY, steps;
        String actionMsg = "", options = "$.params.";

        try {
            String json = getPayload(request).toString();
            Logger.info("Json Payload: ", json);
            startX = JsonPath.compile(options + "startX").read(json);
            startY = JsonPath.compile(options + "startY").read(json);
            endX = JsonPath.compile(options + "endX").read(json);
            endY = JsonPath.compile(options + "endY").read(json);
            steps = JsonPath.compile(options + "steps").read(json);

            isActionPerformed = Device.getUiDevice().drag(startX, startY, endX, endY, steps);

            if (isActionPerformed) {
                actionMsg = "Drag from (" + startX + "," + startY + ") to (" + endX + "," + endY + ") with " + steps + " steps";
                Logger.info("Drag Performed ", actionMsg);
            } else
                actionMsg = "Drag failed to performed";
        } catch (JSONException e) {
            Logger.error("Unable to parse JSON data: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.JSON_DECODER_ERROR, e);
        }

        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, actionMsg);
    }
}
