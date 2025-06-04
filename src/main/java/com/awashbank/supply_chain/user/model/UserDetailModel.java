package com.awashbank.supply_chain.user.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_detail")
//@Table(name = "bnpl_users")
@NamedQuery(name = "UserDetailModel.byUser", query = "SELECT u FROM UserDetailModel u WHERE u.username=?1")
@NamedQuery(name = "UserDetailModel.byRoom", query = "SELECT u FROM UserDetailModel u WHERE u.room=?1 and u.status = 1")
@NamedQuery(name = "UserDetailModel.getAllUser", query = "SELECT u FROM UserDetailModel u WHERE u.status = 1")
@NamedQuery(name = "UserDetailModel.getSearchedUser", query = "SELECT u FROM UserDetailModel u WHERE u.username != ?1 and u.username like ?2 and u.status = 1")
@NamedQuery(name = "UserDetailModel.bannedUser", query = "SELECT u FROM UserDetailModel u WHERE u.status = 0")
public class UserDetailModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "room")
    private String room;

    @Column(name = "dob")
    private String dob;

    private String password;
    private String role;
    private String branch;
    private String region;

    @Column(name = "gender")
    private String gender;

    @Column(name = "designation")
    private String designation;

    @Column(name = "create_date")
    private String create_date;

    @Column(name = "status", nullable = false)
    private Integer status;
}
