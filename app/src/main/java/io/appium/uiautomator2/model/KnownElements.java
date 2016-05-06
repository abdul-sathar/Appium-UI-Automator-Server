package io.appium.uiautomator2.model;

import java.util.HashMap;
import java.util.Map;

public class KnownElements {
    private static Map<String, AndroidElement> cache = new HashMap<String, AndroidElement>();

    private static String getCacheKey(AndroidElement element) {
        for (Map.Entry<String, AndroidElement> entry : cache.entrySet()) {
            if (entry.getValue().equals(element)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String getIdOfElement(AndroidElement element) {
        if (cache.containsValue(element)) {
            return getCacheKey(element);
        }
        return null;
    }

    public static AndroidElement getElementFromCache(String id) {
        return cache.get(id);
    }

    public String add(AndroidElement element) {
        if (cache.containsValue(element)) {
            return getCacheKey(element);
        }
        cache.put(element.getId(), element);
        return element.getId();
    }

    public void clear() {
        if (!cache.isEmpty()) {
            cache.clear();
            System.gc();
        }

    }
}
