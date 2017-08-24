package io.appium.uiautomator2.handler;

import android.graphics.Rect;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.KnownElements;
import io.appium.uiautomator2.model.UiObject2Element;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Device;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.ReflectionUtils;

public class GetElementContentSize extends SafeRequestHandler {

    public GetElementContentSize(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        Logger.info("get content size of element command");
        String id = getElementId(request);

        AndroidElement element = KnownElements.getElementFromCache(id);
        if (element == null) {
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        }
        try {
            Rect boundsRect = element.getBounds();
            ContentSize contentSize = new ContentSize(boundsRect);
            contentSize.touchPadding = getTouchPadding(element);
            contentSize.scrollableOffset = getScrollableOffcet(element);

            return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, contentSize);
        } catch (UiObjectNotFoundException e) {
            Logger.error("Element not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        } catch(StaleObjectException e){
            Logger.error("Stale Element Exception: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.STALE_ELEMENT_REFERENCE, e);
        } catch (ReflectiveOperationException e) {
            Logger.error("Can not access to method or field: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR);
        } catch (InvalidSelectorException e) {
            Logger.error("Can not access to method or field: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR);
        }
    }

    private class ContentSize {
        int width;
        int height;
        int top;
        int left;
        int scrollableOffset;
        int touchPadding;

        ContentSize(Rect rect) {
            width = rect.width();
            height = rect.height();
            top = rect.top;
            left = rect.left;
        }

        @Override
        public String toString() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("width", width);
                jsonObject.put("height", height);
                jsonObject.put("top", top);
                jsonObject.put("left", left);
                jsonObject.put("scrollableOffset", scrollableOffset);
                jsonObject.put("touchPadding", touchPadding);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject.toString();
        }
    }

    private int getScrollableOffcet(AndroidElement uiScrollable) throws UiObjectNotFoundException, ClassNotFoundException, InvalidSelectorException {
        AccessibilityNodeInfo nodeInfo = null;
        int offset = 0;
        UiObject object = (UiObject) uiScrollable.getChild(new UiSelector().index(0));
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

    private int getTouchPadding(AndroidElement element) throws UiObjectNotFoundException, ReflectiveOperationException {
        UiObject2 uiObject2;
        if (element instanceof UiObject2Element) {
            uiObject2 = Device.getUiDevice().findObject(By.clazz(((UiObject2) element.getUiObject()).getClassName()));
        } else {
            uiObject2 = Device.getUiDevice().findObject(By.clazz(((UiObject) element.getUiObject()).getClassName()));
        }
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
