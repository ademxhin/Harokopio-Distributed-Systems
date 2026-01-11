package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query("select avg(r.score) from Rating r where r.ratedPerson.id = :personId")
    Double findAverageScoreForPerson(@Param("personId") Long personId);

}
