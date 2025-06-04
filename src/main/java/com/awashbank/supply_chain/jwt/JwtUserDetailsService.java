package com.awashbank.supply_chain.jwt;

import com.awashbank.supply_chain.user.model.UserDetailModel;
import com.awashbank.supply_chain.user.model.UserDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(JwtUserDetailsService.class);
    private UserDetailRepository usr;

    public JwtUserDetailsService(UserDetailRepository usr) {
        this.usr = usr;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDetailModel user = usr.byUser(username);
        if (user == null) {
           log.error("User not found with username: " + username);
           //row new UsernameNotFoundException("User not found with username: " + username);
           return new org.springframework.security.core.userdetails.User("user.getUsername()"," user.getPassword()",
                    new ArrayList<>());
        }else {
            return new org.springframework.security.core.userdetails.User(user.getUsername(), "",
                    getAuthority(user.getUsername()));
        }
    }

    private Set<SimpleGrantedAuthority> getAuthority(String username) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        return authorities;
    }
}
