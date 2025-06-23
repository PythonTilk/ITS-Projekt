package notizprojekt.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import notizprojekt.web.model.Note;
import notizprojekt.web.model.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {
    private Integer id;
    private String title;
    private String tag;
    private String content;
    private Integer positionX;
    private Integer positionY;
    private String color;
    private Note.NoteType noteType;
    private String privacyLevel;
    private String sharedWith;
    private Boolean hasImages;
    private String imagePaths;
    
    // Author information
    private Integer authorId;
    private String authorUsername;
    private String authorDisplayName;
    private String authorProfilePicture;
    
    public static NoteDTO fromNote(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setTag(note.getTag());
        dto.setContent(note.getContent());
        dto.setPositionX(note.getPositionX());
        dto.setPositionY(note.getPositionY());
        dto.setColor(note.getColor());
        dto.setNoteType(note.getNoteType());
        dto.setPrivacyLevel(note.getPrivacyLevel() != null ? note.getPrivacyLevel().getValue() : "private");
        dto.setSharedWith(note.getSharedWith());
        dto.setHasImages(note.getHasImages());
        dto.setImagePaths(note.getImagePaths());
        
        // Set author information
        if (note.getUser() != null) {
            User author = note.getUser();
            dto.setAuthorId(author.getId());
            dto.setAuthorUsername(author.getUsername());
            dto.setAuthorDisplayName(author.getDisplayName() != null && !author.getDisplayName().trim().isEmpty() 
                ? author.getDisplayName() : author.getUsername());
            dto.setAuthorProfilePicture(author.getProfilePicture());
        }
        
        return dto;
    }
}