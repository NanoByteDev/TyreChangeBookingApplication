package com.tyrechange.model;

import java.util.List;

public class TyreWorkshop {
    private String name;
    private String address;
    private List<String> vehicleTypes;
    private String apiUrl;
    private String apiVersion;
    private boolean xmlApi;
    
   // Getters and setters
   public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public String getAddress() {
    return address;
}

public void setAddress(String address) {
    this.address = address;
}

public List<String> getVehicleTypes() {
    return vehicleTypes;
}

public void setVehicleTypes(List<String> vehicleTypes) {
    this.vehicleTypes = vehicleTypes;
}

public String getApiUrl() {
    return apiUrl;
}

public void setApiUrl(String apiUrl) {
    this.apiUrl = apiUrl;
}

public String getApiVersion() {
    return apiVersion;
}

public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
}

public boolean isXmlApi() {
    return xmlApi;
}

public void setXmlApi(boolean xmlApi) {
    this.xmlApi = xmlApi;
}

    @Override
    public String toString() {
        return "TyreWorkshop{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", vehicleTypes=" + vehicleTypes +
                ", apiUrl='" + apiUrl + '\'' +
                ", apiVersion='" + apiVersion + '\'' +
                ", xmlApi=" + xmlApi +
                '}';
    }
}
