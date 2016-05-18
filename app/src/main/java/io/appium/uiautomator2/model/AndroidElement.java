package io.appium.uiautomator2.model;

import android.graphics.Rect;
import android.os.RemoteException;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;

import io.appium.uiautomator2.common.exceptions.InvalidCoordinatesException;
import io.appium.uiautomator2.common.exceptions.NoSuchElementAttributeException;
import io.appium.uiautomator2.utils.Device;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.Point;
import io.appium.uiautomator2.utils.PositionHelper;
import io.appium.uiautomator2.utils.UnicodeEncoder;

public class AndroidElement {
    private final UiObject2 element;
    private final String id;

    public AndroidElement(String id, UiObject2 element) {
        this.id = id;
        this.element = element;
    }

    public static boolean back() {
        return Device.getUiDevice().pressBack();
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

    public String getStringAttribute(final String attr) throws UiObjectNotFoundException {
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
            throw new NoSuchElementAttributeException("The attribute with name '" + attr + "' was not found.");
        }
        return res;
    }

    public void setText(final String text, boolean unicodeKeyboard) throws UiObjectNotFoundException {
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

    public static void wake() throws RemoteException {
        Device.getUiDevice().wakeUp();
    }

    public static void scrollTo(String scrollToString) throws UiObjectNotFoundException {
        // TODO This logic needs to be changed according to the request body from the Driver
        UiScrollable uiScrollable = new UiScrollable(new UiSelector().scrollable(true).instance(0));
        uiScrollable.scrollIntoView(new UiSelector().descriptionContains(scrollToString).instance(0));
        uiScrollable.scrollIntoView(new UiSelector().textContains(scrollToString).instance(0));
    }

    public String getId() {
        return this.id;
    }

    public Rect getBounds() throws UiObjectNotFoundException {
        Rect rectangle = element.getVisibleBounds();
        return rectangle;
    }

    public UiObject2 getChild(final BySelector sel) throws UiObjectNotFoundException {
        return element.findObject(sel);
    }

    public String getContentDesc() throws UiObjectNotFoundException {
        return element.getContentDescription();
    }

    public UiObject2 getUiObject() {
        return element;
    }

    public Point getAbsolutePosition(final Point point)
            throws UiObjectNotFoundException, InvalidCoordinatesException {
        final Rect rect = this.getBounds();

        Logger.debug("Element bounds: " + rect.toShortString());

        return PositionHelper.getAbsolutePosition(point, rect, new Point(rect.left, rect.top), false);
    }

}
