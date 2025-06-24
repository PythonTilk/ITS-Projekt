package notizprojekt.web.controller.api;

import lombok.RequiredArgsConstructor;
import notizprojekt.web.model.User;
import notizprojekt.web.service.UserService;
import notizprojekt.web.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final NoteService noteService;

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
                        userData.put("displayName", userService.getDisplayName(user));
                        userData.put("email", null); // Email not available in current schema
                        userData.put("avatar", user.getProfilePicture() != null ? user.getProfilePicture() : user.getUsername().substring(0, 1).toUpperCase());
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

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Integer userId, HttpSession session) {
        Integer currentUserId = (Integer) session.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> profile = new HashMap<>();
                profile.put("id", user.getId());
                profile.put("username", user.getUsername());
                profile.put("displayName", userService.getDisplayName(user));
                profile.put("biography", user.getBiography());
                profile.put("profilePicture", user.getProfilePicture());
                profile.put("isOwnProfile", user.getId().equals(currentUserId));
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("profile", profile);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error fetching profile: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> profileData, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        try {
            String displayName = profileData.get("displayName");
            String biography = profileData.get("biography");
            String profilePicture = profileData.get("profilePicture");

            User updatedUser = userService.updateProfile(userId, displayName, biography, profilePicture);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profile updated successfully");
            response.put("user", Map.of(
                "id", updatedUser.getId(),
                "username", updatedUser.getUsername(),
                "displayName", userService.getDisplayName(updatedUser),
                "biography", updatedUser.getBiography(),
                "profilePicture", updatedUser.getProfilePicture()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating profile: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("username", user.getUsername());
                userData.put("displayName", userService.getDisplayName(user));
                userData.put("biography", user.getBiography());
                userData.put("profilePicture", user.getProfilePicture());
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("user", userData);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error fetching current user: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/profile/upload-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(401).body(response);
        }
        
        try {
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Only image files are allowed for profile pictures");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Save the file using the existing file upload service
            String filePath = noteService.saveUploadedFile(file, userId);
            
            // Update user's profile picture
            userService.updateProfile(userId, null, null, filePath);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("profilePicture", filePath);
            response.put("message", "Profile picture updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error uploading profile picture: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/profile/{userId}/public-notes")
    public ResponseEntity<?> getUserPublicNotes(@PathVariable Integer userId, HttpSession session) {
        Integer currentUserId = (Integer) session.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        try {
            // Get public notes for the specified user
            var publicNotes = noteService.getPublicNotesByUserId(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notes", publicNotes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error fetching public notes: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}