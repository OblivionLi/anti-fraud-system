package antifraud.businesslayer.antifraud.stolencard;

import jakarta.persistence.*;

@Entity
@Table(name = "stolen_cards")
public class StolenCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String number;

    public StolenCard() {}

    public StolenCard(String number) {
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
