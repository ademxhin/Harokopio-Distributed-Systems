package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Rating;
import dit.hua.gr.greenride.core.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByRaterAndRatedPerson(Person rater, Person ratedPerson);
    @Query("""
        SELECT COALESCE(AVG(r.score), 0)
        FROM Rating r
        WHERE r.ratedPerson.id = :personId
    """)
    Double getAverageRatingForPerson(@Param("personId") Long personId);
}