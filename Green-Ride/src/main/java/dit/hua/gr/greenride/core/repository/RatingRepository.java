package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByRaterAndRatedPerson(dit.hua.gr.greenride.core.model.Person rater,
                                        dit.hua.gr.greenride.core.model.Person ratedPerson);

    @Query("select r.ratedPerson.id from Rating r where r.rater.id = :raterId")
    List<Long> findRatedPersonIdsByRaterId(@Param("raterId") Long raterId);
}