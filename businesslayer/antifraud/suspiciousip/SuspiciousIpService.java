package antifraud.businesslayer.antifraud.suspiciousip;

import antifraud.persistence.antifraud.SuspiciousIpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SuspiciousIpService {
    @Autowired
    SuspiciousIpRepository suspiciousIpRepository;

    public ResponseEntity<SuspiciousIpResponseDTO> saveSuspiciousIp(SuspiciousIpRequestDTO request) {
        if (request == null
                || request.getIp() == null
                || request.getIp().isEmpty()
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        SuspiciousIp suspiciousIp = suspiciousIpRepository.findSuspiciousIpBy(request.getIp());
        if (suspiciousIp != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        boolean isIpValid = isIpValid(request.getIp());
        if (!isIpValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        SuspiciousIp suspiciousIp1 = new SuspiciousIp(request.getIp());
        suspiciousIp1 = suspiciousIpRepository.save(suspiciousIp1);

        SuspiciousIpResponseDTO response = new SuspiciousIpResponseDTO(suspiciousIp1.getId(), suspiciousIp1.getIp());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<Object> deleteSuspiciousIp(String ip) {
        if (!isIpValid(ip)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        SuspiciousIp suspiciousIp = suspiciousIpRepository.findSuspiciousIpBy(ip);
        if (suspiciousIp == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        suspiciousIpRepository.delete(suspiciousIp);

        Map<String, String> response = new HashMap<>();
        response.put("status", "IP " + ip + " successfully removed!");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<List<SuspiciousIp>> getAllSuspiciousIps() {
        List<SuspiciousIp> suspiciousIpList = suspiciousIpRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(suspiciousIpList);
    }

    public boolean isIpValid(String ip) {
        String[] ipParts = ip.split("\\.");
        if (ipParts.length != 4) {
            return false;
        }

        boolean isIpValid = true;
        for (var part : ipParts) {
            try {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    isIpValid = false;
                    break;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return isIpValid;
    }
}
