package antifraud.businesslayer.antifraud.stolencard;

import antifraud.persistence.antifraud.StolenCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StolenCardService {
    @Autowired
    StolenCardRepository stolenCardRepository;

    public ResponseEntity<StolenCardResponseDTO> saveStolenCard(StolenCardRequestDTO request) {
        if (request == null
                || request.getNumber() == null
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        StolenCard stolenCard = stolenCardRepository.findStolenCardBy(request.getNumber());
        if (stolenCard != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        boolean isCardNumberValid = isCardNumberValid(request.getNumber());
        if (!isCardNumberValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        StolenCard stolenCard1 = new StolenCard(request.getNumber());
        stolenCard1 = stolenCardRepository.save(stolenCard1);

        StolenCardResponseDTO response = new StolenCardResponseDTO(stolenCard1.getId(), stolenCard1.getNumber());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<Object> deleteStolenCard(String cardNumber) {
        boolean isCardNumberValid = isCardNumberValid(cardNumber);
        if (!isCardNumberValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        StolenCard stolenCard = stolenCardRepository.findStolenCardBy(cardNumber);
        if (stolenCard == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        stolenCardRepository.delete(stolenCard);

        Map<String, String> response = new HashMap<>();
        response.put("status", "Card " + cardNumber + " successfully removed!");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<List<StolenCard>> getAllStolenCard() {
        List<StolenCard> stolenCardsList = stolenCardRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(stolenCardsList);
    }

    public boolean isCardNumberValid(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }
}
