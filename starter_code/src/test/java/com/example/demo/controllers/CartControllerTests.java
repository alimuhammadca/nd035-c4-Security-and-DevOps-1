package com.example.demo.controllers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.junit.Assert.assertEquals;

public class CartControllerTests {

    @BeforeClass
    public static void setUp() {
        Map<String, Object> map = new HashMap();
        map.put("username", "carttest1");
        map.put("password", "testPassword");
        map.put("confirmPassword", "testPassword");

        given().
                config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("application/JSON", ContentType.JSON))).contentType("application/JSON").body(map).
                when().
                post("http://localhost:8080/api/user/create").
                then().
                statusCode(200);

        map = new HashMap();
        map.put("username", "carttest2");
        map.put("password", "testPassword");
        map.put("confirmPassword", "testPassword");

        given().
                config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("application/JSON", ContentType.JSON))).contentType("application/JSON").body(map).
                when().
                post("http://localhost:8080/api/user/create").
                then().
                statusCode(200);
    }

    public String login(String username) {
        Map<String, Object> map = new HashMap();
        map.put("username", username);
        map.put("password", "testPassword");

        Response response = given().
                config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("application/JSON", ContentType.JSON))).contentType("application/JSON").body(map).
                when().
                post("http://localhost:8080/login");
        String authorizationCode = response.headers().get("Authorization").getValue();
        return authorizationCode;
    }

    @Test
    public void testAddToCart() throws Exception {
        String code = login("carttest1");
        Map<String, Object> map = new HashMap();
        map.put("username", "carttest1");
        map.put("itemId", 1);
        map.put("quantity", 5);

        Response response = given().
                header("Authorization", code).
                contentType("application/JSON").
                config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("application/JSON", ContentType.JSON))).contentType("application/JSON").body(map).
                when().
                post("http://localhost:8080/api/cart/addToCart");
        assertEquals(200, response.getStatusCode());
        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        assertEquals(14.95, jsonObject.get("total"));
    }

    @Test
    public void testRemoveFromCart() throws Exception {
        String code = login("carttest2");

        Map<String, Object> map = new HashMap();
        map.put("username", "carttest2");
        map.put("itemId", 1);
        map.put("quantity", 5);

        Response response = given().
                header("Authorization", code).
                contentType("application/JSON").
                config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("application/JSON", ContentType.JSON))).contentType("application/JSON").body(map).
                when().
                post("http://localhost:8080/api/cart/addToCart");
        assertEquals(200, response.getStatusCode());

        map = new HashMap();
        map.put("username", "carttest2");
        map.put("itemId", 1);
        map.put("quantity", 2);

        response = given().
                header("Authorization", code).
                contentType("application/JSON").
                config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("application/JSON", ContentType.JSON))).contentType("application/JSON").body(map).
                when().
                post("http://localhost:8080/api/cart/removeFromCart");
        assertEquals(200, response.getStatusCode());
        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        assertEquals(8.97, jsonObject.get("total"));
    }

}
