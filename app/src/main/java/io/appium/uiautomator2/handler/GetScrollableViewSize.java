package io.appium.uiautomator2.handler;

import android.graphics.Rect;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Device;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.ReflectionUtils;

public class GetScrollableViewSize extends SafeRequestHandler {

    public GetScrollableViewSize(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        UiScrollable uiScrollable = new UiScrollable(new UiSelector().scrollable(true));

        try {
            if (!uiScrollable.isScrollable()) {
                return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, "Could not find scrollable view");
            }

            Rect boundsRect = uiScrollable.getVisibleBounds();
            ScrollableView scrollableView = new ScrollableView(boundsRect);
            scrollableView.touchPadding = getTouchPadding(uiScrollable);
            scrollableView.scrollableOffset = getScrollableViewOffset(uiScrollable);

            return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, scrollableView.toString());
        } catch (UiObjectNotFoundException e) {
            Logger.error("Element not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        } catch (ReflectiveOperationException e) {
            Logger.error("Can not access to method or field: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR);
        }
    }

    private class ScrollableView {
        int width;
        int height;
        int top;
        int left;
        int scrollableOffset;
        int touchPadding;

        ScrollableView(Rect rect) {
            width = rect.width();
            height = rect.height();
            top = rect.top;
            left = rect.left;
        }

        @Override
        public String toString() {
            return "{" +
                    "width=" + width +
                    ", height=" + height +
                    ", top=" + top +
                    ", left=" + left +
                    ", scrollableOffset=" + scrollableOffset +
                    ", touchPadding=" + touchPadding +
                    '}';
        }
    }

    private int getScrollableViewOffset(UiScrollable uiScrollable) throws UiObjectNotFoundException {
        AccessibilityNodeInfo nodeInfo = null;
        int offset = 0;
        UiObject object = uiScrollable.getChild(new UiSelector().index(0));
        Method findAccessibilityNodeInfoMethod = ReflectionUtils.method(UiObject.class, "findAccessibilityNodeInfo", long.class);
        long WAIT_FOR_SELECTOR_TIMEOUT = (long) ReflectionUtils.getField(UiObject.class, "WAIT_FOR_SELECTOR_TIMEOUT", object);

        nodeInfo = (AccessibilityNodeInfo) ReflectionUtils.invoke(findAccessibilityNodeInfoMethod, object, WAIT_FOR_SELECTOR_TIMEOUT);

        if (nodeInfo != null) {
            Rect rect = new Rect();
            nodeInfo.getBoundsInParent(rect);
            offset = rect.height();
        }

        return offset;
    }

    private int getTouchPadding(UiScrollable uiScrollable) throws UiObjectNotFoundException, ReflectiveOperationException {
        UiObject2 uiObject2 = Device.getUiDevice().findObject(By.clazz(uiScrollable.getClassName()));

        // Try to find gesture offset for detecting that user invoke swipe method
        Field gestureField = uiObject2.getClass().getDeclaredField("mGestures");
        gestureField.setAccessible(true);
        Object gestureObject = gestureField.get(uiObject2);

        Field viewConfigField = gestureObject.getClass().getDeclaredField("mViewConfig");
        viewConfigField.setAccessible(true);
        Object viewConfigObject = viewConfigField.get(gestureObject);

        Method getScaledPagingTouchSlopMethod = viewConfigObject.getClass().getDeclaredMethod("getScaledPagingTouchSlop");
        getScaledPagingTouchSlopMethod.setAccessible(true);
        int touchPadding = (int) getScaledPagingTouchSlopMethod.invoke(viewConfigObject);

        return touchPadding / 2;
    }
}
