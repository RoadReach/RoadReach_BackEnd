package com.roadreach.roadreach_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.roadreach.roadreach_backend.model.User;
import com.roadreach.roadreach_backend.repository.UserRepository;
import com.roadreach.roadreach_backend.model.UserData;
import com.roadreach.roadreach_backend.repository.UserDataRepository;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.security.SecureRandom;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

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
            StringBuilder sb = new StringBuilder(USER_ID_LENGTH);
            for (int i = 0; i < USER_ID_LENGTH; i++) {
                sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            id = sb.toString();
        } while (userRepository.existsByUserid(id)); // Ensure uniqueness
        return id;
    }

    @PostMapping("/create")
    public String createUser(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists!";
        }
        user.setUserid(generateUniqueUserID());
        userRepository.save(user);
        return "User created successfully!";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        Optional<User> userOpt = userRepository.findByEmailAndPassword(loginUser.getEmail(), loginUser.getPassword());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println(user.getFirstName()+user.getLastName()+user.getEmail()+user.getUserid());
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "firstname", user.getFirstName(),
                "lastname", user.getLastName(),
                "email", user.getEmail(),
                "userid", user.getUserid()
            ));
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
                "country", userData.getCountry()
            ));
        } else {
            return ResponseEntity.ok().body(java.util.Map.of());
        }
    }
}