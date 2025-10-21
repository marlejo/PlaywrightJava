package web.utils;

import com.microsoft.playwright.Page;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class ScreenshotUtil {

    public static String takeScreenshot(Page page, String testName){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String fileName =  testName + "_" + timeStamp + ".png";

        String relativeDirectory = "screenshots" + File.separator;
        String fullDirectoryPath = System.getProperty("user.dir") + File.separator + "test-output" + File.separator + relativeDirectory;
        File dir = new File(fullDirectoryPath);

        String absoluteFilePath = fullDirectoryPath + fileName;
        String relativeFilePath = relativeDirectory + fileName;

        try {
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(absoluteFilePath)).setFullPage(false));
            return relativeFilePath;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String captureScreenshotAsBase64(Page page) {
        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(false));  // Solo viewport visible
        return Base64.getEncoder().encodeToString(screenshot);
    }

    public static String captureElementScreenshot(Page page, String selector, String screenshotName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = screenshotName + "_" + timestamp + ".png";
        String directory = System.getProperty("user.dir") + "/screenshots/";
        String filePath = directory + fileName;

        try {
            page.locator(selector).screenshot(new com.microsoft.playwright.Locator.ScreenshotOptions()
                    .setPath(Paths.get(filePath)));

            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
