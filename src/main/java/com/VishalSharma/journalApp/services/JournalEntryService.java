package com.VishalSharma.journalApp.services;

import com.VishalSharma.journalApp.dto.JournalEntryDTO;
import com.VishalSharma.journalApp.entity.JournalEntry;
import com.VishalSharma.journalApp.entity.User;
import com.VishalSharma.journalApp.repository.JournalEntryRepository;
import com.VishalSharma.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final UserRepository userRepository;

    public JournalEntryService(JournalEntryRepository journalEntryRepository, UserRepository userRepository) {
        this.journalEntryRepository = journalEntryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveNewEntry(JournalEntryDTO entry, String userName) {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            log.warn("User not found with username: {}", userName);
            throw new RuntimeException("User not found: " + userName);
        }

        log.info("Creating new JournalEntry for user={}", userName);
        JournalEntry newEntry = new JournalEntry();
        newEntry.setTitle(entry.getTitle());
        newEntry.setContent(entry.getContent());
        newEntry.setSentiment(entry.getSentiment());
        newEntry.setDate(LocalDateTime.now());

        journalEntryRepository.save(newEntry);

        user.getJournalEntries().add(newEntry);
        userRepository.save(user);

        log.info("JournalEntry saved with id={} for user={}", newEntry.getId(), userName);
    }

    public List<JournalEntry> getAllJournalsOfUser(String userName) {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            log.warn("User not found while fetching journals, username={}", userName);
            throw new RuntimeException("User not found: " + userName);
        }

        List<JournalEntry> journalEntries = user.getJournalEntries();
        if (journalEntries == null || journalEntries.isEmpty()) {
            log.info("No JournalEntries found for user={}", userName);
            return new ArrayList<>();
        }

        log.info("Returning {} JournalEntries for user={}", journalEntries.size(), userName);
        return journalEntries;
    }

    public void updateJournalEntry(ObjectId jId, JournalEntryDTO entry, String userName) {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            log.warn("User not found while updating journal, username={}", userName);
            throw new RuntimeException("User not found: " + userName);
        }

        user.getJournalEntries().stream()
                .filter(j -> j.getId().equals(jId))
                .findFirst()
                .ifPresentOrElse(
                        existingEntry -> {
                            if ((entry.getTitle() == null || entry.getTitle().isEmpty()) &&
                                    (entry.getContent() == null || entry.getContent().isEmpty())) {
                                log.warn("Empty update request for journal id={} user={}", jId, userName);
                                throw new RuntimeException("JournalEntry update payload is empty");
                            }

                            if (entry.getTitle() != null && !entry.getTitle().isEmpty()) {
                                existingEntry.setTitle(entry.getTitle());
                            }
                            if (entry.getContent() != null && !entry.getContent().isEmpty()) {
                                existingEntry.setContent(entry.getContent());
                            }

                            journalEntryRepository.save(existingEntry);
                            log.info("Updated JournalEntry id={} for user={}", jId, userName);
                        },
                        () -> {
                            log.warn("JournalEntry not found for user={} with id={}", userName, jId);
                            throw new RuntimeException("JournalEntry not found: " + jId);
                        }
                );
    }

    @Transactional
    public void deleteJournalById(ObjectId jId, String userName) {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            log.warn("User not found while deleting journal, username={}", userName);
            throw new RuntimeException("User not found: " + userName);
        }

        boolean removed = user.getJournalEntries().removeIf(x -> x.getId().equals(jId));
        if (!removed) {
            log.warn("JournalEntry not found for user={} with id={}", userName, jId);
            throw new RuntimeException("JournalEntry not found: " + jId);
        }

        journalEntryRepository.deleteById(jId);
        userRepository.save(user);
        log.info("Deleted JournalEntry id={} for user={}", jId, userName);
    }

    @Transactional
    public void deleteAllJournalsOfUser(String userName) {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            log.warn("User not found while deleting all journals, username={}", userName);
            throw new RuntimeException("User not found: " + userName);
        }

        List<ObjectId> ids = user.getJournalEntries().stream()
                .map(JournalEntry::getId)
                .toList();

        if (!ids.isEmpty()) {
            journalEntryRepository.deleteAllById(ids);
            user.getJournalEntries().clear();
            userRepository.save(user);
            log.info("Deleted {} JournalEntries for user={}", ids.size(), userName);
        } else {
            log.info("No JournalEntries found to delete for user={}", userName);
        }
    }
}
