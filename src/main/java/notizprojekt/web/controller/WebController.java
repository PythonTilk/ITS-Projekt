package notizprojekt.web.controller;

import lombok.RequiredArgsConstructor;
import notizprojekt.web.model.Note;
import notizprojekt.web.service.NoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final NoteService noteService;

    @GetMapping("/")
    public String home() {
        return "redirect:/board";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @GetMapping("/board")
    public String board(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            System.out.println("No userId in session, redirecting to login");
            return "redirect:/login";
        }
        
        System.out.println("Loading board for user ID: " + userId);
        
        try {
            List<Note> notes = noteService.getAllNotesByUser(userId);
            System.out.println("Found " + notes.size() + " notes for user ID: " + userId);
            
            // Add default positions and colors if needed
            for (Note note : notes) {
                if (note.getPositionX() == null) {
                    note.setPositionX(50);
                }
                if (note.getPositionY() == null) {
                    note.setPositionY(50);
                }
                if (note.getColor() == null) {
                    note.setColor("#FFFF88");
                }
            }
            
            model.addAttribute("notes", notes);
            model.addAttribute("username", session.getAttribute("username"));
            
            return "board";
        } catch (Exception e) {
            System.err.println("Error loading board: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to load notes: " + e.getMessage());
            model.addAttribute("username", session.getAttribute("username"));
            return "board";
        }
    }
    
    @GetMapping("/search")
    public String search(@RequestParam String searchTerm, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            List<Note> notes = noteService.searchNotes(userId, searchTerm);
            System.out.println("Found " + notes.size() + " notes matching search term: " + searchTerm);
            
            model.addAttribute("notes", notes);
            model.addAttribute("searchTerm", searchTerm);
            model.addAttribute("username", session.getAttribute("username"));
            
            return "board";
        } catch (Exception e) {
            System.err.println("Error searching notes: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to search notes: " + e.getMessage());
            model.addAttribute("username", session.getAttribute("username"));
            return "board";
        }
    }
}