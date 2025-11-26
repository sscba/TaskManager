package com.taskmanager.taskmanagerapp.auth.security;

import com.taskmanager.taskmanagerapp.entity.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;


public class CustomUserDetails extends User {

    private final UserDetails userDetails;

    public CustomUserDetails(UserDetails userDetails){
        super(userDetails.getUsername(), userDetails.getPassword(), userDetails.isEnable(),
                true,true,true,getAuthorities(userDetails.getRole()));
        this.userDetails = userDetails;
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(String role){
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+ role));
    }

    public UserDetails getUserDetails(){
        return userDetails;
    }
}
