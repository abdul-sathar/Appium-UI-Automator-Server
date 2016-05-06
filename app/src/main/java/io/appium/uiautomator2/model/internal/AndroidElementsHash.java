/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.appium.uiautomator2.model.internal;

import android.graphics.Rect;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Pattern;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.utils.Logger;

import static io.appium.uiautomator2.utils.Device.getUiDevice;

/**
 * A cache of elements that the app has seen.
 */
public class AndroidElementsHash {

    private static final Pattern endsWithInstancePattern = Pattern.compile(".*INSTANCE=\\d+]$");
    private static AndroidElementsHash instance;
    private final Hashtable<String, AndroidElement> elements;
    private Integer counter;

    /**
     * Constructor
     */
    public AndroidElementsHash() {
        counter = 0;
        elements = new Hashtable<String, AndroidElement>();
    }

    public static AndroidElementsHash getInstance() {
        if (AndroidElementsHash.instance == null) {
            AndroidElementsHash.instance = new AndroidElementsHash();
        }
        return AndroidElementsHash.instance;
    }

    /**
     * @param element
     * @return
     */
    public AndroidElement addElement(final UiObject2 element) {
        counter++;
        final String key = counter.toString();
        final AndroidElement el = new AndroidElement(key, element);
        elements.put(key, el);
        return el;
    }

    /**
     * Return an element given an Id.
     *
     * @return {@link AndroidElement}
     */
    public AndroidElement getElement(final String key) {
        return elements.get(key);
    }

    /**
     * Return an elements child given the key (context id), or uses the selector to get the
     * element.
     *
     * @param key Element id.
     * @return {@link AndroidElement}
     */
    public AndroidElement getElement(final BySelector sel, final String key) throws ElementNotFoundException {
        AndroidElement baseEl;
        baseEl = elements.get(key);
        UiObject2 el;

        if (baseEl == null) {
            el = getUiDevice().findObject(sel);
        } else {
            try {
                el = baseEl.getChild(sel);
                // there are times when UiAutomator returns an element from another parent
                // so we need to see if it is within the bounds of the parent
                if (!Rect.intersects(baseEl.getBounds(), el.getVisibleBounds())) {
                    Logger.debug("UiAutomator returned a child element but it is " +
                            "outside the bounds of the parent. Assuming no " +
                            "child element found");
                    throw new ElementNotFoundException();
                }
            } catch (final UiObjectNotFoundException e) {
                throw new ElementNotFoundException();
            }
        }

        // As per the UiAutomator UiAutomator V1(bootstrap) implementation, e1 is UiObject
        // instance and has exists() method but in e1 is UiObject2 and doesn't have exists()
        // so using isEnabled() in place of exists() in all useages.
        // need to monitor the behaviour and figure out workaround if causes any issue
        if (el.isEnabled()) {
            return addElement(el);
        } else {
            throw new ElementNotFoundException();
        }
    }

    /**
     * Same as {@link #getElement(BySelector, String)} but for multiple elements at once.
     *
     * @return ArrayList<{@link AndroidElement}>
     */
    public ArrayList<AndroidElement> getElements(final BySelector sel, final String key) throws UiObjectNotFoundException {
        boolean keepSearching = true;
        final String selectorString = sel.toString();
        final boolean useIndex = selectorString.contains("CLASS_REGEX=");
        final boolean endsWithInstance = endsWithInstancePattern.matcher(selectorString).matches();
        Logger.debug("getElements selector:" + selectorString);
        final ArrayList<AndroidElement> elements = new ArrayList<AndroidElement>();

        // If sel is UiSelector[CLASS=android.widget.Button, INSTANCE=0]
        // then invoking instance with a non-0 argument will corrupt the selector.
        //
        // sel.instance(1) will transform the selector into:
        // UiSelector[CLASS=android.widget.Button, INSTANCE=1]
        //
        // The selector now points to an entirely different element.
        if (endsWithInstance) {
            Logger.debug("Selector ends with instance.");
            // There's exactly one element when using instance.
            UiObject2 instanceObj = getUiDevice().findObject(sel);
            if (instanceObj != null && instanceObj.isEnabled()) {
                elements.add(addElement(instanceObj));
            }
            return elements;
        }

        UiObject2 lastFoundObj;
        final AndroidElement baseEl = this.getElement(key);

        BySelector tmp;
        int counter = 0;
        while (keepSearching) {
            if (baseEl == null) {
                Logger.debug("Element[" + key + "] is null: (" + counter + ")");

                // TODO: commednted below if else block because there is no way to find element
                // using index and instance on BySelector object
        /*if (useIndex) {
          Logger.debug("  using index...");
          tmp = sel.index(counter);
        } else {
          tmp = sel.instance(counter);
        }*/

                Logger.debug("getElements tmp selector:" + sel.toString());

                lastFoundObj = getUiDevice().findObject(sel);
            } else {
                Logger.debug("Element[" + key + "] is " + baseEl.getId() + ", counter: " + counter);
                lastFoundObj = baseEl.getChild(sel);
            }
            counter++;
            if (lastFoundObj != null && lastFoundObj.isEnabled()) {
                elements.add(addElement(lastFoundObj));
            } else {
                keepSearching = false;
            }
        }
        return elements;
    }
}
