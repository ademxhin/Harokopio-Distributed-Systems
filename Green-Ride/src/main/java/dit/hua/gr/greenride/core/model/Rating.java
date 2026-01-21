package dit.hua.gr.greenride.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "rating")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int score;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rater_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person rater;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rated_person_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person ratedPerson;
}