package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        // Calculate the total amount
        Integer noOfScreensRequired = subscriptionEntryDto.getNoOfScreensRequired();
        Integer baseAmount = 0;
        Integer perScreenCost = 0;

        switch (subscriptionEntryDto.getSubscriptionType()) {
            case BASIC:
                baseAmount = 500;
                perScreenCost = 200;
                break;
            case PRO:
                baseAmount = 800;
                perScreenCost = 250;
                break;
            case ELITE:
                baseAmount = 1000;
                perScreenCost = 350;
                break;
            default:
                break;
        }

        Integer totalAmount = baseAmount + (perScreenCost * noOfScreensRequired);

        // Get user info
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        // Set subscription parameters
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(noOfScreensRequired);
        subscription.setUser(user);
        subscription.setTotalAmountPaid(totalAmount);

        // Set User FK
        user.setSubscription(subscription);

        return totalAmount;
    }

    public Integer upgradeSubscription(Integer userId) throws Exception {
        // If you are already at an ELITE subscription: throw Exception ("Already the best Subscription")
        // In all other cases, try to upgrade the subscription and calculate the price difference that the user has to pay
        // Update the subscription in the repository

        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));

        if (user.getSubscription().getSubscriptionType() == SubscriptionType.ELITE) {
            throw new Exception("Already the best Subscription");
        }

        Subscription subscription = user.getSubscription();
        Integer previousFair = subscription.getTotalAmountPaid();
        Integer currFair;

        if (subscription.getSubscriptionType() == SubscriptionType.BASIC) {
            subscription.setSubscriptionType(SubscriptionType.PRO);
            currFair = previousFair + 300 + (50 * subscription.getNoOfScreensSubscribed());
        } else {
            if (subscription.getSubscriptionType() == SubscriptionType.ELITE) {
                throw new Exception("Already the best Subscription");
            }
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            currFair = previousFair + 200 + (100 * subscription.getNoOfScreensSubscribed());
        }

        subscription.setTotalAmountPaid(currFair);
        user.setSubscription(subscription);
        subscriptionRepository.save(subscription);

        return currFair - previousFair;
    }

    public Integer calculateTotalRevenueOfHotstar() {
        // We need to find out the total revenue of Hotstar from all the subscriptions combined
        // Hint is to use the findAll function from the SubscriptionDb

        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        Integer totalRevenue = 0;

        for (Subscription subscription : subscriptionList) {
            totalRevenue += subscription.getTotalAmountPaid();
        }
        return totalRevenue;
    }


}