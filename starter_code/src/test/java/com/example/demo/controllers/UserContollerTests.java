package com.example.demo.controllers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.hamcrest.Matchers.is;

public class UserContollerTests {

    @BeforeClass
    public static void setUp() {
        Map<String, Object> map = new HashMap();
        map.put("username", "test");
        map.put("password", "testPassword");
        map.put("confirmPassword", "testPassword");

        given().
                config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("application/JSON", ContentType.JSON))).contentType("application/JSON").body(map).
                when().
                post("http://localhost:8081/api/user/create").
                then().
                statusCode(200).
                assertThat().
                body("username", is("test")).assertThat().body("id", is(1));
    }

    @Test
    public void testCreateUserFailed() {
        Map<String, Object> map = new HashMap();
        map.put("username", "test1");
        map.put("password", "testPassword");
        map.put("confirmPassword", "testPassword1");

        given().
                config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("application/JSON", ContentType.JSON))).contentType("application/JSON").body(map).
                when().
                post("http://localhost:8081/api/user/create").
                then().
                statusCode(400);
    }

    @Test
    public void testCreateUserSuccessfully() {
        Map<String, Object> map = new HashMap();
        map.put("username", "test2");
        map.put("password", "testPassword");
        map.put("confirmPassword", "testPassword");
        given().

        config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("application/JSON", ContentType.JSON))).contentType("application/JSON").body(map).
        when().
        post("http://localhost:8081/api/user/create").
        then().
        statusCode(200).
        assertThat().
        body("username", is("test2")).assertThat().body("id", is(2));
    }

    @Test
    public void testLoginSuccessfully() {
        Map<String, Object> map = new HashMap();
        map.put("username", "test");
        map.put("password", "testPassword");

        given().
                config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("application/JSON", ContentType.JSON))).contentType("application/JSON").body(map).
                when().
                post("http://localhost:8081/login").then().statusCode(200);
    }

    public String login() {
        Map<String, Object> map = new HashMap();
        map.put("username", "test");
        map.put("password", "testPassword");

        Response response = given().
                config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("application/JSON", ContentType.JSON))).contentType("application/JSON").body(map).
                when().
                post("http://localhost:8081/login");
        String authorizationCode = response.headers().get("Authorization").getValue();
        return authorizationCode;
    }

    @Test
    public void testFindUserByIdSuccessfully() {

        String code = login();

        given().
                header("Authorization", code).
                pathParam("id", "1").
                contentType("application/JSON").
                when().
                get("http://localhost:8081/api/user/id/{id}").
                then().
                statusCode(200).
                assertThat().
                body("username", is("test")).assertThat().body("id", is(1));
    }

    @Test
    public void testFindUserByIdFailed() {

        String code = login();

        given().
                header("Authorization", code).
                pathParam("id", "100").
                contentType("application/JSON").
                when().
                get("http://localhost:8081/api/user/id/{id}").
                then().
                statusCode(404);
    }

    @Test
    public void testFindUserByUserNameSuccessfully() {

        String code = login();

        given().
                header("Authorization", code).
                pathParam("username", "test").
                contentType("application/JSON").
                when().
                get("http://localhost:8081/api/user/{username}").
                then().
                statusCode(200).
                assertThat().
                body("username", is("test")).assertThat().body("id", is(1));
    }

    @Test
    public void testFindUserByUserNameFailed() {

        String code = login();

        given().
                header("Authorization", code).
                pathParam("username", "test1").
                contentType("application/JSON").
                when().
                get("http://localhost:8081/api/user/{username}").
                then().
                statusCode(404);
    }
}
