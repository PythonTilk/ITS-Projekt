package notizprojekt.web.controller.api;

import lombok.RequiredArgsConstructor;
import notizprojekt.web.model.User;
import notizprojekt.web.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam(required = false) String q, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        try {
            List<User> users = userService.searchUsers(q, userId);
            
            // Convert to simplified user data for frontend
            List<Map<String, Object>> userList = users.stream()
                    .map(user -> {
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("id", user.getId());
                        userData.put("username", user.getUsername());
                        userData.put("email", null); // Email not available in current schema
                        userData.put("avatar", user.getUsername().substring(0, 1).toUpperCase());
                        return userData;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", userList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error searching users: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}