package io.appium.uiautomator2.unittest.test;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.squareup.okhttp.MediaType;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import io.appium.uiautomator2.model.By;
import io.appium.uiautomator2.server.ServerInstrumentation;
import io.appium.uiautomator2.util.Device;
import io.appium.uiautomator2.util.Logger;

import static io.appium.uiautomator2.unittest.test.TestUtil.click;
import static io.appium.uiautomator2.unittest.test.TestUtil.findElement;
import static io.appium.uiautomator2.unittest.test.TestUtil.findElements;
import static io.appium.uiautomator2.unittest.test.TestUtil.getAttribute;
import static io.appium.uiautomator2.unittest.test.TestUtil.getStringValueInJsonObject;
import static io.appium.uiautomator2.unittest.test.TestUtil.getText;
import static io.appium.uiautomator2.unittest.test.TestUtil.sendKeys;
import static io.appium.uiautomator2.unittest.test.TestUtil.waitForElement;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class HandlersTest {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final int port = 8080;
    private static final String testAppPkg = "io.appium.android.apis";
    private static final int SECOND = 1000;
    private static ServerInstrumentation serverInstrumentation;
    private static Context ctx;

    /**
     * start io.appium.uiautomator2.server and launch the application main activity     *
     *
     * @throws InterruptedException
     */
    @BeforeClass
    public static void beforeStartServer() throws InterruptedException {
        if (serverInstrumentation == null) {
            Assert.assertNotNull(Device.getUiDevice());
            ctx = InstrumentationRegistry.getInstrumentation().getContext();
            serverInstrumentation = ServerInstrumentation.getInstance(ctx, port);
            Logger.info("[AppiumUiAutomator2Server]", " Starting Server ");
            Intent intent = new Intent()
                    .setClassName(testAppPkg, testAppPkg + ".ApiDemos")
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
            serverInstrumentation.startServer();
            Logger.info("[AppiumUiAutomator2Server]", " waiting for app to launch ");
            TestHelper.waitForNetty();
            TestHelper.waitForAppToLaunch(testAppPkg, 15 * SECOND);
        }
    }

    /**
     * click on element
     *
     * @throws JSONException
     */
    @Test
    public void clickElementTest() throws JSONException {
        waitForElement(By.name("Accessibility"), 5 * SECOND);
        String element = findElement(By.name("Accessibility"));
        Logger.info("[AppiumUiAutomator2Server]", " click element:" + element);
        Assert.assertNotNull(element);
        click(element);
    }

    /**
     * find element
     */
    @Test
    public void findElementTest() {
        waitForElement(By.name("Custom View"), 5 * SECOND);
        String response = findElement(By.name("Custom View"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.name: " + response);
        Assert.assertNotNull(response);

        response = findElement(By.id("android:id/action_bar_title"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.id: " + response);
        Assert.assertNotNull(response);

        response = findElement(By.className("android.widget.TextView"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.className: " +
                response);
        Assert.assertNotNull(response);
    }

    /**
     * findElements Test
     */
    @Test
    public void findElementsTest() {
        String response = findElements(By.className("android.widget.TextView"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.className: " + response);
        Assert.assertNotNull(response);
    }


    /**
     * get Attribute Text
     *
     * @throws JSONException
     */
    @Test
    public void getAttributeTest() throws JSONException {
        String element = findElement(By.className("android.widget.TextView"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.className: " + element);
        String result = getAttribute(element, "resourceId");
        Logger.info("[AppiumUiAutomator2Server]", " getAttribute: resourceId - " + result);
        Logger.info("[AppiumUiAutomator2Server]", " getAttribute: contentDescription - " + getAttribute(element, "contentDescription"));
        Logger.info("[AppiumUiAutomator2Server]", " getAttribute: text - " + getAttribute(element, "text"));
        Logger.info("[AppiumUiAutomator2Server]", " getAttribute: className - " +
                getAttribute(element,
                        "className"));
        Logger.info("[AppiumUiAutomator2Server]", " getAttribute: name - " + getAttribute(element, "name"));
    }

    /**
     * get Element Text
     *
     * @throws JSONException
     */
    @Test
    public void getTextTest() throws JSONException {
        String element = findElement(By.className("android.widget.TextView"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.className: " + element);
        String elementTxt = getText(element);
        Assert.assertEquals(getStringValueInJsonObject(elementTxt, "value"), "API Demos");
    }

    /**
     * send keys to element
     *
     * @throws JSONException
     * @throws InterruptedException
     */

    @Test
    public void sendKeysTest() throws JSONException, InterruptedException {
        waitForElement(By.name("Views"), 10 * SECOND);
        click(findElement(By.name("Views")));
        waitForElement(By.name("Controls"), 10 * SECOND);
        click(findElement(By.name("Controls")));
        waitForElement(By.name("1. Light Theme"), 10 * SECOND);
        click(findElement(By.name("1. Light Theme")));
        waitForElement(By.id("io.appium.android.apis:id/edit"), 5 * SECOND);
        sendKeys(findElement(By.id("io.appium.android.apis:id/edit")), "Dummy Theme");
        Assert.assertEquals("Dummy Theme", getStringValueInJsonObject(getText(findElement(By.id("io.appium.android.apis:id/edit"))), "value"));
    }
}
