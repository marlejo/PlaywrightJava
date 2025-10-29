package web.base;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.microsoft.playwright.*;
import org.testng.ITestResult;
import web.utils.ScreenshotUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BaseWebTest extends BaseTest{

    private Browser browser;
    protected Page page;
    private BrowserContext context;

    @Override
    protected void setupSpecific(Method method) {
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)
                        .setSlowMo(1000)
        );
        context = browser.newContext();
        page = context.newPage();
        page.setDefaultTimeout(5000);

        initializePages();
    }

    @Override
    protected void tearDownSpecific(ITestResult result) {
        if (context != null) context.close();
        if (browser != null) browser.close();
    }

    @Override
    protected void handleFailure(ITestResult result) {
        String screenshotPath = ScreenshotUtil.takeScreenshot(page, result.getName());

        if (screenshotPath != null) {
            try {
                test.fail(
                        result.getThrowable(),
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build()
                );
            } catch (Exception e) {
                e.printStackTrace();
                test.fail(result.getThrowable());
            }
        } else {
            test.fail("Test failed but could not capture screenshot.");
            test.fail(result.getThrowable());
        }
    }

    @Override
    protected void assignTestType() {
        test.assignCategory("WEB UI");
        test.assignAuthor("QA Automation Team");
        test.assignDevice("Chrome Browser");
    }

    private void initializePages() {
        Class<?> clazz = this.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().getPackage() != null &&
                    field.getType().getPackage().getName().startsWith("web.pages")) {
                try {
                    field.setAccessible(true);
                    Object pageInstance = field.getType()
                            .getConstructor(Page.class)
                            .newInstance(page);
                    field.set(this, pageInstance);
                } catch (Exception e) {
                    System.err.println("Initialize error: " + field.getName());
                }
            }
        }
    }
}
