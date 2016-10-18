package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.KnownElements;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Device;
import io.appium.uiautomator2.utils.Logger;

/**
 * Send keys to a given element.
 */
public class SendKeysToElement extends SafeRequestHandler {

    public SendKeysToElement(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        try {
            Logger.info("send keys to element command");
            JSONObject payload = getPayload(request);
            AndroidElement element;
            if (payload.has("elementId")) {
                String id = payload.getString("elementId");
                element = KnownElements.getElementFromCache(id);
                if (element == null) {
                    return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, "Element Not found");
                }
            } else {
                //perform action on focused element
                try {
                    element = KnownElements.geElement(android.support.test.uiautomator.By.focused(true), null /* by */);
                } catch (ElementNotFoundException e) {
                    Logger.debug("Error retrieving focused element: " + e);
                    return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, "Unable to set text without a focused element.");
                } catch (InvalidSelectorException | UiAutomator2Exception | ClassNotFoundException e) {
                    Logger.debug("Error in finding focused element: " + e);
                    return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, "Unable to find a focused element." + e.getStackTrace());
                }
            }
            boolean replace = Boolean.parseBoolean(payload.getString("replace").toString());
            String text = payload.getString("text").toString();

            boolean pressEnter = false;
            if (text.endsWith("\\n")) {
                pressEnter = true;
                text = text.replace("\\n", "");
                Logger.debug("Will press enter after setting text");
            }

            boolean unicodeKeyboard = false;
            if (payload.has("unicodeKeyboard")) {
                unicodeKeyboard = Boolean.parseBoolean(payload.getString("unicodeKeyboard").toString());
            }

            String currText = element.getText();
            new Clear("/wd/hub/session/:sessionId/element/:id/clear").handle(request);
            if (!isTextFieldCleared(element)) {
                // clear could have failed, or we could have a hint in the field
                // we'll assume it is the latter
                Logger.debug("Text not cleared. Assuming remainder is hint text.");
                currText = "";
            }
            if (!replace && currText != null) {
                text = currText + text;
            }
            element.setText(text, unicodeKeyboard);

            boolean isActionPerformed;
            String actionMsg = "";
            if (pressEnter) {
                final UiDevice d = Device.getUiDevice();
                isActionPerformed = d.pressEnter();
                if (isActionPerformed) {
                    actionMsg = "Sent keys to the device";
                } else {
                    actionMsg = "Unable to send keys to the device";
                }
            }
            return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, actionMsg);
        } catch (final UiObjectNotFoundException e) {
            Logger.error("Unable to Send Keys", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, e);
        } catch (JSONException e) {
            Logger.error("Exception while reading JSON: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.JSON_DECODER_ERROR, e);
        }
    }

    private boolean isTextFieldCleared(AndroidElement element) throws UiObjectNotFoundException {
        if (element.getText() == null) {
            return true;
        } else if (element.getText().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
}


