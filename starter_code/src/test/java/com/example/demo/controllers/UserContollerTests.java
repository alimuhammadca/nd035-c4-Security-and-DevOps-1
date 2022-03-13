package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserContollerTests {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void createUserSuccessful() throws Exception {
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("ThisIsHashed");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User user = responseEntity.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("ThisIsHashed", user.getPassword());

    }

    @Test
    public void createUserFailed() {
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("ThisIsHashed");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testtest");

        final ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        assertNotNull(responseEntity);
        assertEquals(400, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testFindUserById() throws Exception {
        final ResponseEntity<User> responseEntity = createTestUser();
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User user = responseEntity.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("ThisIsHashed", user.getPassword());


        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        final ResponseEntity<User> response = userController.findById(user.getId());
        User userFound = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, userFound.getId());
        assertEquals("test", userFound.getUsername());
        assertEquals("ThisIsHashed", userFound.getPassword());
    }

    @Test
    public void testFindUserByUserName() throws Exception {
        final ResponseEntity<User> responseEntity = createTestUser();
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User user = responseEntity.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("ThisIsHashed", user.getPassword());


        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        final ResponseEntity<User> response = userController.findByUserName(user.getUsername());
        User userFound = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, userFound.getId());
        assertEquals("test", userFound.getUsername());
        assertEquals("ThisIsHashed", userFound.getPassword());
    }

    public ResponseEntity<User> createTestUser() {
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("ThisIsHashed");
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("test");
        userRequest.setPassword("testPassword");
        userRequest.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(userRequest);
        return response;
    }

    @Test
    public void testFindByIdFailed() {
        final ResponseEntity<User> response = userController.findById(0l);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testFindByUserNameFailed() {
        final ResponseEntity<User> response = userController.findByUserName("something");
        assertEquals(404, response.getStatusCodeValue());
    }
}
