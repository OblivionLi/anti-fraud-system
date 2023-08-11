package antifraud.businesslayer.user;

import antifraud.businesslayer.enums.UserAccess;
import antifraud.businesslayer.enums.UserRole;
import antifraud.persistence.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new UserDetailsImpl(user);
    }

    public ResponseEntity<UserResponseDTO> saveUser(UserRegisterRequestDTO request) {
        if (request == null
                || request.getName() == null
                || request.getUsername() == null
                || request.getPassword() == null
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User existingUser = userRepository.findByUsernameIgnoreCase(request.getUsername());
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        UserRole userRole = userRepository.count() < 1 ? UserRole.ADMINISTRATOR : UserRole.MERCHANT;
        boolean isUserLocked = !userRole.equals(UserRole.ADMINISTRATOR);

        User newUser = new User(request.getName(), request.getUsername(), encryptedPassword, userRole.name(), isUserLocked);
        newUser = userRepository.save(newUser);

        UserResponseDTO response = new UserResponseDTO(newUser.getId(), newUser.getName(), newUser.getUsername(), newUser.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<UserResponseDTO> updateUserRole(UserUpdateRoleRequestDTO request) {
        if (request == null
                || request.getUsername() == null
                || request.getRole() == null
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User existingUser = userRepository.findByUsernameIgnoreCase(request.getUsername());
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (!request.getRole().equals(UserRole.SUPPORT.name()) && !request.getRole().equals(UserRole.MERCHANT.name())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (existingUser.getRole().equals(request.getRole())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        existingUser.setRole(request.getRole());
        userRepository.save(existingUser);

        UserResponseDTO response = new UserResponseDTO(existingUser.getId(), existingUser.getName(), existingUser.getUsername(), request.getRole());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<UserToggleAccessResponseDTO> toggleUserAccess(UserToggleAccessRequestDTO request) {
        if (request == null
                || request.getUsername() == null
                || request.getOperation() == null
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!request.getOperation().equalsIgnoreCase(UserAccess.LOCK.name())
                && !request.getOperation().equalsIgnoreCase(UserAccess.UNLOCK.name())
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User existingUser = userRepository.findByUsernameIgnoreCase(request.getUsername());
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (existingUser.getRole().equals(UserRole.ADMINISTRATOR.name())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        boolean locked = request.getOperation().equalsIgnoreCase(UserAccess.LOCK.name());
        existingUser.setLocked(locked);
        userRepository.save(existingUser);

        String operation = request.getOperation().equals(UserAccess.UNLOCK.name()) ? "unlocked" : "locked";
        String responseStatus = "User " + request.getUsername() + " " + operation + "!";
        UserToggleAccessResponseDTO response = new UserToggleAccessResponseDTO(responseStatus);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<List<User>> getAllUsers() {
        List<User> usersList = userRepository.findAll();
        Collections.sort(usersList); // Sort the list by ID in ascending order
        return ResponseEntity.status(HttpStatus.OK).body(usersList);
    }

    public ResponseEntity<Object> deleteUser(String username) {
        User existingUser = userRepository.findByUsernameIgnoreCase(username);
        if (existingUser == null) {
            Map<String, String> response = new HashMap<>();
            response.put("username", username);
            response.put("status", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        userRepository.delete(existingUser);

        Map<String, String> response = new HashMap<>();
        response.put("username", existingUser.getUsername());
        response.put("status", "Deleted successfully!");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
