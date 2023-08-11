package antifraud.businesslayer.limit;

import jakarta.persistence.*;

@Entity
@Table(name = "amount_limit")
public class AmountLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String number;
    private long maxAllowed;
    private long maxManual;

    public AmountLimit() {
    }

    public AmountLimit(String number, long maxAllowed, long maxManual) {
        this.number = number;
        this.maxAllowed = maxAllowed;
        this.maxManual = maxManual;
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

    public long getMaxAllowed() {
        return maxAllowed;
    }

    public void setMaxAllowed(long maxAllowed) {
        this.maxAllowed = maxAllowed;
    }

    public long getMaxManual() {
        return maxManual;
    }

    public void setMaxManual(long maxManual) {
        this.maxManual = maxManual;
    }

    @Override
    public String toString() {
        return "AmountLimit{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", maxAllowed=" + maxAllowed +
                ", maxManual=" + maxManual +
                '}';
    }
}
