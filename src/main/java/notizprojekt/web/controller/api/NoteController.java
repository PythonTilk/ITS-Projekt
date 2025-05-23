package notizprojekt.web.controller.api;

import lombok.RequiredArgsConstructor;
import notizprojekt.web.model.Note;
import notizprojekt.web.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> createNote(
            @RequestParam String title,
            @RequestParam(required = false) String tag,
            @RequestParam String content,
            @RequestParam(required = false, defaultValue = "0") Integer positionX,
            @RequestParam(required = false, defaultValue = "0") Integer positionY,
            @RequestParam(required = false, defaultValue = "#FFFF88") String color,
            HttpSession session) {
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        Note note = noteService.createNote(userId, title, tag, content, positionX, positionY, color);
        return ResponseEntity.ok(note);
    }
    
    @PutMapping("/{noteId}")
    public ResponseEntity<?> updateNote(
            @PathVariable Integer noteId,
            @RequestParam String title,
            @RequestParam(required = false) String tag,
            @RequestParam String content,
            HttpSession session) {
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        Note note = noteService.updateNote(noteId, title, tag, content);
        return ResponseEntity.ok(note);
    }
    
    @PutMapping("/{noteId}/position")
    public ResponseEntity<?> updateNotePosition(
            @PathVariable Integer noteId,
            @RequestParam Integer positionX,
            @RequestParam Integer positionY,
            HttpSession session) {
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        Note note = noteService.updateNotePosition(noteId, positionX, positionY);
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
}