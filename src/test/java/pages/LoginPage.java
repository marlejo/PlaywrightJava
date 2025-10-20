package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LoginPage {

    private final Page page;
    private final Locator username_input;
    private final Locator password_input;
    private final Locator login_btn;

    public LoginPage(Page page) {
        this.page = page;
        this.username_input = page.locator("//input[@name='username']");
        this.password_input = page.locator("//input[@name='password']");
        this.login_btn = page.locator("//button[@type='submit']");
    }

    public void typeUsername(String username){
        username_input.fill(username);
    }

    public void typePassword(String password){
        password_input.fill(password);
    }

    public void clickLoginButton(){
        login_btn.click();
    }

    public void login(String username, String password){
        username_input.fill(username);
        password_input.fill(password);
        login_btn.click();
    }
}
