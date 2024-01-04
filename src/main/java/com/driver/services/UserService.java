package com.driver.services;

import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;

    public Integer addUser(User user){
        // Simply add the user to the database and return the userId returned by the repository
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){
        // Return the count of all webSeries that a user can watch based on their ageLimit and subscriptionType
        // Hint: Retrieve all the Webseries from the WebSeriesRepository

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Integer userAge = user.getAge();

        if (userAge > 18) {
            userAge = Integer.MAX_VALUE;
        } else {
            userAge = 18;
        }

        List<WebSeries> webSeriesList = webSeriesRepository.findAll();

        Integer count = 0;

        for (WebSeries webSeries : webSeriesList) {
            if (webSeries.getAgeLimit() <= userAge) {
                count++;
            }
        }

        return count;
    }
}