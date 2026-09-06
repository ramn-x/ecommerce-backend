package com.ecommerce.ecommerce_backend.Service;

import com.ecommerce.ecommerce_backend.Exception.AccessDeniedException;
import com.ecommerce.ecommerce_backend.Exception.CartNotFoundException;
import com.ecommerce.ecommerce_backend.Exception.UserNotFoundException;
import com.ecommerce.ecommerce_backend.Repository.CartRepository;
import com.ecommerce.ecommerce_backend.DTO.CartDTO;
import com.ecommerce.ecommerce_backend.Entity.Cart;
import com.ecommerce.ecommerce_backend.Entity.Product;
import com.ecommerce.ecommerce_backend.Entity.User;
import com.ecommerce.ecommerce_backend.Mapper.CartMapper;
import com.ecommerce.ecommerce_backend.Repository.ProductRepository;
import com.ecommerce.ecommerce_backend.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartRepository cartRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public CartDTO addToCart(
            Integer productId,
            Integer quantity,
            String currentUserEmail) {

            if (quantity == null || quantity < 1) {
                throw new IllegalArgumentException(
                        "Quantity must be at least 1");
            }

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email: "
                                        + currentUserEmail));

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found with id: " + productId));

        Cart cartItem = cartRepository
                .findByUserIdAndProductId(
                        user.getId(),
                        productId )
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(
                    cartItem.getQuantity() + quantity );
        } else {
            cartItem = new Cart();

            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }
        Cart savedCart = cartRepository.save(cartItem);
        return CartMapper.toDTO(savedCart);
    }

    //

    public List<CartDTO> getMyCart(String ccurrentUserEmail){

        User user= userRepository.findByEmail(ccurrentUserEmail)
                .orElseThrow(()->
                        new UserNotFoundException(
                                "user not found with emails"
                                        + ccurrentUserEmail));
        List<Cart> cartItem =
                cartRepository
                        .findByUserId(user.getId());
        return  cartItem
                .stream()
                .map(CartMapper::toDTO)
                .toList();

    }

    //

    public CartDTO updateQuantity(
            Integer cartId,
            Integer quantity,
            String currentUserEmail) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cart item not found with id: " + cartId));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email: "
                                        + currentUserEmail));
        System.out.println("CURRENT USER ID: " + currentUser.getId());
        System.out.println("CURRENT USER EMAIL: " + currentUser.getEmail());
        System.out.println("CURRENT USER ROLE: " + currentUser.getRole());
        System.out.println("CART USER ID: " + cart.getUser().getId());

        if (!cart.getUser().getId().equals(currentUser.getId())
                && !currentUser.getRole().equals("ADMIN")) {

            throw new AccessDeniedException("Access denied");
        }

        cart.setQuantity(quantity);

        Cart updatedCart = cartRepository.save(cart);

        return CartMapper.toDTO(updatedCart);
    }
    public void deleteCartItem(
            Integer cartId,
            String currentUserEmail) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cart item not found with id: " + cartId));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email: "
                                        + currentUserEmail));

        if (!cart.getUser().getId().equals(currentUser.getId())
                && !currentUser.getRole().equals("ADMIN")) {

            throw new AccessDeniedException("Access denied");
        }

        cartRepository.delete(cart);
    }
    public CartDTO getCartItem(
            Integer cartId,
            String currentUserEmail) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() ->
                        new CartNotFoundException (
                                "Cart item not found with id: " + cartId));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email: "
                                        + currentUserEmail));

        if (!cart.getUser().getId().equals(currentUser.getId())
                && !currentUser.getRole().equals("ADMIN")) {

            throw new AccessDeniedException("Access denied");
        }

        return CartMapper.toDTO(cart);
    }
}