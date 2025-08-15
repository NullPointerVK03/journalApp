package com.VishalSharma.journalApp.controller;

import com.VishalSharma.journalApp.entity.JournalEntry;
import com.VishalSharma.journalApp.services.JournalEntryService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/journal")
public class JournalController {
    //    Autowire the JournalEntryService
    @Autowired
    private JournalEntryService journalEntryService;

    //    health check dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        String msg = "Welcome " + userName + "! Journal dashboard is working fine.";
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    //    CRUD operations
//    create
    @PostMapping("/create-new-journal")
    public ResponseEntity<String> createJournal(@RequestBody JournalEntry entry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            log.info("incoming POST request to create new JournalEntry for userName: {}", userName);
            journalEntryService.saveNewEntry(entry, userName);
            log.info("New JournalEntry created for userName: {}", userName);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            log.warn("Something went wrong while creating a JournalEntry for a user", e);
            return new ResponseEntity<>("Some error occurred, during creating a new journalEntry", HttpStatus.BAD_REQUEST);
        }
    }

    //    read journal entries of a user
    @GetMapping("/get-all-journals")
    public ResponseEntity<List<JournalEntry>> getJournalEntriesOfUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            log.info("incoming GET request to get all JournalEntries for userName: {}.", userName);
            List<JournalEntry> journalEntriesOfUser = journalEntryService.getAllJournalsOfUser(userName);
            if (journalEntriesOfUser != null && !journalEntriesOfUser.isEmpty()) {
                log.info("Returning JournalEntries for userName: {}", userName);
                return new ResponseEntity<>(journalEntriesOfUser, HttpStatus.OK);
            }
            log.info("No JournalEntries found for userName: {}, returning empty response.", userName);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.warn("Some error occurred while fetching all JournalEntries of a user.", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //    update
    @PutMapping("/update-journal/{jId}")
    public ResponseEntity<Void> updateJournalEntryById(@PathVariable ObjectId jId, @RequestBody JournalEntry entry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            log.info("incoming PUT request to update JournalEntry with jID: {}, for userName: {}", jId, userName);
            journalEntryService.updateJournalEntry(jId, entry, userName);
            log.info("JournalEntry with jId: {}, updated for userName: {}", jId, userName);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Some error occurred while updating JournalEntry of a user.", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //    delete entry
    @DeleteMapping("/delete-journal/{jId}")
    public ResponseEntity<Void> deleteJournalById(@PathVariable ObjectId jId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            log.info("Incoming DELETE request to delete a JournalEntry with jId: {}, of userName: {}.", jId, userName);
            journalEntryService.deleteJournalById(jId, userName);
            log.info("JournalEntry with jId: {}, deleted.", jId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.warn("jId: {} not found.", jId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete-all-journal")
    public ResponseEntity<Void> deleteAllJournals() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            log.info("Incoming DELETE request to delete all JournalEntries of userName: {}", userName);
            journalEntryService.deleteAllJournalsOfUser(userName);
            log.info("All JournalEntries of userName: {}, deleted", userName);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Something went wrong while deleting all JournalEntries of a user.", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
