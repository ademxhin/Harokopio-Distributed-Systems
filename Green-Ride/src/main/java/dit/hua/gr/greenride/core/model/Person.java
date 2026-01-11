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
    private PersonType personType;

    @Enumerated(EnumType.STRING)
    private UserType userType;

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

    public Person(String userId, String firstName, String lastName, String mobilePhoneNumber,
                  String emailAddress, UserType userType, String hashedPassword) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.emailAddress = emailAddress;
        this.userType = userType;
        this.hashedPassword = hashedPassword;
        this.personType = PersonType.USER; // Προεπιλογή
    }

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    public String getMobilePhoneNumber() { return mobilePhoneNumber; }
    public void setMobilePhoneNumber(String mobilePhoneNumber) { this.mobilePhoneNumber = mobilePhoneNumber; }
    public String getHashedPassword() { return hashedPassword; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }
    public PersonType getPersonType() { return personType; }
    public void setPersonType(PersonType personType) { this.personType = personType; }
    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }
    public List<Rating> getRatings() { return ratings; }

    // --- HELPER METHODS ---
    public boolean isAdmin() { return this.personType == PersonType.ADMIN; }
    public String getFullName() { return firstName + " " + lastName; }


    public Double getAverageRating() {
        if (ratings == null || ratings.isEmpty()) return null;
        double sum = 0;
        for (Rating r : ratings) sum += r.getScore();
        return sum / ratings.size();
    }
}