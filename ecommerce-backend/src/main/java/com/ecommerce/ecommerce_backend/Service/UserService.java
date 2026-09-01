package com.ecommerce.ecommerce_backend.Service;

import com.ecommerce.ecommerce_backend.DTO.LoginRequestDTO;
import com.ecommerce.ecommerce_backend.DTO.LoginResponseDTO;
import com.ecommerce.ecommerce_backend.DTO.UserDTO;
import com.ecommerce.ecommerce_backend.DTO.UserRequestDTO;
import com.ecommerce.ecommerce_backend.Entity.User;
import com.ecommerce.ecommerce_backend.Exception.AccessDeniedException;
import com.ecommerce.ecommerce_backend.Exception.EmailAlreadyExistsException;
import com.ecommerce.ecommerce_backend.Exception.UserNotFoundException;
import com.ecommerce.ecommerce_backend.Mapper.UserMapper;
import com.ecommerce.ecommerce_backend.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Create User
    public UserDTO addUser(UserRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = UserMapper.toEntity(request);
        if (user.getRole()== null ||user.getRole().isBlank()){
            user.setRole("USER");
        }
        user.setPassword(passwordEncoder
                .encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        return UserMapper.toDTO(savedUser);
    }

    // Get All Users
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toDTO);
    }

    // Get User By ID
    public UserDTO getUserById(Integer id, String currentUserEmail) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + id));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Current user not found with email: "
                                        + currentUserEmail));

        if (!user.getEmail().equals(currentUserEmail)
                && !currentUser.getRole().equals("ADMIN")) {

            throw new AccessDeniedException("Access denied");
        }

        return UserMapper.toDTO(user);
    }

    // Delete User
    public void deleteById(
            Integer id,
            String currentUserEmail) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + id));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Current user not found with email: "
                                        + currentUserEmail));

        if (!user.getEmail().equals(currentUserEmail)
                && !currentUser.getRole().equals("ADMIN")) {

            throw new AccessDeniedException("Access denied");
        }

        userRepository.delete(user);

     }
    // Update User
    public UserDTO updateUser(
            Integer id,
            UserRequestDTO request,
            String currentUserEmail) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + id));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Current user not found with email: "
                                        + currentUserEmail));

        if (!existingUser.getEmail().equals(currentUserEmail)
                && !currentUser.getRole().equals("ADMIN")) {

            throw new AccessDeniedException("Access denied");
        }

        if (userRepository.existsByEmailAndIdNot(
                request.getEmail(), id)) {

            throw new EmailAlreadyExistsException(
                    "Email already exists");
        }

        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPassword(request.getPassword());
        existingUser.setPhone(request.getPhone());

        User updatedUser = userRepository.save(existingUser);

        return UserMapper.toDTO(updatedUser);
    }

    // search by name
    public Page<UserDTO> searchUsersByName(
            String name,
            Pageable pageable) {

        Page<User> users =
                userRepository.findByNameContainingIgnoreCase(name, pageable);

        return users.map(UserMapper::toDTO);
    }

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email:" + email));
        return UserMapper.toDTO(user);
    }

    //    Login
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        String token = jwtService.generateToken(user.getEmail());
        return new LoginResponseDTO(token);

    }
}