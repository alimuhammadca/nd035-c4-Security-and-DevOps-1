package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTests {
    private OrderController orderController;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
        TestUtils.injectObject(orderController, "userRepository", userRepository);
    }

    @Test
    public void testOrderSubmit() {
        Item item = createItem(1L, "Round Widget", "A widget that is round", new BigDecimal("2.99"));
        ArrayList<Item> items = new ArrayList<>();
        items.add(item);
        Cart cart = createCart(1L, items, null);
        User user = createUser(1L, "test", "testPassword", cart);
        cart.setUser(user);
        when(userRepository.findByUsername("test")).thenReturn(user);
        final ResponseEntity<UserOrder> response = orderController.submit("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder order = response.getBody();
        assertEquals(user, order.getUser());
        assertEquals(items, order.getItems());
        assertEquals(BigDecimal.valueOf(2.99), order.getTotal());
    }

    @Test
    public void testGetOrdersForUser() {
        Item item = createItem(1L, "Round Widget", "A widget that is round", new BigDecimal("2.99"));
        ArrayList<Item> items = new ArrayList<>();
        items.add(item);
        Cart cart = createCart(1L, new ArrayList<>(), null);
        User user = createUser(1L, "test", "testPassword", cart);
        cart.setUser(user);
        cart.setItems(items);
        user.setCart(cart);
        orderController.submit("test");
        when(userRepository.findByUsername("test")).thenReturn(user);
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
    }

    @Test
    public void testSubmitFail() {
        when(userRepository.findByUsername("test")).thenReturn(null);
        final ResponseEntity<UserOrder> response = orderController.submit("test");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetOrdersForUserFail() {
        when(userRepository.findByUsername("test")).thenReturn(null);
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    public User createUser(long userId, String username, String password, Cart cart) {
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setPassword(password);
        user.setCart(cart);
        return user;
    }

    public Item createItem(Long id, String name, String description, BigDecimal price) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setPrice(price);
        item.setDescription(description);
        return item;
    }

    public Cart createCart(long cartId, ArrayList<Item> items, User user) {
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setItems(items);
        cart.setUser(user);
        cart.setTotal(BigDecimal.valueOf(2.99));
        return cart;
    }
}
