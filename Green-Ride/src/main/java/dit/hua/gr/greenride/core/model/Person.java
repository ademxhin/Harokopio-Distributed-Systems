package dit.hua.gr.greenride.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "person")
@Getter
@Setter
@NoArgsConstructor // Απαραίτητο για JPA
public final class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String mobilePhoneNumber;

    @Column(nullable = false, unique = true)
    private String emailAddress;

    /**
     * System-level role (security)
     * USER / ADMIN
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonType personType;

    /**
     * Business usage role
     * PASSENGER / DRIVER / BOTH
     * null for ADMIN
     */
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(nullable = false)
    private String hashedPassword;

    /* =========================
       ADMIN-RELATED FIELDS
    ========================= */
    private int reportCount = 0;
    private boolean banned = false;

    /* =========================
       CONSTRUCTORS
    ========================= */

    /**
     * Constructor for USER registration
     */
    public Person(
            String userId,
            String firstName,
            String lastName,
            String mobilePhoneNumber,
            String emailAddress,
            UserType userType,
            String hashedPassword
    ) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.emailAddress = emailAddress;
        this.personType = PersonType.USER;
        this.userType = userType;
        this.hashedPassword = hashedPassword;
    }

    /**
     * Constructor for ADMIN
     */
    public Person(
            String userId,
            String firstName,
            String lastName,
            String emailAddress,
            String hashedPassword
    ) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.personType = PersonType.ADMIN;
        this.userType = null;
        this.hashedPassword = hashedPassword;
    }
    // Προσθήκη Getters στο Person.java για να σταματήσουν τα σφάλματα του Mapper
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getMobilePhoneNumber() { return mobilePhoneNumber; }

    /* =========================
       DOMAIN HELPERS
    ========================= */

    public boolean isAdmin() {
        return this.personType == PersonType.ADMIN;
    }

    public boolean isUser() {
        return this.personType == PersonType.USER;
    }

    public boolean isPassenger() {
        return this.userType != null && this.userType.isPassenger();
    }

    public boolean isDriver() {
        return this.userType != null && this.userType.isDriver();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @OneToMany(mappedBy = "ratedPerson", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private java.util.List<Rating> ratings = new java.util.ArrayList<>();

    public String getAverageRating() {
        if (ratings == null || ratings.isEmpty()) {
            return "No rating";
        }

        double sum = 0;
        for (Rating r : ratings) {
            sum += r.getScore();
        }
        double avg = sum / ratings.size();

        // Επιστρέφει το πραγματικό αποτέλεσμα (π.χ. 3.0 ★)
        return String.format("%.1f ★", avg);
    }


}