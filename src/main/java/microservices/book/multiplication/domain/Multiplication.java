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
@Table(name = "multiplication")
public final class Multiplication {


    @Id
    @GeneratedValue
    @Column(name="MULTIPLICATION_ID")
    private long id;

    // Both factors
    private final int factorA;
    private final int factorB;

    // Empty constructor for JSON (de)serialization
    public Multiplication() {
        this(0, 0);
    }
}
