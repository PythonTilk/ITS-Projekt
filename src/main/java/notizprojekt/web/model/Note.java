package notizprojekt.web.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "notiz")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "N_id")
    private Integer id;

    @Column(name = "Titel", nullable = false)
    private String title;

    @Column(name = "Tag", nullable = false)
    private String tag = "";

    @Column(name = "Inhalt", columnDefinition = "MEDIUMTEXT")
    private String content = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "B_id")
    @JsonBackReference
    private User user;
    
    // Position on the board
    @Column(name = "position_x")
    private Integer positionX = 0;
    
    @Column(name = "position_y")
    private Integer positionY = 0;
    
    // Color of the note
    @Column(name = "color")
    private String color = "#FFFF88"; // Default yellow color
    
    // Note type (text or code)
    @Enumerated(EnumType.STRING)
    @Column(name = "note_type")
    private NoteType noteType = NoteType.text;
    
    // Privacy level
    @Enumerated(EnumType.STRING)
    @Column(name = "privacy_level")
    private PrivacyLevel privacyLevel = PrivacyLevel.private_;
    
    // Users this note is shared with (comma-separated usernames)
    @Column(name = "shared_with", columnDefinition = "TEXT")
    private String sharedWith;
    
    // Image support
    @Column(name = "has_images")
    private Boolean hasImages = false;
    
    @Column(name = "image_paths", columnDefinition = "TEXT")
    private String imagePaths;
    
    public enum NoteType {
        text, code
    }
    
    public enum PrivacyLevel {
        private_("private"), some_people("some_people"), everyone("everyone");
        
        private final String value;
        
        PrivacyLevel(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
}