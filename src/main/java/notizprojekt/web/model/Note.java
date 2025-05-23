package notizprojekt.web.model;

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

    @Column(name = "Inhalt", nullable = false, length = 2000)
    private String content = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "B_id")
    private User user;
    
    // Position on the board
    @Column(name = "position_x")
    private Integer positionX = 0;
    
    @Column(name = "position_y")
    private Integer positionY = 0;
    
    // Color of the note
    @Column(name = "color")
    private String color = "#FFFF88"; // Default yellow color
}