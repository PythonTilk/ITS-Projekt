package notizprojekt.web.controller.api;

import lombok.RequiredArgsConstructor;
import notizprojekt.web.model.Note;
import notizprojekt.web.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<?> getAllNotes(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        List<Note> notes = noteService.getAllNotesByUser(userId);
        return ResponseEntity.ok(notes);
    }
    
    @PostMapping
    public ResponseEntity<?> createNote(@RequestBody Map<String, Object> noteData, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(401).body(response);
        }
        
        try {
            Note note = noteService.createEnhancedNote(userId, noteData);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("note", note);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{noteId}")
    public ResponseEntity<?> updateNote(
            @PathVariable Integer noteId,
            @RequestBody Map<String, Object> noteData,
            HttpSession session) {
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(401).body(response);
        }
        
        try {
            Note note = noteService.updateEnhancedNote(noteId, noteData);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("note", note);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{noteId}/position")
    public ResponseEntity<?> updateNotePosition(
            @PathVariable Integer noteId,
            @RequestBody Map<String, Integer> positionData,
            HttpSession session) {
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        Note note = noteService.updateNotePosition(noteId, 
            positionData.get("positionX"), positionData.get("positionY"));
        return ResponseEntity.ok(note);
    }
    
    @DeleteMapping("/{noteId}")
    public ResponseEntity<?> deleteNote(
            @PathVariable Integer noteId,
            HttpSession session) {
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        noteService.deleteNote(noteId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchNotes(
            @RequestParam String searchTerm,
            HttpSession session) {
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        List<Note> notes = noteService.searchNotes(userId, searchTerm);
        return ResponseEntity.ok(notes);
    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
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
            String filePath = noteService.saveUploadedFile(file, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("filePath", filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}