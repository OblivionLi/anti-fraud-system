package antifraud.businesslayer.antifraud.stolencard;

public class StolenCardResponseDTO {
    private long id;
    private String number;

    public StolenCardResponseDTO(long id, String number) {
        this.id = id;
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
