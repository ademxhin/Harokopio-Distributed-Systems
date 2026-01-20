package dit.hua.gr.greenride.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "person")
@Getter
@Setter
@NoArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String userId;
    private String firstName;
    private String lastName;
    private String mobilePhoneNumber;
    private String emailAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "person_type", nullable = false)
    private PersonType personType;

    private String hashedPassword;

    @Column(nullable = false)
    private int reportCount = 0;

    @Column(nullable = false)
    private boolean banned = false;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ride> rides = new ArrayList<>();

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "ratedPerson", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "rater", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratingsGiven = new ArrayList<>();

    public Person(String userId,
                  String firstName,
                  String lastName,
                  String mobilePhoneNumber,
                  String emailAddress,
                  PersonType personType,
                  String hashedPassword) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.emailAddress = emailAddress;
        this.personType = (personType != null) ? personType : PersonType.PASSENGER;
        this.hashedPassword = hashedPassword;
    }

    public boolean isAdmin() { return this.personType == PersonType.ADMIN; }
    public boolean isDriver() { return this.personType == PersonType.DRIVER; }
    public boolean isPassenger() { return this.personType == PersonType.PASSENGER; }

    public String getFullName() { return firstName + " " + lastName; }

    public Double getAverageRating() {
        if (ratings == null || ratings.isEmpty()) return null;
        return ratings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0);
    }
}