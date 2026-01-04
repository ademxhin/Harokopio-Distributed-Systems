package dit.hua.gr.greenride.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Table(name = "person")
public final class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull @NotBlank @Size(max = 20)
    @Column(name = "user_id", nullable = false, length = 20)
    private String userId;

    @NotNull @NotBlank @Size(max = 20)
    private String firstName;

    @NotNull @NotBlank @Size(max = 20)
    private String lastName;

    @NotNull @NotBlank @Size(max = 18)
    private String mobilePhoneNumber; // Απαραίτητο για το PersonBusinessLogicServiceImpl

    @NotNull @NotBlank @Email @Size(max = 40)
    private String emailAddress;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PersonType personType;

    @NotNull @NotBlank
    private String hashedPassword;

    @CreationTimestamp
    private Instant createdAt;

    // ΠΕΔΙΑ ΓΙΑ ΤΟ ΘΕΜΑ 4: GREENRIDE
    private int reportCount = 0;
    private boolean banned = false;
    private boolean isDriver = false;

    protected Person() {}

    public Person(String userId, String firstName, String lastName, String mobilePhoneNumber,
                  String emailAddress, PersonType personType, String hashedPassword) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.emailAddress = emailAddress;
        this.personType = personType;
        this.hashedPassword = hashedPassword;
    }

    // --- GETTERS ---
    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getMobilePhoneNumber() { return mobilePhoneNumber; }
    public String getEmailAddress() { return emailAddress; }
    public PersonType getPersonType() { return personType; }
    public String getHashedPassword() { return hashedPassword; }
    public int getReportCount() { return reportCount; }
    public boolean isBanned() { return banned; }
    public boolean isDriver() { return isDriver; }

    // --- SETTERS ---
    public void setReportCount(int reportCount) { this.reportCount = reportCount; }
    public void setBanned(boolean banned) { this.banned = banned; }
    public void setDriver(boolean driver) { isDriver = driver; }
}