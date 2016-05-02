package io.appium.uiautomator2.model.internal;


import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.model.By;
import io.appium.uiautomator2.util.Logger;


public class NativeAndroidBySelector {
    public static final String SELECTOR_NATIVE_ID = "id";
    // TODO review this, not perfect, but main goal is to use default bindings
    public static final String SELECTOR_L10N = "tag name";
    public static final String SELECTOR_TEXT = "link text";
    public static final String SELECTOR_PARTIAL_TEXT = "partial link text";
    public static final String SELECTOR_XPATH = "xpath";
    public static final String SELECTOR_NAME = "name";
    public static final String SELECTOR_CLASS = "class name";
    public static final String SELECTOR_CSS = "css selector";

    public By pickFrom(String method, String selector) {
        if (SELECTOR_NATIVE_ID.equals(method)) {
            return By.id(selector);
        } /*else if (SELECTOR_L10N.equals(method)) {
      return By.tagName(selector);
    } */ else if (SELECTOR_NAME.equals(method)) {
            return By.name(selector);
        } else if (SELECTOR_TEXT.equals(method)) {
            return By.linkText(selector);
        } else if (SELECTOR_PARTIAL_TEXT.equals(method)) {
            return By.partialLinkText(selector);
        } else if (SELECTOR_XPATH.equals(method)) {
            return By.xpath(selector);
        } else if (SELECTOR_CLASS.equals(method)) {
            return By.className(selector);
        } else if (SELECTOR_CSS.equals(method)) {
            return By.cssSelector(selector);
        } else {
            Logger.info("By type for methof not found: " + method);
            throw new UiAutomator2Exception("method (by) not found: " + method);
        }
    }
}
