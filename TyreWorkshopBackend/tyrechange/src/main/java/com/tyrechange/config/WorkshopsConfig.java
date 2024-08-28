package com.tyrechange.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.tyrechange.model.TyreWorkshop;

@Configuration
@ConfigurationProperties(prefix = "app")
public class WorkshopsConfig {
    private List<TyreWorkshop> workshops;

    public List<TyreWorkshop> getWorkshops() {
        return workshops;
    }

    public void setWorkshops(List<TyreWorkshop> workshops) {
        this.workshops = workshops;
    }

}