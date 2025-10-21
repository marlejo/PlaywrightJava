package api.tests;

import api.base.BaseApiTest;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.util.Map;

public class FirstApiTest extends BaseApiTest {

    @Override
    protected void setupSpecific(Method method) {
        // Configurar la URL base para GoRest API
        baseURL = "https://gorest.co.in/public/v2";

        // Crear el contexto de API sin baseURL para usar URLs completas
        request = playwright.request().newContext(
                new com.microsoft.playwright.APIRequest.NewContextOptions()
                        .setExtraHTTPHeaders(Map.of(
                                "Content-Type", "application/json",
                                "Accept", "application/json"
                        ))
        );

        test.info("Base URL configurada: " + baseURL);
    }

    @Test
    public void getSpecificUser() {
        test.info("Iniciando test de obtener usuario específico");

        String fullUrl = baseURL + "/users";
        logRequest("GET", fullUrl + "?id=8196663&status=inactive", null);

        // Realizar el request con URL completa
        response = request.get(fullUrl,
                RequestOptions.create()
                        .setQueryParam("id", 8196663)
                        .setQueryParam("status", "inactive")
        );

        // Loguear la respuesta
        logResponse();

        // Validaciones
        int statusCode = response.status();
        test.info("Status Code: " + statusCode);
        assertStatus(200);

        Assert.assertTrue(response.ok(), "La respuesta debería ser OK");
        test.pass("Response OK validado correctamente");

        String responseText = response.statusText();
        test.info("Status Text: " + responseText);
        Assert.assertEquals(responseText, "OK");
        test.pass("Status Text validado: " + responseText);
    }

    @Test
    public void getUsersApiTest() {
        test.info("Iniciando test de obtener lista de usuarios");

        String fullUrl = baseURL + "/users";
        logRequest("GET", fullUrl, null);

        // Realizar el request con URL completa
        response = request.get(fullUrl);

        // Loguear la respuesta
        logResponse();
        logResponseHeaders();

        // Validar status code
        int statusCode = response.status();
        test.info("Status Code: " + statusCode);
        assertStatus(200);

        Assert.assertTrue(response.ok(), "La respuesta debería ser OK");
        test.pass("Response OK validado correctamente");

        // Validar status text
        String responseText = response.statusText();
        test.info("Status Text: " + responseText);
        Assert.assertEquals(responseText, "OK");
        test.pass("Status Text validado: " + responseText);

        // Parsear y mostrar el JSON
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.body());
            test.info("Response JSON parseado correctamente");
            test.info("<pre>" + jsonResponse.toPrettyString() + "</pre>");
            test.pass("JSON parseado y validado correctamente");
        } catch (Exception e) {
            test.fail("Error al parsear JSON: " + e.getMessage());
            throw new RuntimeException(e);
        }

        // Validar URL
        test.info("Response URL: " + response.url());

        // Validar headers
        Map<String, String> headerMap = response.headers();
        test.info("Validando headers de respuesta");

        Assert.assertEquals(
                headerMap.get("content-type"),
                "application/json; charset=utf-8",
                "Content-Type debe ser application/json; charset=utf-8"
        );
        test.pass("Header Content-Type validado correctamente");

        Assert.assertEquals(
                headerMap.get("transfer-encoding"),
                "chunked",
                "Transfer-Encoding debe ser chunked"
        );
        test.pass("Header Transfer-Encoding validado correctamente");

        test.pass("Todos los headers validados exitosamente");
    }
}