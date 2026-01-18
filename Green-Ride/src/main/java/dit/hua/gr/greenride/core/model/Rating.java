package dit.hua.gr.greenride.core.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "rating")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score;

    @ManyToOne
    @JoinColumn(name = "rater_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person rater;

    @ManyToOne
    @JoinColumn(name = "rated_person_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person ratedPerson;

    // Getters/Setters (Αν δεν χρησιμοποιείς Lombok)
    public Person getRater() { return rater; }
    public void setRater(Person rater) { this.rater = rater; }
    public Person getRatedPerson() { return ratedPerson; }
    public void setRatedPerson(Person ratedPerson) { this.ratedPerson = ratedPerson; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}