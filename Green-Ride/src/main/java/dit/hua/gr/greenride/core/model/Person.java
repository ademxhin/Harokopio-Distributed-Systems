package dit.hua.gr.greenride.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "person",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_person_user_id", columnNames = "user_id"),
                @UniqueConstraint(name = "uk_person_email_address", columnNames = "email_address"),
                @UniqueConstraint(name = "uk_person_mobile_phone_number", columnNames = "mobile_phone_number")
        },
        indexes = {
                @Index(name = "idx_person_type", columnList = "person_type"),
                @Index(name = "idx_person_last_name", columnList = "last_name")
        }
)
public final class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    // Public display id (e.g. GR-000123). Generated in the service layer.
    @NotNull
    @NotBlank
    @Size(max = 20)
    @Column(name = "user_id", nullable = false, length = 20)
    private String userId;

    @NotNull
    @NotBlank
    @Size(max = 20)
    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @NotNull
    @NotBlank
    @Size(max = 20)
    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @NotNull
    @NotBlank
    @Size(max = 18)
    @Column(name = "mobile_phone_number", nullable = false, length = 18)
    private String mobilePhoneNumber;

    @NotNull
    @NotBlank
    @Size(max = 40)
    @Email
    @Column(name = "email_address", nullable = false, length = 40)
    private String emailAddress;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "person_type", nullable = false, length = 20)
    private PersonType personType;

    @NotNull
    @NotBlank
    @Size(max = 255)
    @Column(name = "hashed_password", nullable = false, length = 255)
    private String hashedPassword;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Required by JPA
    protected Person() {
    }

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
        this.personType = personType;
        this.hashedPassword = hashedPassword;
    }

    // Getters (no public setter for id or createdAt)

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public PersonType getPersonType() {
        return personType;
    }

    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", personType=" + personType +
                '}';
    }
}