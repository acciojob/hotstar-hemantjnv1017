package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto) throws Exception {
        // Add a webSeries to the database and update the ratings of the productionHouse
        // If the seriesName is already present in the database, throw an Exception ("Series is already present")
        // Use a function written in the Repository Layer for the same
        // Don't forget to save the production and webseries Repository

        WebSeries existingWebSeries = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
        if (existingWebSeries != null) {
            throw new Exception("Series is already present");
        }

        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId())
                .orElseThrow(() -> new IllegalArgumentException("Production house not found"));

        WebSeries newWebSeries = new WebSeries(
                webSeriesEntryDto.getSeriesName(),
                webSeriesEntryDto.getAgeLimit(),
                webSeriesEntryDto.getRating(),
                webSeriesEntryDto.getSubscriptionType()
        );
        newWebSeries.setProductionHouse(productionHouse);
        newWebSeries = webSeriesRepository.save(newWebSeries);

        productionHouse.setRatings(webSeriesEntryDto.getRating());
        productionHouse.getWebSeriesList().add(newWebSeries);
        productionHouseRepository.save(productionHouse);

        return newWebSeries.getId();
    }
}