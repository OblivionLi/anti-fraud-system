package antifraud.businesslayer.enums;

public enum TransactionRegion {
    EAP,
    ECA,
    HIC,
    LAC,
    MENA,
    SA,
    SSA;

    public static boolean contains(String value) {
        for (TransactionRegion region : TransactionRegion.values()) {
            if (region.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
