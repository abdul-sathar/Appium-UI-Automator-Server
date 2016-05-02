package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.KnownElements;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.util.Device;
import io.appium.uiautomator2.util.Logger;

/**
 * Send keys to a given element.
 */
public class SendKeysToElement extends SafeRequestHandler {

    public SendKeysToElement(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) throws JSONException {
        Logger.info("send keys to element command");

        JSONObject payload = getPayload(request);
        String id = payload.getString("id");
        AndroidElement element = KnownElements.getElementFromCache(id);

        try {
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
            if (element.getText() == null || element.getText().isEmpty()) {
                // clear could have failed, or we could have a hint in the field
                // we'll assume it is the latter
                Logger.debug("Text not cleared. Assuming remainder is hint text.");
                currText = "";
            }
            if (!replace) {
                text = currText + text;
            }
            element.setText(text, unicodeKeyboard);

            if (pressEnter) {
                final UiDevice d = Device.getUiDevice();
                d.pressEnter();
            }
            return new AppiumResponse(getSessionId(request), "Click element");
        } catch (final UiObjectNotFoundException e) {
            Logger.error("Unable to Send Keys", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, e);
        } catch (final Exception e) { // handle NullPointerException
            Logger.error("Unable to Send Keys", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        }
    }
}


