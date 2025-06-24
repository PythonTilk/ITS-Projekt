package notizprojekt.web.service;

import lombok.RequiredArgsConstructor;
import notizprojekt.web.model.User;
import notizprojekt.web.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerNewUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setBId(0); // Default value for b_id
        
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public boolean checkCredentials(String username, String password) {
        System.out.println("Checking credentials for username: " + username);
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("Found user: " + user.getUsername());
            System.out.println("Stored password: " + user.getPassword());
            System.out.println("Input password: " + password);
            
            // Check if password is BCrypt encoded (starts with $2a$, $2b$, or $2y$)
            if (user.getPassword().startsWith("$2")) {
                System.out.println("Using BCrypt password matching");
                boolean matches = passwordEncoder.matches(password, user.getPassword());
                System.out.println("BCrypt match result: " + matches);
                return matches;
            } else {
                // For backward compatibility with plain text passwords
                System.out.println("Using plain text password matching");
                boolean matches = password.equals(user.getPassword());
                System.out.println("Plain text match result: " + matches);
                return matches;
            }
        } else {
            System.out.println("User not found: " + username);
        }
        return false;
    }
    
    public List<User> searchUsers(String searchTerm, Integer excludeUserId) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return userRepository.findAll().stream()
                    .filter(user -> !user.getId().equals(excludeUserId))
                    .limit(10)
                    .collect(Collectors.toList());
        }
        
        return userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(excludeUserId))
                .filter(user -> user.getUsername().toLowerCase().contains(searchTerm.toLowerCase()))
                .limit(10)
                .collect(Collectors.toList());
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }
    
    public User updateProfile(Integer userId, String displayName, String biography, String profilePicture) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setDisplayName(displayName);
            user.setBiography(biography);
            if (profilePicture != null && !profilePicture.trim().isEmpty()) {
                user.setProfilePicture(profilePicture);
            }
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }
    
    public String getDisplayName(User user) {
        if (user.getDisplayName() != null && !user.getDisplayName().trim().isEmpty()) {
            return user.getDisplayName();
        }
        return user.getUsername();
    }
}