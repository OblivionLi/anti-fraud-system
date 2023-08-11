package antifraud.businesslayer.limit;

import antifraud.businesslayer.transaction.Transaction;
import antifraud.persistence.limit.AmountLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AmountLimitService {

    @Autowired
    AmountLimitRepository amountLimitRepository;

    public AmountLimit getAmountLimits(String number) {
        AmountLimit amountLimit = amountLimitRepository.findByNumber(number);
        if (amountLimit == null) {
            amountLimit = new AmountLimit();
            amountLimit.setMaxManual(1500L);
            amountLimit.setMaxAllowed(200L);
        }

        return amountLimit;
    }

    public void updateAmountLimit(Transaction transaction) {
        AmountLimit amountLimit = getAmountLimits(transaction.getNumber());

        long currentAllowedLimit = amountLimit.getMaxAllowed();
        long currentManualLimit = amountLimit.getMaxManual();

        switch (transaction.getTransactionStatus()) {
            case "ALLOWED" -> {
                switch (transaction.getFeedback()) {
                    case "MANUAL_PROCESSING" -> amountLimit.setMaxAllowed(decreaseLimit(currentAllowedLimit, transaction.getAmount()));
                    case "PROHIBITED" -> {
                        amountLimit.setMaxAllowed(decreaseLimit(currentAllowedLimit, transaction.getAmount()));
                        amountLimit.setMaxManual(decreaseLimit(currentManualLimit, transaction.getAmount()));
                    }
                }
            }

            case "MANUAL_PROCESSING" -> {
                switch (transaction.getFeedback()) {
                    case "ALLOWED" -> amountLimit.setMaxAllowed(increaseLimit(currentAllowedLimit, transaction.getAmount()));
                    case "PROHIBITED" -> amountLimit.setMaxManual(decreaseLimit(currentManualLimit, transaction.getAmount()));
                }
            }

            case "PROHIBITED" -> {
                switch (transaction.getFeedback()) {
                    case "ALLOWED" -> {
                        amountLimit.setMaxAllowed(increaseLimit(currentAllowedLimit, transaction.getAmount()));
                        amountLimit.setMaxManual(increaseLimit(currentManualLimit, transaction.getAmount()));
                    }
                    case "MANUAL_PROCESSING" -> amountLimit.setMaxManual(increaseLimit(currentManualLimit, transaction.getAmount()));
                }
            }
        }

        amountLimit.setNumber(transaction.getNumber());
        amountLimitRepository.save(amountLimit);
    }

    private long increaseLimit(long currentMaxLimit, long currentAmount) {
        return (long) Math.ceil(0.8 * currentMaxLimit + 0.2 * currentAmount);
    }

    private long decreaseLimit(long currentMaxLimit, long currentAmount) {
        return (long) Math.ceil(0.8 * currentMaxLimit - 0.2 * currentAmount);
    }
}
