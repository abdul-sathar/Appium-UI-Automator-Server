package io.appium.uiautomator2.handler;

import io.appium.uiautomator2.core.UiAutomatorBridge;

public class TouchUp extends TouchEvent {

    public TouchUp(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public boolean executeTouchEvent() {
        printEventDebugLine("TouchUp");
        boolean isTouchUpPerformed = UiAutomatorBridge.getInstance().getInteractionController().touchUp(clickX, clickY);
        return isTouchUpPerformed;
    }
}
