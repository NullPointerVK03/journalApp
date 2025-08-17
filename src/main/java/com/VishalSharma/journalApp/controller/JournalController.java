package com.VishalSharma.journalApp.controller;

import com.VishalSharma.journalApp.dto.JournalEntryDTO;
import com.VishalSharma.journalApp.entity.JournalEntry;
import com.VishalSharma.journalApp.services.JournalEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/journal")
@Tag(name = "JournalEntry APIs", description = "Dashboard, Create, Read, Update, Delete JournalEntry")
public class JournalController {

    private final JournalEntryService journalEntryService;

    public JournalController(JournalEntryService journalEntryService) {
        this.journalEntryService = journalEntryService;
    }

    // health check dashboard
    @GetMapping("/dashboard")
    @Operation(description = "JournalController Dashboard")
    public ResponseEntity<String> dashboard() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            log.info("GET /journal/dashboard requested by user: {}", userName);
            String msg = "Welcome " + userName + "! Journal dashboard is working fine.";

            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            log.error("Error accessing /journal/dashboard", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong");
        }
    }

    // create
    @PostMapping("/create-new-journal")
    @Operation(description = "Create JournalEntry of a User")
    public ResponseEntity<String> createJournal(@RequestBody JournalEntryDTO entry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            log.info("POST /journal/create-new-journal for user: {}", userName);
            journalEntryService.saveNewEntry(entry, userName);

            log.info("JournalEntry created successfully for user: {}", userName);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Journal entry created successfully.");
        } catch (Exception e) {
            log.error("Error creating journal entry", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Some error occurred while creating a new journal entry");
        }
    }

    // read
    @GetMapping("/all-journals")
    @Operation(description = "Get all JournalEntries of a User")
    public ResponseEntity<List<JournalEntry>> getJournalEntriesOfUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            log.info("GET /journal/all-journals requested by user: {}", userName);
            List<JournalEntry> journalEntriesOfUser = journalEntryService.getAllJournalsOfUser(userName);

            if (journalEntriesOfUser != null && !journalEntriesOfUser.isEmpty()) {
                log.info("Returning {} journal entries for user: {}", journalEntriesOfUser.size(), userName);
                return ResponseEntity.ok(journalEntriesOfUser);
            }

            log.info("No journal entries found for user: {}", userName);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error fetching journal entries", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // update
    @PutMapping("/update-journal/{jId}")
    @Operation(description = "Updates JournalEntry by ID of a User")
    public ResponseEntity<String> updateJournalEntryById(@PathVariable String jId, @RequestBody JournalEntryDTO entry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            log.info("PUT /journal/update-journal/{} requested by user: {}", jId, userName);

            ObjectId id = new ObjectId(jId);
            journalEntryService.updateJournalEntry(id, entry, userName);

            log.info("JournalEntry {} updated for user: {}", jId, userName);
            return ResponseEntity.ok("Journal entry updated successfully.");
        } catch (Exception e) {
            log.warn("Error updating journal entry with id: {}", jId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Journal entry not found with id: " + jId);
        }
    }

    // delete one
    @DeleteMapping("/delete-journal/{jId}")
    @Operation(description = "Delete JournalEntry By ID of a User")
    public ResponseEntity<String> deleteJournalById(@PathVariable String jId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            log.info("DELETE /journal/delete-journal/{} requested by user: {}", jId, userName);

            ObjectId id = new ObjectId(jId);
            journalEntryService.deleteJournalById(id, userName);

            log.info("JournalEntry {} deleted for user: {}", jId, userName);
            return ResponseEntity.ok("Journal entry deleted successfully.");
        } catch (Exception e) {
            log.warn("Error deleting journal entry with id: {}", jId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Journal entry not found with id: " + jId);
        }
    }

    // delete all
    @DeleteMapping("/delete-all-journal")
    @Operation(description = "Deletes all JournalEntries of a User")
    public ResponseEntity<String> deleteAllJournals() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            log.info("DELETE /journal/delete-all-journal requested by user: {}", userName);
            journalEntryService.deleteAllJournalsOfUser(userName);

            log.info("All journal entries deleted for user: {}", userName);
            return ResponseEntity.ok("All journal entries deleted successfully.");
        } catch (Exception e) {
            log.error("Error deleting all journal entries for user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while deleting all journal entries");
        }
    }
}
