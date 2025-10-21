package web.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class HomePage {

    private final Page page;
    private final Locator pim_Submenu;

    public HomePage(Page page) {
        this.page = page;
        this.pim_Submenu = page.locator("//a[@href='/web/index.php/pim/viewPimModule']");
    }

    public void clickPimSubmenu(){
        pim_Submenu.click();
    }
}
