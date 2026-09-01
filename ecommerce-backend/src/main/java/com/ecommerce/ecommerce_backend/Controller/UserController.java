package com.ecommerce.ecommerce_backend.Controller;

import com.ecommerce.ecommerce_backend.DTO.LoginRequestDTO;
import com.ecommerce.ecommerce_backend.DTO.LoginResponseDTO;
import com.ecommerce.ecommerce_backend.DTO.UserDTO;
import com.ecommerce.ecommerce_backend.DTO.UserRequestDTO;
import com.ecommerce.ecommerce_backend.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> addUser(@Valid @RequestBody UserRequestDTO request) {

        UserDTO savedUser = userService.addUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedUser);
    }

    @GetMapping
    public Page<UserDTO> getAllUsers(Pageable pageable){
        return userService.getAllUsers(pageable);
    }


    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Integer id){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
                String currentUserEmail = authentication.getName();

        return userService.getUserById(id , currentUserEmail);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void >deleteUser(@PathVariable Integer id){

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String currentUserEmail =
                authentication.getName();

        userService.deleteById(id,currentUserEmail  );
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UserRequestDTO request){
        Authentication authentication =SecurityContextHolder
                .getContext().getAuthentication();
        String currentUserEmail =authentication.getName();
        UserDTO updatedUser = userService.updateUser(id, request, currentUserEmail);
        return ResponseEntity.ok(updatedUser);
    }
    @GetMapping("/search")
    public Page<UserDTO> searchUsersByName(
            @RequestParam String name , Pageable pageable ) {

        return userService.searchUsersByName(name ,pageable);
    }


    @GetMapping("/email")
    public UserDTO getUserByEmail (@RequestParam String email){
        return userService.getUserByEmail(email);
    }


    @PostMapping("/login")
    public LoginResponseDTO login(@Valid
                      @RequestBody LoginRequestDTO request){
        return userService.login(request);
    }
}
