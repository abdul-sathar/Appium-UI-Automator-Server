package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.By;
import io.appium.uiautomator2.model.KnownElements;
import io.appium.uiautomator2.model.internal.NativeAndroidBySelector;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.ClassInstancePair;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.XMLHierarchy;

import static io.appium.uiautomator2.model.internal.CustomUiDevice.getInstance;

public class FindElements extends SafeRequestHandler {

    public FindElements(String mappedUri) {
        super(mappedUri);
    }

    /**
     * returns  UiObject2 for an xpath expression
     **/
    private static List<UiObject2> getXPathUiObjects(final String expression, final boolean multiple, String contextId) throws ElementNotFoundException, ParserConfigurationException, InvalidSelectorException, ClassNotFoundException {
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
        return getInstance().findObjects(selectors.get(0));
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        try {
            Logger.info("Find elements command");
            KnownElements ke = new KnownElements();
            JSONObject payload = getPayload(request);
            String method = payload.getString("using");
            String selector = payload.getString("value");
            Logger.info(String.format("find element command using '%s' with selector '%s'.", method, selector));
            By by = new NativeAndroidBySelector().pickFrom(method, selector);

            List<UiObject2> elements = this.findElememnts(by);
            if (elements == null) {
                return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, "Element was not found.");
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
        } catch (UnsupportedOperationException e) {
            Logger.error("Unable Operation: UnsupportedOperationException ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (InvalidSelectorException e) {
            Logger.error("Unable Operation: InvalidSelectorException ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (JSONException e) {
            Logger.error("Exception while reading JSON: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (ElementNotFoundException e) {
            Logger.error("Element not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (ParserConfigurationException e) {
            Logger.error("Unable to parse configuration: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (ClassNotFoundException e) {
            Logger.error("Class not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        }
    }

    private List<UiObject2> findElememnts(By by) throws ElementNotFoundException, ParserConfigurationException, ClassNotFoundException, InvalidSelectorException {
        if (by instanceof By.ById) {
            return getInstance().findObjects(android.support.test.uiautomator.By.res(by.getElementLocator()));
        }/* else if (by instanceof ByTagName) {
            return findElementByTagName(by.getElementLocator());
        }*/ else if (by instanceof By.ByLinkText) {
            return getInstance().findObjects(android.support.test.uiautomator.By.desc(by.getElementLocator()));
        } else if (by instanceof By.ByPartialLinkText) {
            return getInstance().findObjects(android.support.test.uiautomator.By.descContains(by.getElementLocator()));
        } else if (by instanceof By.ByClass) {
            return getInstance().findObjects(android.support.test.uiautomator.By.clazz(by.getElementLocator()));
        } else if (by instanceof By.ByName) {
            return getInstance().findObjects(android.support.test.uiautomator.By.text(by.getElementLocator()));
        } else if (by instanceof By.ByXPath) {
            //  return findElementByXPath(by.getElementLocator());
            //TODO: need to handle the context parameter in a smart way
            return getXPathUiObjects(by.getElementLocator(), false, "");
        }

        String msg = String.format("By locator %s is curently not supported!", by.getClass().getSimpleName());
        throw new UnsupportedOperationException(msg);
    }
}
