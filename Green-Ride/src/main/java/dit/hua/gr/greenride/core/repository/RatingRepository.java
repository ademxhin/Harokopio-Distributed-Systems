package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Rating;
import dit.hua.gr.greenride.core.model.Person; // ✅ ΠΡΟΣΘΕΣΕ ΑΥΤΟ
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query("select avg(r.score) from Rating r where r.ratedPerson.id = :personId")
    Double findAverageScoreForPerson(@Param("personId") Long personId);

    boolean existsByRaterAndRatedPerson(Person rater, Person ratedPerson);
}