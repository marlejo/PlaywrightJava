package api.base;

import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.microsoft.playwright.*;
import org.testng.ITestResult;
import web.base.BaseTest;

import java.lang.reflect.Method;
import java.util.Map;

public class BaseApiTest extends BaseTest{

    protected APIRequestContext request;
    protected APIResponse response;
    protected String baseURL;

    @Override
    protected void setupSpecific(Method method) {
        // Configurar la URL base desde properties o variable de entorno
        baseURL = System.getProperty("api.base.url", "https://api.example.com");

        // Crear el contexto de API con configuración por defecto
        request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(baseURL)
                .setExtraHTTPHeaders(Map.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"
                ))
        );

        test.info("Base URL: " + baseURL);
    }

    @Override
    protected void tearDownSpecific(ITestResult result) {
        if (request != null) request.dispose();
    }

    @Override
    protected void handleFailure(ITestResult result) {
        // Agregar información de la respuesta API al reporte si existe
        if (response != null) {
            test.fail("<b>Status Code:</b> " + response.status());
            test.fail("<b>Status Text:</b> " + response.statusText());
            test.fail("<b>URL:</b> " + response.url());

            try {
                String responseBody = new String(response.body());
                test.fail(MarkupHelper.createCodeBlock(responseBody, CodeLanguage.JSON));
            } catch (Exception e) {
                test.fail("No se pudo obtener el cuerpo de la respuesta");
            }
        }

        test.fail(result.getThrowable());
    }

    /**
     * Método helper para loguear request en el reporte
     */
    protected void logRequest(String method, String endpoint, Object body) {
        test.info("<b>Request:</b> " + method + " " + endpoint);
        if (body != null) {
            test.info(MarkupHelper.createCodeBlock(body.toString(), CodeLanguage.JSON));
        }
    }

    /**
     * Método helper para loguear response en el reporte
     */
    protected void logResponse() {
        if (response != null) {
            test.info("<b>Status:</b> " + response.status() + " " + response.statusText());
            test.info("<b>URL:</b> " + response.url());

            try {
                String responseBody = new String(response.body());
                test.info(MarkupHelper.createCodeBlock(responseBody, CodeLanguage.JSON));
            } catch (Exception e) {
                test.warning("No se pudo obtener el cuerpo de la respuesta");
            }
        }
    }

    /**
     * Método helper para loguear headers de response
     */
    protected void logResponseHeaders() {
        if (response != null) {
            StringBuilder headers = new StringBuilder();
            response.headers().forEach((key, value) ->
                    headers.append(key).append(": ").append(value).append("\n")
            );
            test.info("<b>Response Headers:</b><br><pre>" + headers.toString() + "</pre>");
        }
    }

    /**
     * Valida el status code y lo registra en el reporte
     */
    protected void assertStatus(int expectedStatus) {
        int actualStatus = response.status();
        if (actualStatus == expectedStatus) {
            test.pass("Status code correcto: " + actualStatus);
        } else {
            test.fail("Status code incorrecto. Esperado: " + expectedStatus + ", Actual: " + actualStatus);
            throw new AssertionError("Status code incorrecto. Esperado: " + expectedStatus + ", Actual: " + actualStatus);
        }
    }

    /**
     * Verifica si el response es exitoso (200-299)
     */
    protected boolean isSuccessful() {
        return response.ok();
    }
}
