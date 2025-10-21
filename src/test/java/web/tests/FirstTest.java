package web.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.Test;
import web.base.BaseWebTest;

public class FirstTest extends BaseWebTest {

    @Test
    public void verifyTitle(){
        page.navigate("https://www.google.com/");

        if(page.isVisible("button:has-text('Accept all')")){
            page.click("button:has-text('Accept all')");
        }
        System.out.println("The page title is: " + page.title());
    }

    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()){
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            page.navigate("https://www.google.com/");
            System.out.println("The page title is: " + page.title());
            browser.close();
        }
    }
}
