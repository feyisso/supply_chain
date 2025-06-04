package com.awashbank.supply_chain.user.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDetailRepository extends JpaRepository<UserDetailModel,Long> {
    public UserDetailModel byUser(String username);
    /*public List<UserDetailModel> byRoom(String room);

    public List<UserDetailModel> bannedUser();
    List<UserDetailModel> getAllUser();
    List<UserDetailModel> getSearchedUser(String username,String search);*/
}
