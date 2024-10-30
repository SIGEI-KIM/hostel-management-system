package com.sigei.hostel_management_system.service;

import com.sigei.hostel_management_system.dblayer.repository.UserRepo;
import com.sigei.hostel_management_system.exception.OurException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Attempting to load user by username/email: " + username);
        return userRepo.findByEmail(username)
                .orElseThrow(() -> {
                    System.out.println("User not found: " + username);
                    return new OurException("Username/Email not Found");
                });
    }
}

