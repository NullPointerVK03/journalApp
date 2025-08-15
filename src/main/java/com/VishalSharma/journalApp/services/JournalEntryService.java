package com.VishalSharma.journalApp.services;

import com.VishalSharma.journalApp.entity.JournalEntry;
import com.VishalSharma.journalApp.entity.User;
import com.VishalSharma.journalApp.repository.JournalEntryRepository;
import com.VishalSharma.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class JournalEntryService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void saveNewEntry(JournalEntry entry, String userName) {
        try {
            log.info("Finding user in DB with userName: {}, to save new JournalEntry", userName);
            User userInDb = userRepository.findByUserName(userName);
            if (userInDb != null) {
                log.info("User with userName: {}, found in DB.", userName);
                log.info("Initializing Date field in JournalEntry for userName: {} ", userName);
                entry.setDate(LocalDateTime.now());
                log.info("Saving the JournalEntry in DB");
                journalEntryRepository.save(entry);
                log.info("Adding JournalEntry with jId: {} in the JournalEntries field for userName:{}", entry.getId(), userName);
                userInDb.getJournalEntries().add(entry);
                log.info("Saving the User with added JournalEntries field for userName: {}", userName);
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
            log.info("Finding user in DB with userName: {}, to get all JournalEntries of it.", userName);
            User userInDb = userRepository.findByUserName(userName);

            List<JournalEntry> journalEntries = userInDb.getJournalEntries();
            if (journalEntries != null && !journalEntries.isEmpty()) {
                log.info("JournalEntries found for userName: {}, returning it as response.", userName);
                return journalEntries;
            }
            log.info("Either JournalEntries of userName: {}, is null or empty. Returning empty list as response.", userName);
            return new ArrayList<>();
        } catch (Exception e) {
            log.warn("Somthing went wrong while fetching all JournalEntries. Exception: ", e);
            throw new RuntimeException(e);
        }
    }

    public void updateJournalEntry(ObjectId jId, JournalEntry entry, String userName) {
        try {
            log.info("Finding user in DB with userName: {}, to update JournalEntry with jId: {}.", userName, jId);
            User user = userRepository.findByUserName(userName);
            user.getJournalEntries().stream()
                    .filter(j -> j.getId().equals(jId))
                    .findFirst()
                    .ifPresentOrElse(
                            existingEntry -> {
                                if ((entry.getContent() == null && entry.getContent().isEmpty())
                                        && (entry.getTitle() == null && entry.getTitle().isEmpty())) {
                                    log.info("Exception: Body of given journalEntry is null. username: {}, jId: {}, can't update JournalEntry.", userName, jId);
                                    throw new RuntimeException("JournalEntry is null");
                                }

                                // Apply partial updates (only non-null/empty fields)
                                if (entry.getTitle() != null && !entry.getTitle().isEmpty()) {
                                    existingEntry.setTitle(entry.getTitle());
                                }
                                if (entry.getContent() != null && !entry.getContent().isEmpty()) {
                                    existingEntry.setContent(entry.getContent());
                                }
                                log.info("JournalEntry updated with new content.");
                                journalEntryRepository.save(existingEntry);
                            },
                            () -> {
                                log.info("Exception: JournalEntry for {} with jId: {}, not found", userName, jId);
                                throw new RuntimeException("Journal entry not found with id: " + entry.getId());
                            }
                    );
        } catch (RuntimeException e) {
            log.info("Some unexpected error occurred while updating journalEntry by jId: {}. for user with userName: {} Exception: ", jId, userName, e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteJournalById(ObjectId jId, String userName) {
        try {
            log.info("Finding user in DB with userName: {}, to delete JournalEntry with jId: {}", jId, userName);
            User userInDb = userRepository.findByUserName(userName);
            boolean removed = userInDb.getJournalEntries().removeIf(x -> x.getId().equals(jId));
            if (removed) {
                log.info("JournalEntry with jId: {}, removed for userName: {}", jId, userName);
                journalEntryRepository.deleteById(jId);
                userRepository.save(userInDb);
                return;
            }
            throw new RuntimeException("Not found!");
        } catch (Exception e) {
            log.info("Maybe JournalEntry with jId: {} not found or does not belong to user with userName: {}", jId, userName);
            log.warn("Something went wrong while deleting JournalEntry jId: {}, Exception: ", jId, e);
            throw new RuntimeException(e);
        }

    }

    @Transactional
    public void deleteAllJournalsOfUser(String userName) {
        try {
            log.info("Finding user in DB with userName: {}, to delete all of it's JournalEntries", userName);
            User userInDb = userRepository.findByUserName(userName);
            log.info("Creating a List of ObjectId to store all the ObjectId of JournalEntries for user with userName: {}", userName);
            List<ObjectId> list = userInDb.getJournalEntries().stream().map(JournalEntry::getId).toList();
            log.info("Deleting all JournalEntries with ObjectId for userName: {} from DB", userName);
            journalEntryRepository.deleteAllById(list);
            log.info("Removing all JournalEntries of user with userName: {} from it's Journalentries field.", userName);
            userInDb.getJournalEntries().removeAll(userInDb.getJournalEntries());
            log.info("Saving the updated user with no JournalEntries in DB");
            userRepository.save(userInDb);
        } catch (Exception e) {
            log.warn("Something went wrong while removing all JournalEntries of user with userName: {}", userName);
            throw new RuntimeException(e);
        }
    }
}
