package io.appium.uiautomator2.model;

import android.graphics.Rect;
import android.support.test.uiautomator.UiObjectNotFoundException;

import io.appium.uiautomator2.common.exceptions.InvalidCoordinatesException;
import io.appium.uiautomator2.utils.Point;

public interface AndroidElement {

    public void clear() throws UiObjectNotFoundException;

    public void click() throws UiObjectNotFoundException;

    public void longClick() throws UiObjectNotFoundException;

    public String getText() throws UiObjectNotFoundException;

    public String getName() throws UiObjectNotFoundException;

    public String getStringAttribute(final String attr) throws UiObjectNotFoundException;

    public void setText(final String text, boolean unicodeKeyboard) throws UiObjectNotFoundException;

    public String getId();

    public Rect getBounds() throws UiObjectNotFoundException;

    public Object getChild(final Object sel) throws UiObjectNotFoundException;

    public String getContentDesc() throws UiObjectNotFoundException;

    public Object getUiObject();

    public Point getAbsolutePosition(final Point point)
            throws UiObjectNotFoundException, InvalidCoordinatesException;

    public boolean dragTo(final int destX, final int destY, final int steps)
            throws UiObjectNotFoundException, InvalidCoordinatesException;

    public boolean dragTo(final Object destObj, final int steps)
            throws UiObjectNotFoundException, InvalidCoordinatesException;
}
