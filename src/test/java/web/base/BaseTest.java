package web.base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.*;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import web.utils.ExtentManager;

import java.lang.reflect.Method;

/**
 * Clase base abstracta para tests con Playwright
 * Maneja el ciclo de vida de Playwright y reportes
 */
public abstract class BaseTest {
    protected Playwright playwright;
    protected ExtentReports extent;
    protected ExtentTest test;


    @BeforeMethod
    public void setup(Method method) {
        extent = ExtentManager.getInstance();
        test = extent.createTest(method.getName());

        // Agregar categorías basadas en los grupos de TestNG
        if (method.isAnnotationPresent(org.testng.annotations.Test.class)) {
            org.testng.annotations.Test testAnnotation = method.getAnnotation(org.testng.annotations.Test.class);
            String[] groups = testAnnotation.groups();
            if (groups.length > 0) {
                test.assignCategory(groups);
            }
        }

        // Agregar categoría del tipo de test (se sobrescribe en las clases hijas)
        assignTestType();

        playwright = Playwright.create();
        setupSpecific(method);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Procesar resultado del test
        processTestResult(result);

        // Teardown específico
        tearDownSpecific(result);

        // Finalizar reportes y Playwright
        extent.flush();
        if (playwright != null) playwright.close();
    }

    /**
     * Método para asignar el tipo de test (se sobrescribe en las clases hijas)
     */
    protected abstract void assignTestType();

    /**
     * Setup específico para cada tipo de test (Web o API)
     */
    protected abstract void setupSpecific(Method method);

    /**
     * Teardown específico para cada tipo de test
     */
    protected abstract void tearDownSpecific(ITestResult result);

    /**
     * Maneja el resultado de un test fallido
     */
    protected abstract void handleFailure(ITestResult result);

    /**
     * Procesa el resultado del test
     */
    private void processTestResult(ITestResult result) {
        switch (result.getStatus()) {
            case ITestResult.FAILURE:
                handleFailure(result);
                break;
            case ITestResult.SUCCESS:
                test.pass("Test Passed");
                break;
            case ITestResult.SKIP:
                test.skip("Test Skipped");
                break;
        }
    }
}