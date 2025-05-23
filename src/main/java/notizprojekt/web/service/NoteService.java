package notizprojekt.web.service;

import lombok.RequiredArgsConstructor;
import notizprojekt.web.model.Note;
import notizprojekt.web.model.User;
import notizprojekt.web.repository.NoteRepository;
import notizprojekt.web.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public List<Note> getAllNotesByUser(Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return noteRepository.findByUser(userOpt.get());
        }
        throw new RuntimeException("User not found");
    }
    
    public Note createNote(Integer userId, String title, String tag, String content, 
                          Integer positionX, Integer positionY, String color) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            Note note = new Note();
            note.setTitle(title);
            note.setTag(tag);
            note.setContent(content);
            note.setUser(user);
            note.setPositionX(positionX);
            note.setPositionY(positionY);
            note.setColor(color);
            
            return noteRepository.save(note);
        }
        throw new RuntimeException("User not found");
    }
    
    public Note updateNote(Integer noteId, String title, String tag, String content) {
        Optional<Note> noteOpt = noteRepository.findById(noteId);
        if (noteOpt.isPresent()) {
            Note note = noteOpt.get();
            note.setTitle(title);
            note.setTag(tag);
            note.setContent(content);
            
            return noteRepository.save(note);
        }
        throw new RuntimeException("Note not found");
    }
    
    public Note updateNotePosition(Integer noteId, Integer positionX, Integer positionY) {
        Optional<Note> noteOpt = noteRepository.findById(noteId);
        if (noteOpt.isPresent()) {
            Note note = noteOpt.get();
            note.setPositionX(positionX);
            note.setPositionY(positionY);
            
            return noteRepository.save(note);
        }
        throw new RuntimeException("Note not found");
    }
    
    public void deleteNote(Integer noteId) {
        noteRepository.deleteById(noteId);
    }
    
    public List<Note> searchNotes(Integer userId, String searchTerm) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return noteRepository.findByUserAndTitleContainingOrUserAndTagContainingOrUserAndContentContaining(
                    user, searchTerm, user, searchTerm, user, searchTerm);
        }
        throw new RuntimeException("User not found");
    }
}