package antifraud.presentation.antifraud;

import antifraud.businesslayer.antifraud.stolencard.StolenCard;
import antifraud.businesslayer.antifraud.stolencard.StolenCardRequestDTO;
import antifraud.businesslayer.antifraud.stolencard.StolenCardResponseDTO;
import antifraud.businesslayer.antifraud.stolencard.StolenCardService;
import antifraud.businesslayer.antifraud.suspiciousip.SuspiciousIp;
import antifraud.businesslayer.antifraud.suspiciousip.SuspiciousIpRequestDTO;
import antifraud.businesslayer.antifraud.suspiciousip.SuspiciousIpResponseDTO;
import antifraud.businesslayer.antifraud.suspiciousip.SuspiciousIpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
public class AntiFraudController {
    @Autowired
    SuspiciousIpService suspiciousIpService;

    @Autowired
    StolenCardService stolenCardService;

    @PostMapping("/suspicious-ip")
    public ResponseEntity<SuspiciousIpResponseDTO> saveSuspiciousIp(@RequestBody SuspiciousIpRequestDTO requestDTO) {
        return suspiciousIpService.saveSuspiciousIp(requestDTO);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<Object> deleteSuspiciousIp(@PathVariable String ip) {
        return suspiciousIpService.deleteSuspiciousIp(ip);
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<List<SuspiciousIp>> listSuspiciousIps() {
        return suspiciousIpService.getAllSuspiciousIps();
    }


    @PostMapping("/stolencard")
    public ResponseEntity<StolenCardResponseDTO> saveStolenCard(@RequestBody StolenCardRequestDTO requestDTO) {
        return stolenCardService.saveStolenCard(requestDTO);
    }

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<Object> deleteStolenCard(@PathVariable String number) {
        return stolenCardService.deleteStolenCard(number);
    }

    @GetMapping("/stolencard")
    public ResponseEntity<List<StolenCard>> listStolenCards() {
        return stolenCardService.getAllStolenCard();
    }
}
