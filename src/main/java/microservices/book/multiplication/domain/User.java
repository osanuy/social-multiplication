package microservices.book.multiplication.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "users")
public final class User {

    @Id
    @GeneratedValue
    @Column(name="USER_ID")
    private long id;
    private final String alias;

    // Empty constructor for JSON (de)serialization
    public User() {
        this.alias = null;
    }
}
