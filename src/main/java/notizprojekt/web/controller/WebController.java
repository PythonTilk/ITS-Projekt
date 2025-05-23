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
            return "redirect:/login";
        }
        
        List<Note> notes = noteService.getAllNotesByUser(userId);
        model.addAttribute("notes", notes);
        model.addAttribute("username", session.getAttribute("username"));
        
        return "board";
    }
    
    @GetMapping("/search")
    public String search(@RequestParam String searchTerm, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        List<Note> notes = noteService.searchNotes(userId, searchTerm);
        model.addAttribute("notes", notes);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("username", session.getAttribute("username"));
        
        return "board";
    }
}