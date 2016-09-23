package io.appium.uiautomator2.model;

import android.graphics.Rect;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.Configurator;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import io.appium.uiautomator2.common.exceptions.InvalidCoordinatesException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.NoSuchElementAttributeException;
import io.appium.uiautomator2.core.AccessibilityNodeInfoGetter;
import io.appium.uiautomator2.model.internal.CustomUiDevice;
import io.appium.uiautomator2.utils.API;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.Point;
import io.appium.uiautomator2.utils.PositionHelper;
import io.appium.uiautomator2.utils.UnicodeEncoder;

import static io.appium.uiautomator2.utils.ReflectionUtils.invoke;
import static io.appium.uiautomator2.utils.ReflectionUtils.method;

public class UiObjectElement implements AndroidElement {

    private final UiObject element;
    private final String id;

    public UiObjectElement(String id, UiObject element) {
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
        // on null returning empty string
        return element.getText() != null ? element.getText() : "";
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
            res = getResourceId();
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
        element.clearTextField();
    }

    public String getId() {
        return this.id;
    }

    public Rect getBounds() throws UiObjectNotFoundException {
        Rect rectangle = element.getVisibleBounds();
        return rectangle;
    }

    public Object getChild(final Object selector) throws UiObjectNotFoundException, InvalidSelectorException, ClassNotFoundException {
        if (selector instanceof BySelector) {
            /**
             * We can't find the child element with BySelector on UiObject,
             * as an alternative creating UiObject2 with UiObject's AccessibilityNodeInfo
             * and finding the child element on UiObject2.
             */
            AccessibilityNodeInfo nodeInfo = AccessibilityNodeInfoGetter.fromUiObject(element);
            UiObject2 uiObject2 = (UiObject2) CustomUiDevice.getInstance().findObject(nodeInfo);
            return uiObject2.findObject((BySelector) selector);
        }
        return element.getChild((UiSelector) selector);
    }

    public String getContentDesc() throws UiObjectNotFoundException {
        return element.getContentDescription();
    }

    public UiObject getUiObject() {
        return element;
    }

    public Point getAbsolutePosition(final Point point)
            throws UiObjectNotFoundException, InvalidCoordinatesException {
        final Rect rect = this.getBounds();

        Logger.debug("Element bounds: " + rect.toShortString());

        return PositionHelper.getAbsolutePosition(point, rect, new Point(rect.left, rect.top), false);
    }

    public String getResourceId() throws UiObjectNotFoundException {
        String resourceId = "";

        if (!API.API_18) {
            Logger.error("Device does not support API >= 18!");
            return resourceId;
        }

        try {
      /*
       * Unfortunately UiObject does not implement a getResourceId method.
       * There is currently no way to determine the resource-id of a given
       * element represented by UiObject. Until this support is added to
       * UiAutomater, we try to match the implementation pattern that is
       * already used by UiObject for getting attributes using reflection.
       * The returned string matches exactly what is displayed in the
       * UiAutomater inspector.
       */
            AccessibilityNodeInfo node = (AccessibilityNodeInfo) invoke(method(element.getClass(), "findAccessibilityNodeInfo", long.class),
                    element, Configurator.getInstance().getWaitForSelectorTimeout());

            if (node == null) {
                throw new UiObjectNotFoundException(element.getSelector().toString());
            }

            resourceId = node.getViewIdResourceName();
        } catch (final Exception e) {
            Logger.error("Exception: " + e + " (" + e.getMessage() + ")");
        }

        return resourceId;
    }

    public boolean dragTo(final int destX, final int destY, final int steps)
            throws UiObjectNotFoundException, InvalidCoordinatesException {
        if (API.API_18) {
            Point coords = new Point(destX, destY);
            coords = PositionHelper.getDeviceAbsPos(coords);
            return element.dragTo(coords.x.intValue(), coords.y.intValue(), steps);
        } else {
            Logger.error("Device does not support API >= 18!");
            return false;
        }
    }

    public boolean dragTo(final Object destObj, final int steps)
            throws UiObjectNotFoundException, InvalidCoordinatesException {
        if (API.API_18) {
            if (destObj instanceof UiObject) {
                return element.dragTo((UiObject) destObj, steps);
            } else if (destObj instanceof UiObject2) {
                android.graphics.Point coords = ((UiObject2) destObj).getVisibleCenter();
                return dragTo(coords.x, coords.y, steps);
            } else {
                Logger.error("Destination should be either UiObject or UiObject2");
                return false;
            }
        } else {
            Logger.error("Device does not support API >= 18!");
            return false;
        }

    }
}
