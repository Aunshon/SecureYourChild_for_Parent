package com.example.secureyourchild;

public class StationInfo {
    String add = null;
    String city = null;
    String state= null;
    String country= null;
    String postalCode= null;
    double lat=0;
    double lon=0;
    String uploadId= null;

    public StationInfo(String add, String city, String state, String country, String postalCode, double lat, double lon, String uploadId) {
        this.add = add;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.uploadId = uploadId;
        this.lat=lat;
        this.lon=lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public StationInfo() {
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
}
