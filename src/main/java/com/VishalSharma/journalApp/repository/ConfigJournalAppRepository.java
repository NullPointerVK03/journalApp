package com.VishalSharma.journalApp.repository;

import com.VishalSharma.journalApp.entity.ConfigJournalApp;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigJournalAppRepository extends MongoRepository<ConfigJournalApp, ObjectId> {
}
