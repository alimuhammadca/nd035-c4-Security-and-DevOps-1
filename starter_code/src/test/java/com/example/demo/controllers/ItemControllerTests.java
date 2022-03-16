package com.example.demo.controllers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.*;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;

public class ItemControllerTests {

    @BeforeClass
    public static void setUp() {
        Map<String, Object> map = new HashMap();
        map.put("username", "itemtest");
        map.put("password", "testPassword");
        map.put("confirmPassword", "testPassword");

        given().
                config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("application/JSON", ContentType.JSON))).contentType("application/JSON").body(map).
                when().
                post("http://localhost:8080/api/user/create").
                then().
                statusCode(200);
    }

    public String login() {
        Map<String, Object> map = new HashMap();
        map.put("username", "itemtest");
        map.put("password", "testPassword");

        Response response = given().
                config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("application/JSON", ContentType.JSON))).contentType("application/JSON").body(map).
                when().
                post("http://localhost:8080/login");
        String authorizationCode = response.headers().get("Authorization").getValue();
        return authorizationCode;
    }

    @Test
    public void testGetItems() throws JSONException {
        String code = login();
        Response response = given().
                header("Authorization", code).
                contentType("application/JSON").
                when().
                get("http://localhost:8080/api/item");

        Assert.assertEquals(200, response.statusCode());
        JSONArray array = new JSONArray(response.getBody().asString());

        Assert.assertEquals(1, array.getJSONObject(0).get("id"));
        Assert.assertEquals("Round Widget", array.getJSONObject(0).get("name"));
        Assert.assertEquals(2.99, array.getJSONObject(0).get("price"));
        Assert.assertEquals("A widget that is round", array.getJSONObject(0).get("description"));

    }

    @Test
    public void testGetItemById() throws JSONException {
        String code = login();
        Response response = given().
                header("Authorization", code).
                pathParam("id", 1).
                contentType("application/JSON").
                when().
                get("http://localhost:8080/api/item/{id}");

        Assert.assertEquals(200, response.statusCode());
        JSONObject jsonObject = new JSONObject(response.getBody().asString());

        Assert.assertEquals(1, jsonObject.get("id"));
        Assert.assertEquals("Round Widget", jsonObject.get("name"));
        Assert.assertEquals(2.99, jsonObject.get("price"));
        Assert.assertEquals("A widget that is round", jsonObject.get("description"));

    }

    @Test
    public void testGetItemsByName() throws JSONException {
        String code = login();
        Response response = given().
                header("Authorization", code).
                pathParam("name", "Round Widget").
                contentType("application/JSON").
                when().
                get("http://localhost:8080/api/item/name/{name}");

        Assert.assertEquals(200, response.statusCode());
        JSONArray array = new JSONArray(response.getBody().asString());

        Assert.assertEquals(1, array.getJSONObject(0).get("id"));
        Assert.assertEquals("Round Widget", array.getJSONObject(0).get("name"));
        Assert.assertEquals(2.99, array.getJSONObject(0).get("price"));
        Assert.assertEquals("A widget that is round", array.getJSONObject(0).get("description"));
    }

}

