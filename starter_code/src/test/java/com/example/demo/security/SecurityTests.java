package com.example.demo.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testUnauthoizedAccess() throws Exception {
        String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJNdWhhbW1hZCIsImV4cCI6MTY0Nzk2NzA1N30.qr122edwWf0xsrKDYoQUon1yngdJkjeWEfy2P67siaWC25ELGu3Vbg0-XxjwJaHx0qprUKlt-nsVEk2diuPGNg";
        mvc.perform(MockMvcRequestBuilders.get("/api/user/test").header("Authorization", token)).andExpect(status().isNotFound());
    }

}