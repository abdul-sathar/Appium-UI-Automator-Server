package io.appium.uiautomator2.handler;


import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiSelector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.common.exceptions.UiSelectorSyntaxException;
import io.appium.uiautomator2.handler.request.SafeRequestHandler;
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
import io.appium.uiautomator2.utils.ClassInstancePair;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.UiAutomatorParser;
import io.appium.uiautomator2.utils.XMLHierarchy;

import static io.appium.uiautomator2.model.internal.CustomUiDevice.getInstance;
import static io.appium.uiautomator2.utils.Device.getAndroidElement;
import static io.appium.uiautomator2.utils.Device.getUiDevice;

public class FindElement extends SafeRequestHandler {

    public FindElement(String mappedUri) {
        super(mappedUri);
    }


    /**
     * returns  UiObject2 for an xpath expression
     */
    private static Object getXPathUiObject(final String expression, final boolean multiple, String contextId) throws ElementNotFoundException, ParserConfigurationException, InvalidSelectorException, ClassNotFoundException, UiAutomator2Exception {
        final List<BySelector> selectors = new ArrayList<BySelector>();

        final ArrayList<ClassInstancePair> pairs = contextId.equals("") ? XMLHierarchy.getClassInstancePairs(expression) : XMLHierarchy.getClassInstancePairs(expression, contextId);

        if (!multiple) {
            if (pairs.size() == 0) {
                throw new ElementNotFoundException();
            }
            selectors.add(pairs.get(0).getSelector());
        } else {
            for (final ClassInstancePair pair : pairs) {
                selectors.add(pair.getSelector());
            }
        }
        return getInstance().findObjects(selectors.get(0)).get((Integer.parseInt(pairs.get(0).getInstance())));
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        try {
            Logger.info("Find element command");
            KnownElements ke = new KnownElements();
            JSONObject payload = getPayload(request);
            String method = payload.getString("strategy");
            String selector = payload.getString("selector");
            Logger.info(String.format("find element command using '%s' with selector '%s'.", method, selector));
            By by = new NativeAndroidBySelector().pickFrom(method, selector);

            getUiDevice().waitForIdle();
            Object element = this.findElement(by);
            if (element == null) {
                return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, false);
            } else {
                String id = UUID.randomUUID().toString();
                AndroidElement androidElement = getAndroidElement(id, element);
                ke.add(androidElement);
                JSONObject result = new JSONObject();
                result.put("ELEMENT", id);
                return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, result);
            }
        } catch (UnsupportedOperationException e) {
            Logger.error("Unsupported operation: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (InvalidSelectorException e) {
            Logger.error("Invalid selector: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.INVALID_SELECTOR, e);
        } catch (ElementNotFoundException e) {
            Logger.error("Element not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, e);
        } catch (ParserConfigurationException e) {
            Logger.error("Unable to parse configuration: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (ClassNotFoundException e) {
            Logger.error("Class not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (JSONException e) {
            Logger.error("Exception while reading JSON: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.JSON_DECODER_ERROR, e);
        } catch (UiSelectorSyntaxException e) {
            Logger.error("Unable to parse UiSelector: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (UiAutomator2Exception e) {
            Logger.error("Exception while finding element: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.JSON_DECODER_ERROR, e);
        }
    }

    /**
     * returns  UiObject2 for an xpath expression
     */
    private Object findElement(By by) throws InvalidSelectorException, ElementNotFoundException, ParserConfigurationException, ClassNotFoundException, UiSelectorSyntaxException, UiAutomator2Exception {
        if (by instanceof ById) {
            return getInstance().findObject(android.support.test.uiautomator.By.res(by.getElementLocator()));
        } else if (by instanceof ByLinkText) {
            return getInstance().findObject(android.support.test.uiautomator.By.desc(by.getElementLocator()));
        } else if (by instanceof By.ByPartialLinkText) {
            return getInstance().findObject(android.support.test.uiautomator.By.descContains(by.getElementLocator()));
        } else if (by instanceof ByClass) {
            return getInstance().findObject(android.support.test.uiautomator.By.clazz(by.getElementLocator()));
        } else if (by instanceof ByName) {
            return getInstance().findObject(android.support.test.uiautomator.By.text(by.getElementLocator()));
        } else if (by instanceof By.ByXPath) {
            return getXPathUiObject(by.getElementLocator(), false, "");
        } else if (by instanceof By.ByAndroidUiAutomator) {
            return getInstance().findObject(findByUiAutomator(by.getElementLocator()));
        }
        String msg = String.format("By locator %s is curently not supported!", by.getClass().getSimpleName());
        throw new UnsupportedOperationException(msg);
    }

    /**
     * finds the UiSelector for given expression
     */
    public UiSelector findByUiAutomator(String expression) throws UiSelectorSyntaxException {
        List<UiSelector> parsedSelectors = null;
        UiAutomatorParser uiAutomatorParser = new UiAutomatorParser();
        final List<UiSelector> selectors = new ArrayList<UiSelector>();
        try {
            parsedSelectors = uiAutomatorParser.parse(expression);
        } catch (final UiSelectorSyntaxException e) {
            throw new UiSelectorSyntaxException(
                    "Could not parse UiSelector argument: " + e.getMessage());
        }

        for (final UiSelector selector : parsedSelectors) {
            selectors.add(selector);
        }
        return selectors.get(0);
    }

}
