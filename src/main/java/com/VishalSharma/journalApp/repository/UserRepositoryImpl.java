package com.VishalSharma.journalApp.repository;

import com.VishalSharma.journalApp.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserRepositoryImpl {

    private final MongoTemplate mongoTemplate;

    public UserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public List<User> findUserWithSA() {
        try {
//        returns list of users which optedIn for sentimentalAnalysis and provides their valid mail id
//        create a query object
            Query query = new Query();

            query.addCriteria(Criteria.where("email").regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"));
            query.addCriteria(Criteria.where("sentimentAnalysis").is(true));

            return mongoTemplate.find(query, User.class);
        } catch (Exception e) {
            log.info("Some error occurred while finding users opted for sentiment analysis and provided correct email", e);
            throw new RuntimeException(e);
        }
    }


}
