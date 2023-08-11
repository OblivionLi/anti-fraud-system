package antifraud.presentation.user;

import antifraud.businesslayer.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    UserDetailServiceImpl userService;

    @PostMapping("/user")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRegisterRequestDTO requestDTO) {
        return userService.saveUser(requestDTO);
    }

    @PutMapping("/role")
    public ResponseEntity<UserResponseDTO> updateUserRole(@RequestBody UserUpdateRoleRequestDTO requestDTO) {
        return userService.updateUserRole(requestDTO);
    }

    @PutMapping("/access")
    public ResponseEntity<UserToggleAccessResponseDTO> toggleUserAccess(@RequestBody UserToggleAccessRequestDTO requestDTO) {
        return userService.toggleUserAccess(requestDTO);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> listUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<Object> deleteUser(@PathVariable String username) {
        return userService.deleteUser(username);
    }
}
