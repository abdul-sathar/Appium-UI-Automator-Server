package io.appium.uiautomator2.handler;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.core.UiAutomatorBridge;

public class TouchDown extends TouchEvent {

    public TouchDown(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public boolean executeTouchEvent() throws UiAutomator2Exception {
        printEventDebugLine("TouchDown");
        boolean isTouchDownPerformed = UiAutomatorBridge.getInstance().getInteractionController().touchDown(clickX, clickY);
        return isTouchDownPerformed;
    }
}
