package com.awashbank.supply_chain.user.model;

public class UserDetailModelDTO {
    private Integer id;
    private String username;
    private String type;
    private String name;
    private String phone;
    private String room;
    private String dob;
    private String gender;
    private String designation;
    private String create_date;
    private String created_by;
    private Integer status;
    private Double cardValue;

    public UserDetailModelDTO(Integer id, String username, String type, String name, String phone, String room, String dob, String gender, String designation, String create_date, String created_by, Integer status, Double cardValue) {
        this.id = id;
        this.username = username;
        this.type = type;
        this.name = name;
        this.phone = phone;
        this.room = room;
        this.dob = dob;
        this.gender = gender;
        this.designation = designation;
        this.create_date = create_date;
        this.created_by = created_by;
        this.status = status;
        this.cardValue = cardValue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getCardValue() {
        return cardValue;
    }

    public void setCardValue(Double cardValue) {
        this.cardValue = cardValue;
    }
}

