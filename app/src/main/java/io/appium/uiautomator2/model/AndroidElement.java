package io.appium.uiautomator2.model;

import android.os.RemoteException;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;

import io.appium.uiautomator2.common.exceptions.NoSuchElementAttributeException;
import io.appium.uiautomator2.util.Device;
import io.appium.uiautomator2.util.Logger;
import io.appium.uiautomator2.util.UnicodeEncoder;

public class AndroidElement {
    private final UiObject2 element;
    private final String id;

    public AndroidElement(String id, UiObject2 element) {
        this.id = id;
        this.element = element;
    }

    public void click() throws UiObjectNotFoundException {
        element.click();
    }

    public void longClick() throws UiObjectNotFoundException {
        element.longClick();
    }

    public String getText() throws UiObjectNotFoundException {
        return element.getText();
    }

    public String getName() throws UiObjectNotFoundException {
        return element.getContentDescription();
    }

    public String getStringAttribute(final String attr)
            throws UiObjectNotFoundException {
        String res;
        if (attr.equalsIgnoreCase("name")) {
            res = element.getText();
            if (res.equals("")) {
                res = getText();
            }
        } else if (attr.equalsIgnoreCase("contentDescription")) {
            res = element.getContentDescription();
        } else if (attr.equalsIgnoreCase("text")) {
            res = getText();
        } else if (attr.equalsIgnoreCase("className")) {
            res = element.getClassName();
        } else if (attr.equalsIgnoreCase("resourceId") || attr.equalsIgnoreCase("resource-id")) {
            res = element.getResourceName();
        } else {
            throw new NoSuchElementAttributeException("The attribute with name '" + attr
                    + "' was not found.");
        }
        return res;
    }

    public void setText(final String text, boolean unicodeKeyboard)
            throws UiObjectNotFoundException {
        if (unicodeKeyboard && UnicodeEncoder.needsEncoding(text)) {
            Logger.debug("Sending Unicode text to element: " + text);
            String encodedText = UnicodeEncoder.encode(text);
            Logger.debug("Encoded text: " + encodedText);
            element.setText(encodedText);
        } else {
            Logger.debug("Sending plain text to element: " + text);
            element.setText(text);
        }
    }

    public void clear() throws UiObjectNotFoundException {
        element.clear();
    }

    public void wake() throws RemoteException {
        Device.getUiDevice().wakeUp();
    }

    public static void back() {
        Device.getUiDevice().pressBack();
    }

    public String getId() {
        return this.id;
    }
}
