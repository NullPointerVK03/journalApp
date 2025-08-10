package com.VishalSharma.journalApp.controller;

import com.VishalSharma.journalApp.entity.JournalEntry;
import com.VishalSharma.journalApp.services.JournalEntryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalController {
//    Autowire the JournalEntryService
    @Autowired
    private JournalEntryService journalEntryService;

//    health check dashboard
@GetMapping("/dashboard")
public ResponseEntity<String> dashboard(){
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userName = authentication.getName();
    String msg = "Welcome " + userName + "! Journal dashboard is working fine.";
    return new ResponseEntity<>(msg, HttpStatus.OK);
}

//    CRUD operations
//    create
    @PostMapping("/create-new-journal")
    public ResponseEntity<String> createJournal(@RequestBody JournalEntry entry){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            journalEntryService.saveNewEntry(entry, userName);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>("Some error occurred, please try again.",HttpStatus.BAD_REQUEST);
        }
    }

//    read journal entries of a user
    @GetMapping("/get-all-journals")
    public ResponseEntity<List<JournalEntry>> getJournalEntriesOfUser(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            List<JournalEntry> journalEntriesOfUser = journalEntryService.getAllJournalsOfUser(userName);
            if(journalEntriesOfUser != null && !journalEntriesOfUser.isEmpty()) {
                return new ResponseEntity<>(journalEntriesOfUser, HttpStatus.OK);
            }return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

//    update
    @PutMapping("/update-journal")
    public ResponseEntity<Void> updateJournalEntryById(@RequestBody JournalEntry entry){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            journalEntryService.updateJournalEntry(entry, userName);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    delete entry
    @DeleteMapping("/delete-journal/{idToDelete}")
    public ResponseEntity<Void> deleteJournalById(@PathVariable ObjectId idToDelete){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            journalEntryService.deleteJournalById(idToDelete, userName);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/delete-all-journal")
    public ResponseEntity<Void> deleteAllJournals(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            journalEntryService.deleteAllJournalsOfUser(userName);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



}
