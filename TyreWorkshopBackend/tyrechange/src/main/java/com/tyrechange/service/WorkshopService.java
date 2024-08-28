package com.tyrechange.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tyrechange.config.WorkshopsConfig;
import com.tyrechange.model.TyreWorkshop;

@Service
public class WorkshopService {
    private static final Logger logger = LoggerFactory.getLogger(WorkshopService.class);
    private final WorkshopsConfig workshopsConfig;

    @Autowired
    public WorkshopService(WorkshopsConfig workshopsConfig) {
        this.workshopsConfig = workshopsConfig;
        logger.debug("WorkshopService initialized with config: {}", workshopsConfig);
    }

    public List<TyreWorkshop> getAllWorkshops() {
        List<TyreWorkshop> workshops = workshopsConfig.getWorkshops();
        logger.debug("Fetched workshops from config: {}", workshops);
        if (workshops == null || workshops.isEmpty()) {
            logger.error("No workshops found in configuration");
            throw new RuntimeException("No workshops found in configuration");
        }
        return workshops;
    }

    public TyreWorkshop getWorkshopByName(String name) {
        return getAllWorkshops().stream()
        .filter(workshop -> workshop.getName().equalsIgnoreCase(name))
        .findFirst()
        .orElseThrow(() -> {
            logger.error("Workshop not found: {}", name);
            return new IllegalArgumentException("Workshop not found: " + name);
        });
}
}