package dit.hua.gr.greenride.core.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "person")
@Getter @Setter @NoArgsConstructor // Απαραίτητο για JPA
public final class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String userId;
    private String firstName;
    private String lastName;
    private String mobilePhoneNumber; // Λύνει το σφάλμα στο PersonMapper
    private String emailAddress;

    @Enumerated(EnumType.STRING)
    private PersonType personType;

    private String hashedPassword;

    // ΠΕΔΙΑ ΓΙΑ ADMIN LOGIC
    private int reportCount = 0;
    private boolean banned = false;
    private boolean isDriver = false; // Λύνει το σφάλμα στο AdminService

    // ✅ Αυτός ο constructor λύνει το σφάλμα στη γραμμή 121 του PersonBusinessLogicServiceImpl
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
}