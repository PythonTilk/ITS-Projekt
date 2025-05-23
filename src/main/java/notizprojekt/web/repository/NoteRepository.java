package notizprojekt.web.repository;

import notizprojekt.web.model.Note;
import notizprojekt.web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {
    List<Note> findByUser(User user);
    List<Note> findByUserAndTitleContainingOrUserAndTagContainingOrUserAndContentContaining(
            User user1, String title, User user2, String tag, User user3, String content);
}