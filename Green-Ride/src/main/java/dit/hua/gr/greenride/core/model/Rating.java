package dit.hua.gr.greenride.core.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rating")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score; // 1-5

    @ManyToOne
    @JoinColumn(name = "rated_person_id")
    private Person ratedPerson; // Ποιος βαθμολογείται
}