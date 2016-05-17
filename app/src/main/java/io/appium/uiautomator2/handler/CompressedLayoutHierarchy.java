package io.appium.uiautomator2.handler;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Device;
import io.appium.uiautomator2.utils.Logger;

import static io.appium.uiautomator2.utils.API.API_18;

public class CompressedLayoutHierarchy extends SafeRequestHandler {

    public CompressedLayoutHierarchy(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        boolean isCompressLayout;
        // setCompressedLayoutHeirarchy doesn't exist on API <= 17

        try {
            if (API_18) {
                isCompressLayout = true;
                Device.getUiDevice().setCompressedLayoutHeirarchy(isCompressLayout);
            }
        } catch (Exception e) {
            Logger.error("error setting compressLayoutHierarchy " + e.getMessage());
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, e);
        }

        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, "Compressed Layout");
    }
}
