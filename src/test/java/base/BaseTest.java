package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.microsoft.playwright.*;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.ExtentManager;
import utils.ScreenshotUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BaseTest {
    private Playwright playwright;
    private Browser browser;
    protected Page page;
    private BrowserContext context;
    protected ExtentReports extent;
    protected ExtentTest test;

    @BeforeMethod
    public void setup(Method method){
        extent = ExtentManager.getInstance();
        test = extent.createTest(method.getName());

        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(1000));
        context = browser.newContext();
        page = context.newPage();

        page.setDefaultTimeout(5000);

        initializePages();
    }

    @AfterMethod
    public void tearDown(ITestResult result){
        if (result.getStatus() == ITestResult.FAILURE){
            String screenshotPath = ScreenshotUtil.takeScreenshot(page, result.getName());

            if (screenshotPath != null) {
                try {
                    test.fail(result.getThrowable(), MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                test.fail("Test failed but could not capture screenshot.");
                test.fail(result.getThrowable());
            }
        }else if (result.getStatus() == ITestResult.SUCCESS){
            test.pass("Test Passed");
        }else {
            test.skip("Test Skipped");
        }
        extent.flush();

        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    private void initializePages() {
        Class<?> clazz = this.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().getPackage() != null &&
                    field.getType().getPackage().getName().startsWith("pages")) {
                try {
                    field.setAccessible(true);
                    Object pageInstance = field.getType()
                            .getConstructor(Page.class)
                            .newInstance(page);
                    field.set(this, pageInstance);
                } catch (Exception e) {
                    System.err.println("No se pudo inicializar: " + field.getName());
                }
            }
        }
    }
}
