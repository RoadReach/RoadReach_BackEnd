package com.roadreach.roadreach_backend.controller;

import com.roadreach.roadreach_backend.model.SupportRequest;
import com.roadreach.roadreach_backend.repository.SupportRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/support/requests")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST,
        RequestMethod.OPTIONS })
public class SupportRequestController {

    private static final Logger log = LoggerFactory.getLogger(SupportRequestController.class);

    @Autowired
    private SupportRequestRepository supportRequestRepository;

    /**
     * Accept JSON body submissions.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> submitRequestJson(@RequestBody SupportRequest request) {
        return saveRequest(request);
    }

    /**
     * Accept classic form submissions (application/x-www-form-urlencoded).
     */
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> submitRequestForm(SupportRequest request) {
        return saveRequest(request);
    }

    private ResponseEntity<?> saveRequest(SupportRequest request) {
        try {
            if (isBlank(request.getFullName()) || isBlank(request.getEmail())
                    || isBlank(request.getRequestType()) || isBlank(request.getDescription())) {
                return ResponseEntity.badRequest()
                        .body("Missing required fields: fullName, email, requestType, description");
            }
            log.info("Saving support request: {}", request);
            SupportRequest saved = supportRequestRepository.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (DataIntegrityViolationException ex) {
            String root = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
            log.error("Data integrity error: {}", root);
            return ResponseEntity.badRequest().body("Invalid data: " + root);
        } catch (Exception ex) {
            log.error("Unexpected error", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save support request");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Return all support requests (for debugging/browser testing).
     */
    @GetMapping
    public ResponseEntity<?> getAllRequests() {
        try {
            return ResponseEntity.ok(supportRequestRepository.findAll());
        } catch (Exception ex) {
            log.error("Error fetching support requests", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch support requests");
        }
    }
}
