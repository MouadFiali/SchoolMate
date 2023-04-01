package com.manager.schoolmateapi.users.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.manager.schoolmateapi.users.UserRepository;
import com.manager.schoolmateapi.users.models.MyUserDetails;

@Service
public class MyUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Service email: " + email);
        return userRepository
                .findByEmail(email)
                .map(MyUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + email));
    }


}
