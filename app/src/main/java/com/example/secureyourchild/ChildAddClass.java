package com.example.secureyourchild;

public class ChildAddClass {
    String childID,childName,childPhone,childAge,parentId;

    public ChildAddClass(String childID, String childName, String childPhone, String childAge, String parentId) {
        this.childID = childID;
        this.childName = childName;
        this.childPhone = childPhone;
        this.childAge = childAge;
        this.parentId = parentId;
    }

    public ChildAddClass() {
    }

    public String getChildID() {
        return childID;
    }

    public void setChildID(String childID) {
        this.childID = childID;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChildPhone() {
        return childPhone;
    }

    public void setChildPhone(String childPhone) {
        this.childPhone = childPhone;
    }

    public String getChildAge() {
        return childAge;
    }

    public void setChildAge(String childAge) {
        this.childAge = childAge;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
