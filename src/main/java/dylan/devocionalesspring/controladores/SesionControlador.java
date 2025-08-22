package dylan.devocionalesspring.controladores;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SesionControlador {

    @GetMapping("/session-status")
    public ResponseEntity<Map<String, Object>> sessionStatus(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Map<String, Object> response = new HashMap<>();
        if (session == null) {
            response.put("status", "unauthenticated");
        } else {
            response.put("status", "active");
            response.put("remainingTime", session.getMaxInactiveInterval() - (System.currentTimeMillis() - session.getLastAccessedTime()) / 1000);
        }
        return ResponseEntity.ok(response);
    }
}