package com.awashbank.supply_chain.user.model;

import lombok.Data;

@Data
public class RegistrationModel {
    private String name;
    private String Designation;
    private String phone;
    private String room;
    private String dob;
    private GENDER gender;

    public enum GENDER{
        MALE,
        FEMALE
    }

}
