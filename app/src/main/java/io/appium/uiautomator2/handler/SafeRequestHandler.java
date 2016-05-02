package io.appium.uiautomator2.handler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.NoSuchElementException;

import io.appium.uiautomator2.common.exceptions.ElementNotVisibleException;
import io.appium.uiautomator2.common.exceptions.NoSuchContextException;
import io.appium.uiautomator2.common.exceptions.StaleElementReferenceException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.KnownElements;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.util.Logger;

public abstract class SafeRequestHandler extends BaseRequestHandler {

    public SafeRequestHandler(String mappedUri) {
        super(mappedUri);
    }


    protected String getIdOfKnownElement(IHttpRequest request, AndroidElement element) {
        return KnownElements.getIdOfElement(element);
    }

    protected AndroidElement getElementFromCache(IHttpRequest request, String id) {

        return KnownElements.getElementFromCache(id);
    }


    protected String[] extractKeysToSendFromPayload(IHttpRequest request) throws JSONException {
        JSONArray valueArr = getPayload(request).getJSONArray("value");
        if (valueArr == null || valueArr.length() == 0) {
            throw new UiAutomator2Exception("No key to send to an element was found.");
        }

        String[] toReturn = new String[valueArr.length()];

        for (int i = 0; i < valueArr.length(); i++) {
            toReturn[i] = valueArr.getString(i);
        }

        return toReturn;
    }

    public abstract AppiumResponse safeHandle(IHttpRequest request) throws JSONException;

    @Override
    public final AppiumResponse handle(IHttpRequest request) throws JSONException {
        try {
            return safeHandle(request);
        } catch (ElementNotVisibleException e) {
            Logger.debug("Element not visible");
            return new AppiumResponse(getSessionId(request), WDStatus.ELEMENT_NOT_VISIBLE, e);
        } catch (StaleElementReferenceException e) {
            Logger.debug("Stale element reference");
            return new AppiumResponse(getSessionId(request), WDStatus.STALE_ELEMENT_REFERENCE, e);
        } catch (IllegalStateException e) {
            Logger.debug("Invalid element state");
            return new AppiumResponse(getSessionId(request), WDStatus.INVALID_ELEMENT_STATE, e);
        } catch (NoSuchElementException e) {
            Logger.debug("No such element");
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, e);
        } catch (UnsupportedOperationException e) {
            Logger.debug("Unknown command");
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_COMMAND, e);
        } catch (NoSuchContextException e) {
            //TODO update error code when w3c spec gets updated
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_WINDOW,
                    new UiAutomator2Exception("Invalid window handle was used: only 'NATIVE_APP' and 'WEBVIEW' are supported."));
        } catch (NoClassDefFoundError e) {
            // This is a potentially interesting class path problem which should be returned to client.
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_COMMAND, e);
        } catch (Exception e) {
            Logger.error("Exception while handling action in: " + this.getClass().getName(), e);
            return AppiumResponse.forCatchAllError(getSessionId(request), e);
        } catch (Error e) {
            // Catching Errors seems like a bad idea in general but if we don't catch this, Netty will catch it anyway.
            // The advantage of catching it here is that we can propagate the Error to clients.
            Logger.error("Fatal error while handling action in: " + this.getClass().getName(), e);
            return AppiumResponse.forCatchAllError(getSessionId(request), e);
        }
    }
}
