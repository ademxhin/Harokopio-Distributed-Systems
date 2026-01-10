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

    // Διαγράφει αυτόματα τις διαδρομές που οδηγούσε ο χρήστης αν διαγραφεί ο ίδιος
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ride> rides = new ArrayList<>();

    // Διαγράφει αυτόματα τις κρατήσεις του χρήστη αν διαγραφεί ο ίδιος
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "ratedPerson", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Rating> ratings = new ArrayList<>();

    // ✅ Κενός constructor για JPA
    public Person() {}

    // ✅ Constructor με 7 παραμέτρους για το Business Logic Service
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

    // ✅ Χρειάζεται για τον PersonMapper
    public String getAverageRating() {
        if (ratings == null || ratings.isEmpty()) return "No rating";
        double sum = 0;
        for (Rating r : ratings) sum += r.getScore();
        return String.format("%.1f ★", sum / ratings.size());
    }
}