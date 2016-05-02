package io.appium.uiautomator2.handler;


import android.support.test.uiautomator.UiObject2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.By;
import io.appium.uiautomator2.model.By.ByClass;
import io.appium.uiautomator2.model.By.ById;
import io.appium.uiautomator2.model.By.ByLinkText;
import io.appium.uiautomator2.model.By.ByName;
import io.appium.uiautomator2.model.KnownElements;
import io.appium.uiautomator2.model.internal.NativeAndroidBySelector;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.util.Logger;

import static io.appium.uiautomator2.util.Device.getUiDevice;

public class FindElement extends SafeRequestHandler {
    //private static  UiDevice uiDevice = Device.getUiDevice();

    public FindElement(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) throws JSONException {
        Logger.info("Find element command");
        KnownElements ke = new KnownElements();
        JSONObject payload = getPayload(request);
        String method = payload.getString("using");
        String selector = payload.getString("value");
        String id;
        Logger.info(String.format("find element command using '%s' with selector '%s'.",
                method, selector));
        By by = new NativeAndroidBySelector().pickFrom(method, selector);
        UiObject2 element;
        try {
            element = this.findElememnt(by);
            if (element == null) {
                return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, "Element was not found.");
            } else {
                id = UUID.randomUUID().toString();
            }
        } catch (UnsupportedOperationException e) {
            Logger.error("Unable Operation ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (Exception e) {
            Logger.error("unable to perform action:" + e);
            e.printStackTrace();
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        }

        AndroidElement androidElement = new AndroidElement(id, element);
        ke.add(androidElement);
        JSONObject result = new JSONObject();
        result.put("ELEMENT", id);
        return new AppiumResponse(getSessionId(request), result);
    }


    private UiObject2 findElememnt(By by) {
        if (by instanceof ById) {
            return getUiDevice().findObject(android.support.test.uiautomator.By.res(by.getElementLocator()));
        }/* else if (by instanceof ByTagName) {
            return findElementByTagName(by.getElementLocator());
        }*/ else if (by instanceof ByLinkText) {
            return getUiDevice().findObject(android.support.test.uiautomator.By.desc(by.getElementLocator()));
        } else if (by instanceof By.ByPartialLinkText) {
            return getUiDevice().findObject(android.support.test.uiautomator.By.descContains(by.getElementLocator()));
        } else if (by instanceof ByClass) {
            return getUiDevice().findObject(android.support.test.uiautomator.By.clazz(by.getElementLocator()));
        } else if (by instanceof ByName) {
            return getUiDevice().findObject(android.support.test.uiautomator.By.text(by.getElementLocator()));
        } /*else if (by instanceof ByXPath) {
            return findElementByXPath(by.getElementLocator());
        }*/

        String msg = String.format("By locator %s is curently not supported!", by.getClass().getSimpleName());
        throw new UnsupportedOperationException(msg);
    }
}
