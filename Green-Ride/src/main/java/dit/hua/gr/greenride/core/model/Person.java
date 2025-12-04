package dit.hua.gr.greenride.core.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "person",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_person_email_address", columnNames = "email_address"),
                @UniqueConstraint(name="uk_person_mobile_phone_number", columnNames = "mobile_phone_number")
        }
)
public class Person {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="first_name", nullable=false, length=20)
    private String firstName;

    @Column(name="last_name", nullable=false, length=20)
    private String lastName;

    @Column(name="mobile_phone_number", nullable=false, length=18)
    private String mobilePhoneNumber;

    @Column(name="email_address", nullable=false, length=40)
    private String emailAddress;

    @Enumerated(EnumType.STRING)
    @Column(name="person_type", nullable=false, length=20)
    private PersonType personType;

    @Column(name="hashed_password", nullable=false)
    private String hashedPassword;

    protected Person() {
    }

    public Person(Long id,
                  String firstName,
                  String lastName,
                  String mobilePhoneNumber,
                  String emailAddress,
                  PersonType personType,
                  String hashedPassword) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.emailAddress = emailAddress;
        this.personType = personType;
        this.hashedPassword = hashedPassword;
    }

    // Getters – Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
}

