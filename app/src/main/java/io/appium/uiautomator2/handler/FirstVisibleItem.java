package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

/**
 * This method return ID of first visible item from scrollable element
 */
public class FirstVisibleItem extends SafeRequestHandler {

    public FirstVisibleItem(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        UiScrollable uiScrollable = new UiScrollable(new UiSelector().scrollable(true).instance(0));

        try {
            String returnedID = null;
            UiObject childObject = uiScrollable.getChild(new UiSelector().index(0));
            for (int i = 0; i < childObject.getChildCount(); i++) {
                try {
                    UiObject object = childObject.getChild(new UiSelector().index(i));
                    if (object.exists()) {
                        AccessibilityNodeInfo nodeInfo = getAccessibilityNodeInfo(object);
                        if (nodeInfo != null) {
                            returnedID = nodeInfo.getViewIdResourceName();
                            break;
                        }
                    }
                } catch (UiObjectNotFoundException ignored) {
                }
            }
            return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, returnedID);
        } catch (UiObjectNotFoundException e) {
            Logger.error("Element not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        }
    }

    private AccessibilityNodeInfo getAccessibilityNodeInfo(UiObject uiObject) {
        try {
            Method findAccessibilityNodeInfoMethod;
            Field WAIT_FOR_SELECTOR_TIMEOUT;
            findAccessibilityNodeInfoMethod = UiObject.class.getDeclaredMethod("findAccessibilityNodeInfo",long.class);
            findAccessibilityNodeInfoMethod.setAccessible(true);
            WAIT_FOR_SELECTOR_TIMEOUT = UiObject.class.getDeclaredField("WAIT_FOR_SELECTOR_TIMEOUT");
            WAIT_FOR_SELECTOR_TIMEOUT.setAccessible(true);
            return (AccessibilityNodeInfo) findAccessibilityNodeInfoMethod.invoke(uiObject, WAIT_FOR_SELECTOR_TIMEOUT.getLong(uiObject));
        } catch (Exception ignored) {
        }
        return null;
    }
}
