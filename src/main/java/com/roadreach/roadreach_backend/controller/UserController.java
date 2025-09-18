
package com.roadreach.roadreach_backend.controller;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.roadreach.roadreach_backend.model.User;
import com.roadreach.roadreach_backend.repository.UserRepository;
import com.roadreach.roadreach_backend.model.UserData;
import com.roadreach.roadreach_backend.repository.UserDataRepository;
import com.roadreach.roadreach_backend.model.GeoCountry;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.security.SecureRandom;
import com.roadreach.roadreach_backend.model.PasswordResetCode;
import com.roadreach.roadreach_backend.repository.PasswordResetCodeRepository;
import java.time.LocalDateTime;
// ...existing code...
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    // In-memory store for reset codes (for demo; use Redis or DB for production)
    @Autowired
    private PasswordResetCodeRepository passwordResetCodeRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDataRepository userDataRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int USER_ID_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();

    private String generateUniqueUserID() {
        String id;
        do {
            StringBuilder sb = new StringBuilder();
            sb.append("RR_"); // prepend RR_
            for (int i = 0; i < USER_ID_LENGTH; i++) {
                sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            id = sb.toString();
        } while (userRepository.existsByUserid(id)); // Ensure uniqueness
        return id;
    }

    @PutMapping("/updateEmail")
    public ResponseEntity<?> updateEmail(@RequestBody java.util.Map<String, String> payload) {
        String userid = payload.get("userid");
        System.out.println("Updating email for user: " + userid);
        String newEmail = payload.get("email");
        boolean updated = false;
        try {
            Optional<User> userOpt = userRepository.findById(userid);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setEmail(newEmail);
                userRepository.save(user);
                updated = true;
            }

            Optional<UserData> userDataOpt = userDataRepository.findById(userid);
            if (userDataOpt.isPresent()) {
                UserData userData = userDataOpt.get();
                userData.setEmail(newEmail);
                userDataRepository.save(userData);
                updated = true;
            }

            if (updated) {
                return ResponseEntity.ok("Email updated in both tables.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public String createUser(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists!";
        }
        user.setUserid(generateUniqueUserID());
        userRepository.save(user);

        // Send confirmation email
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject("Welcome to RoadReach!");
            helper.setText(
                "Hello " + user.getFirstName() + ",<br>" +
                "Your account has been created successfully!<br>" +
                "Your User ID is: <b>" + user.getUserid() + "</b>",
                true
            );
            mailSender.send(message);
        } catch (Exception e) {
            return "User created, but failed to send email: " + e.getMessage();
        }
        return "User created successfully!";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        Optional<User> userOpt = userRepository.findByEmailAndPassword(loginUser.getEmail(), loginUser.getPassword());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println(user.getFirstName() + user.getLastName() + user.getEmail() + user.getUserid());
            return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "firstname", user.getFirstName(),
                    "lastname", user.getLastName(),
                    "email", user.getEmail(),
                    "userid", user.getUserid()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("success", false));
        }
    }

    @PostMapping("/userData")
    public ResponseEntity<?> saveUserData(@RequestBody UserData userData) {
        // if (userDataRepository.existsById(userData.getUserid())) {
        //     return ResponseEntity.status(HttpStatus.CONFLICT).body("User data already exists for this userid.");
        // }
        userDataRepository.save(userData);
        return ResponseEntity.ok("User data saved successfully!");
    }

    @PutMapping("/userData")
    public ResponseEntity<?> updateAddress(@RequestBody UserData updatedData) {
        Optional<UserData> userDataOpt = userDataRepository.findByUserid(updatedData.getUserid());
        if (userDataOpt.isPresent()) {
            UserData userData = userDataOpt.get();
            userData.setPhonenumber(updatedData.getPhonenumber());
            userData.setAddress1(updatedData.getAddress1());
            userData.setAddress2(updatedData.getAddress2());
            userData.setCity(updatedData.getCity());
            userData.setState(updatedData.getState());
            userData.setZipcode(updatedData.getZipcode());
            userData.setCountry(updatedData.getCountry());
            userDataRepository.save(userData);
            return ResponseEntity.ok("Address updated successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    @GetMapping("/profile/{userid}")
    public ResponseEntity<?> getProfile(@PathVariable String userid) {
        Optional<UserData> userDataOpt = userDataRepository.findByUserid(userid);
        if (userDataOpt.isPresent()) {
            UserData userData = userDataOpt.get();
            return ResponseEntity.ok().body(java.util.Map.of(
                    "address1", userData.getAddress1(),
                    "address2", userData.getAddress2(),
                    "phonenumber", userData.getPhonenumber(),
                    "city", userData.getCity(),
                    "state", userData.getState(),
                    "zipcode", userData.getZipcode(),
                    "country", userData.getCountry()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(java.util.Map.of(
                    "message", "User not found"));
        }
    }

    @DeleteMapping("/profile/{userid}")
    public ResponseEntity<?> deleteUser(@PathVariable String userid) {
        boolean userExists = userRepository.existsByUserid(userid);
        boolean userDataExists = userDataRepository.existsByUserid(userid);

        if (userExists || userDataExists) {
            if (userExists) {
                userRepository.deleteByUserid(userid);
            }
            if (userDataExists) {
                userDataRepository.deleteByUserid(userid);
            }
            return ResponseEntity.ok("User deleted successfully from both tables.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found in either table.");
        }
    }

    @GetMapping("/geo/countries")
    public ResponseEntity<?> getCountries() {
        return ResponseEntity.ok(GeoCountry.getAllCountries());
    }

    @PutMapping("/updatePhone")
    public ResponseEntity<?> updatePhone(@RequestBody java.util.Map<String, String> payload) {
        String userid = payload.get("userid");
        String newPhone = payload.get("phonenumber");
        try {
            Optional<UserData> userDataOpt = userDataRepository.findByUserid(userid);
            if (userDataOpt.isPresent()) {
                UserData userData = userDataOpt.get();
                userData.setPhonenumber(newPhone);
                userDataRepository.save(userData);
                return ResponseEntity.ok("Phone number updated successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody java.util.Map<String, String> payload) {
        String userid = payload.get("userid");
        String newPassword = payload.get("password");
        try {
            Optional<User> userOpt = userRepository.findById(userid);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setPassword(newPassword);
                userRepository.save(user);
                return ResponseEntity.ok("Password updated successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
    // ...your logic here...
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not implemented.");
    }
    // Removed duplicate declaration

    @PostMapping("/send-reset-code")
    @Transactional
    public ResponseEntity<?> sendResetCode(@RequestBody java.util.Map<String, String> payload) {
        String email = payload.get("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(java.util.Map.of("success", false, "message", "Email not found."));
        }
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        String codeStr = String.valueOf(code);
        // Remove any previous code for this email
        passwordResetCodeRepository.deleteByEmail(email);
        // Set code to expire in 15 minutes
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        PasswordResetCode resetCode = new PasswordResetCode(email, codeStr, expiresAt);
        passwordResetCodeRepository.save(resetCode);
        System.out.println("Reset code for " + email + ": " + codeStr);
        // Send code via email
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Your RoadReach Password Reset Code");
            helper.setText(
                "Hello,<br><br>Your password reset code is: <b>" + codeStr + "</b><br><br>If you did not request this, please ignore this email.",
                true
            );
            mailSender.send(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(java.util.Map.of("success", false, "message", "Failed to send email: " + e.getMessage()));
        }
        return ResponseEntity.ok()
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .body(java.util.Map.of("success", true));
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody java.util.Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");
        var codeOpt = passwordResetCodeRepository.findByEmailAndCode(email, code);
        if (codeOpt.isPresent() && codeOpt.get().getExpiresAt().isAfter(LocalDateTime.now())) {
            return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(java.util.Map.of("valid", 1));
        }
        return ResponseEntity.ok()
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .body(java.util.Map.of("valid", 0));
    }

    @Transactional
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody java.util.Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");
        String newPassword = payload.get("password");
        var codeOpt = passwordResetCodeRepository.findByEmailAndCode(email, code);
        if (codeOpt.isPresent() && codeOpt.get().getExpiresAt().isAfter(LocalDateTime.now())) {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setPassword(newPassword);
                userRepository.save(user);
                passwordResetCodeRepository.deleteByEmailAndCode(email, code);
                return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(java.util.Map.of("success", true));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(java.util.Map.of("success", false, "message", "User not found."));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .body(java.util.Map.of("success", false, "message", "Invalid or expired code."));
    }

}