package io.appium.uiautomator2.handler;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

public class AppStrings extends BaseRequestHandler {
    public AppStrings(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse handle(IHttpRequest request) {
        String jsonString = "";
        final String filePath = "/data/local/tmp/strings.json";

        try {
            final File jsonFile = new File(filePath);
            Logger.debug("Loading strings.json from file location: " + filePath);

            DataInputStream dataInput = new DataInputStream(
                    new FileInputStream(jsonFile));
            byte[] jsonBytes = new byte[(int) jsonFile.length()];
            dataInput.readFully(jsonBytes);
            dataInput.close();
            jsonString = new String(jsonBytes, "UTF-8");
            Logger.debug("json loading complete ");

        } catch (IOException e) {
            Logger.error("Error loading json from " + filePath + " " + e.getMessage());
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        }
        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, jsonString);
    }
}
