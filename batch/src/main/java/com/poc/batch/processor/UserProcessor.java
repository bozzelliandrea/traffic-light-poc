package com.poc.batch.processor;

import com.poc.batch.domain.User;
import com.poc.batch.model.UserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class UserProcessor implements ItemProcessor<UserDetail, User> {

    private static final Logger log = LoggerFactory.getLogger(UserProcessor.class);

    @Override
    public User process(UserDetail item) throws Exception {
        log.info("processing user data.....{}", item);

        User transformedUser = new User();
        transformedUser.setEmail(item.getEmail());
        transformedUser.setFirstName(item.getFirstName());
        transformedUser.setLastName(item.getLastName());
        transformedUser.setMobileNumber(item.getMobileNumber());
        return transformedUser;
    }
}
