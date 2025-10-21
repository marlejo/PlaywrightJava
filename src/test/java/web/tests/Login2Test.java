package web.tests;

import org.testng.annotations.Test;
import web.base.BaseWebTest;
import web.pages.HomePage;
import web.pages.LoginPage;

public class Login2Test extends BaseWebTest {

    LoginPage loginPage;
    HomePage homePage;

    @Test
    public void test() {
        test.info("Navigating to login page");
        page.navigate("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
        test.info("Typing credentials info");
        loginPage.login("Admin", "admin123");
        test.info("Clicking pim submenu");
        homePage.clickPimSubmenu();
    }
}
