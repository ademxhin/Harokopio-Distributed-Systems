package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRatedPerson(Person person);
}