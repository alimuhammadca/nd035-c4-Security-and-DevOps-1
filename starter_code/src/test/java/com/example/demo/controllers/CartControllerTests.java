package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTests {
    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void testAddToCart() {
        ModifyCartRequest cartRequest = createCartRequest();
        final ResponseEntity<Cart> response = cartController.addToCart(cartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(10, cart.getItems().size());
    }

    @Test
    public void testRemoveFromCart() {
        ModifyCartRequest cartRequest = createCartRequest();
        final ResponseEntity<Cart> response = cartController.removeFromCart(cartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();

        assertNotNull(cart);
        assertEquals(0, cart.getItems().size());
    }

    public ModifyCartRequest createCartRequest() {
        Cart cart = new Cart();
        User user = createUser(1l, "test", "testPassword", cart);
        Item item = createItem(1L, "Round Widget", new BigDecimal("2.99"), "A widget that is round");
        ModifyCartRequest newCartRequest = createCartRequest(1L, 10, "test");
        ArrayList<Item> listOfItems = new ArrayList<Item>();
        listOfItems.add(item);
        createCart(1l, listOfItems, user);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        return newCartRequest;
    }

    public ModifyCartRequest createCartRequest(long itemId, int quantity, String username) {
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(itemId);
        cartRequest.setQuantity(quantity);
        cartRequest.setUsername(username);
        return cartRequest;
    }

    public User createUser(long userId, String username, String password, Cart cart) {
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setPassword(password);
        user.setCart(cart);
        return user;
    }

    public Item createItem(Long id, String name, BigDecimal price, String description) {
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
        return cart;
    }

}
