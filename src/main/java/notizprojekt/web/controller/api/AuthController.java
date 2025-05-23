package notizprojekt.web.controller.api;

import lombok.RequiredArgsConstructor;
import notizprojekt.web.model.User;
import notizprojekt.web.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, 
                                  @RequestParam String password,
                                  HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        if (userService.checkCredentials(username, password)) {
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                
                response.put("success", true);
                response.put("userId", user.getId());
                response.put("username", user.getUsername());
                return ResponseEntity.ok(response);
            }
        }
        
        response.put("success", false);
        response.put("message", "Invalid username or password");
        return ResponseEntity.badRequest().body(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username, 
                                     @RequestParam String password,
                                     @RequestParam String confirmPassword) {
        Map<String, Object> response = new HashMap<>();
        
        if (!password.equals(confirmPassword)) {
            response.put("success", false);
            response.put("message", "Passwords do not match");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            User user = userService.registerNewUser(username, password);
            response.put("success", true);
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}