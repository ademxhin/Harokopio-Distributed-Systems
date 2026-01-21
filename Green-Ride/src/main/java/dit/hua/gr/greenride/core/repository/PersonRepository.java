package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.PersonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByEmailAddress(String emailAddress);
    boolean existsByEmailAddress(String emailAddress);
    boolean existsByMobilePhoneNumber(String mobilePhoneNumber);
    boolean existsByUserId(String userId);

    List<Person> findByFirstNameContainingIgnoreCaseAndPersonType(String firstName, PersonType personType);
    List<Person> findAllByPersonType(PersonType personType);

    long countByPersonType(PersonType personType);

    interface PersonRatingStats {
        Long getId();
        String getFirstName();
        String getLastName();
        Double getAverageRating();
        Long getRatingsCount();
        PersonType getPersonType();
    }

    @Query("""
        select 
            p.id as id,
            p.firstName as firstName,
            p.lastName as lastName,
            p.personType as personType,
            avg(r.score) as averageRating,
            count(r.id) as ratingsCount
        from Person p
        left join Rating r on r.ratedPerson.id = p.id
        where p.personType = :personType
        group by p.id, p.firstName, p.lastName, p.personType
        order by p.firstName asc, p.lastName asc
    """)
    List<PersonRatingStats> findAllWithRatingStatsByPersonType(@Param("personType") PersonType personType);

    @Query("""
        select 
            p.id as id,
            p.firstName as firstName,
            p.lastName as lastName,
            p.personType as personType,
            avg(r.score) as averageRating,
            count(r.id) as ratingsCount
        from Person p
        left join Rating r on r.ratedPerson.id = p.id
        where p.personType = :personType
          and ( lower(p.firstName) like lower(concat('%', :q, '%'))
             or lower(p.lastName)  like lower(concat('%', :q, '%')) )
        group by p.id, p.firstName, p.lastName, p.personType
        order by p.firstName asc, p.lastName asc
    """)
    List<PersonRatingStats> searchWithRatingStatsByPersonType(@Param("q") String q,
                                                              @Param("personType") PersonType personType);
}