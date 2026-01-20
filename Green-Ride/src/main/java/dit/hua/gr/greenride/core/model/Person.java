package dit.hua.gr.greenride.core.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "person")
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
    private int reportCount = 0;
    private boolean banned = false;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ride> rides = new ArrayList<>();

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "ratedPerson", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "rater", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratingsGiven = new ArrayList<>();

    public Person() {}

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
        this.personType = (personType != null) ? personType : PersonType.PASSENGER; // default
        this.hashedPassword = hashedPassword;
    }

    public Long getId() { return id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getMobilePhoneNumber() { return mobilePhoneNumber; }
    public void setMobilePhoneNumber(String mobilePhoneNumber) { this.mobilePhoneNumber = mobilePhoneNumber; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public PersonType getPersonType() { return personType; }
    public void setPersonType(PersonType personType) { this.personType = personType; }

    public String getHashedPassword() { return hashedPassword; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }

    public int getReportCount() { return reportCount; }
    public void setReportCount(int reportCount) { this.reportCount = reportCount; }

    public boolean isBanned() { return banned; }
    public void setBanned(boolean banned) { this.banned = banned; }

    public List<Ride> getRides() { return rides; }
    public List<Booking> getBookings() { return bookings; }

    public List<Rating> getRatings() { return ratings; }
    public List<Rating> getRatingsGiven() { return ratingsGiven; }

    public boolean isAdmin() { return this.personType == PersonType.ADMIN; }
    public boolean isDriver() { return this.personType == PersonType.DRIVER; }
    public boolean isPassenger() { return this.personType == PersonType.PASSENGER; }

    public String getFullName() { return firstName + " " + lastName; }

    public Double getAverageRating() {
        if (ratings == null || ratings.isEmpty()) return null;
        double sum = 0;
        for (Rating r : ratings) sum += r.getScore();
        return sum / ratings.size();
    }
}