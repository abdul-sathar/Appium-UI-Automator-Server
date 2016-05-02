package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.UiObject2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.By;
import io.appium.uiautomator2.model.KnownElements;
import io.appium.uiautomator2.model.internal.NativeAndroidBySelector;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.util.Logger;

import static io.appium.uiautomator2.util.Device.getUiDevice;

public class FindElements extends SafeRequestHandler {

    public FindElements(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) throws JSONException {
        Logger.info("Find elements command");
        KnownElements ke = new KnownElements();
        JSONObject payload = getPayload(request);
        String method = payload.getString("using");
        String selector = payload.getString("value");
        List<UiObject2> elements;
        Logger.info(String.format("find element command using '%s' with selector '%s'.",
                method, selector));
        By by = new NativeAndroidBySelector().pickFrom(method, selector);
        try {
            elements = this.findElememnts(by);
            if (elements == null) {
                return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, "Element was not found.");
            }
        } catch (UnsupportedOperationException e) {
            Logger.error("Unable Operation ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        }

        JSONArray result = new JSONArray();
        for (UiObject2 element : elements) {
            String id = UUID.randomUUID().toString();
            AndroidElement androidElement = new AndroidElement(id, element);
            ke.add(androidElement);
            JSONObject jsonElement = new JSONObject();
            jsonElement.put("ELEMENT", id);
            result.put(jsonElement);
        }
        return new AppiumResponse(getSessionId(request), result);
    }


    private List<UiObject2> findElememnts(By by) {
        if (by instanceof By.ById) {
            return getUiDevice().findObjects(android.support.test.uiautomator.By.res(by.getElementLocator()));
        }/* else if (by instanceof ByTagName) {
            return findElementByTagName(by.getElementLocator());
        }*/ else if (by instanceof By.ByLinkText) {
            return getUiDevice().findObjects(android.support.test.uiautomator.By.desc(by.getElementLocator()));
        } else if (by instanceof By.ByPartialLinkText) {
            return getUiDevice().findObjects(android.support.test.uiautomator.By
                    .descContains(by.getElementLocator
                            ()));
        } else if (by instanceof By.ByClass) {
            return getUiDevice().findObjects(android.support.test.uiautomator.By.clazz(by.getElementLocator()));
        } else if (by instanceof By.ByName) {
            return getUiDevice().findObjects(android.support.test.uiautomator.By.text(by.getElementLocator()));
        } /*else if (by instanceof ByXPath) {
            return findElementByXPath(by.getElementLocator());
        }*/

        String msg = String.format("By locator %s is curently not supported!", by.getClass().getSimpleName());
        throw new UnsupportedOperationException(msg);
    }
}
