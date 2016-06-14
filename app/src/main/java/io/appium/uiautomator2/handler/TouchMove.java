package io.appium.uiautomator2.handler;

import io.appium.uiautomator2.core.UiAutomatorBridge;

public class TouchMove extends TouchEvent {

    public TouchMove(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public boolean executeTouchEvent() {
        printEventDebugLine("TouchMove");
        boolean isTouchMovePerformed = UiAutomatorBridge.getInstance().getInteractionController().touchMove(clickX, clickY);
        return isTouchMovePerformed;
    }
}
