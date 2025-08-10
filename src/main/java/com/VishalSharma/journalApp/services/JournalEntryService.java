package com.VishalSharma.journalApp.services;

import com.VishalSharma.journalApp.entity.JournalEntry;
import com.VishalSharma.journalApp.entity.User;
import com.VishalSharma.journalApp.repository.JournalEntryRepository;
import com.VishalSharma.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JournalEntryService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void saveNewEntry(JournalEntry entry, String userName) {
        try {
            User userInDb = userRepository.findByUserName(userName);
            if (userInDb != null) {
                entry.setDate(LocalDateTime.now());
                journalEntryRepository.save(entry);
                userInDb.getJournalEntries().add(entry);
                userRepository.save(userInDb);
                return;
            }
            throw new RuntimeException("User not found.");
        } catch (Exception e) {
            log.info("User not found with username: {}", userName, e);
            throw new RuntimeException(e);
        }
    }

    public List<JournalEntry> getAllJournalsOfUser(String userName) {
        try {
            User userInDb = userRepository.findByUserName(userName);
            List<JournalEntry> journalEntries = userInDb.getJournalEntries();
            if (journalEntries != null && !journalEntries.isEmpty()) {
                return journalEntries;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateJournalEntry(JournalEntry entry, String userName) {
        try {
            User user = userRepository.findByUserName(userName);
            user.getJournalEntries().stream()
                    .filter(j -> j.getId().equals(entry.getId()))
                    .findFirst()
                    .ifPresentOrElse(
                            existingEntry -> {
                                // Apply partial updates (only non-null/empty fields)
                                if (entry.getTitle() != null && !entry.getTitle().isEmpty()) {
                                    existingEntry.setTitle(entry.getTitle());
                                }
                                if (entry.getContent() != null && !entry.getContent().isEmpty()) {
                                    existingEntry.setContent(entry.getContent());
                                }
                                journalEntryRepository.save(existingEntry);
                            },
                            () -> {
                                throw new RuntimeException("Journal entry not found with id: " + entry.getId());
                            }
                    );
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteJournalById(ObjectId idToDelete, String userName) {
        try {
            User userInDb = userRepository.findByUserName(userName);
            boolean removed = userInDb.getJournalEntries().removeIf(x -> x.getId().equals(idToDelete));
            if (removed) {
                journalEntryRepository.deleteById(idToDelete);
                userRepository.save(userInDb);
                return;
            }
            throw new RuntimeException("Not found!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Transactional
    public void deleteAllJournalsOfUser(String userName) {
        try {
            User userInDb = userRepository.findByUserName(userName);
            List<ObjectId> list = userInDb.getJournalEntries().stream().map(JournalEntry::getId).toList();
            journalEntryRepository.deleteAllById(list);
            userInDb.getJournalEntries().removeAll(userInDb.getJournalEntries());
            userRepository.save(userInDb);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
