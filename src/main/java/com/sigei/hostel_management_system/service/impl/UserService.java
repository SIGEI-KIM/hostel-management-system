package com.sigei.hostel_management_system.service.impl;

import com.sigei.hostel_management_system.dblayer.entity.User;
import com.sigei.hostel_management_system.dblayer.repository.UserRepo;
import com.sigei.hostel_management_system.dto.LoginRequest;
import com.sigei.hostel_management_system.dto.Response;
import com.sigei.hostel_management_system.dto.UserDTO;
import com.sigei.hostel_management_system.exception.OurException;
import com.sigei.hostel_management_system.service.interfac.IUserService;
import com.sigei.hostel_management_system.utils.JWTUtils;
import com.sigei.hostel_management_system.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public Response register(User user) {
        Response response = new Response();
        try {

            if (user.getRole() == null || user.getRole().isBlank()) {
                user.setRole("user");
            }

            if (userRepo.existsByEmail(user.getEmail())) {
                throw new OurException(user.getEmail() + " is already registered");
            }

           user.setPassword(passwordEncoder.encode(user.getPassword()));

            User savedUser = userRepo.save(user);

            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);

            String message = "user registered successfully";
            if ("admin".equalsIgnoreCase(savedUser.getRole())) {
                message = "Admin registered successfully";
            }

            response.setStatusCode(200);
            response.setMessage(message);
            response.setUser(userDTO);

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Occurred During Registration: " + e.getMessage());
        }

        return response;
    }


    @Override
    public Response login(LoginRequest loginRequest) {
        Response response = new Response();
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var user = userRepo.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new OurException(loginRequest.getEmail() + " is not registered"));

            var token = jwtUtils.generateToken(user);
            response.setStatusCode(200);
            response.setToken(token);
            response.setRole(user.getRole());
            response.setExpirationTime("24 Hours");
            response.setMessage("login successfully");
        }catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {

            response.setStatusCode(500);
            response.setMessage("Error Occurred During User Login " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllUsers() {
        Response response = new Response();
        try{
            List<User> usersList = userRepo.findAll();
            List<UserDTO> userDTOList = Utils.mapUserListEntityToUserListDTO(usersList);
            response.setStatusCode(200);
            response.setMessage("successfully");
            response.setUserList(userDTOList);
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserBookingHistory(String userId) {
        Response response = new Response();
        try{
            User user = userRepo.findById(Long.valueOf(userId)).orElseThrow(() -> new OurException(userId + " is not registered"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTOPlusUserBookingsAndRoom(user);
            response.setStatusCode(200);
            response.setMessage("successfully");
            response.setUser(userDTO);
        }catch (Exception e) {

            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteUser(String userId) {
        Response response = new Response();

        try {
            userRepo.findById(Long.valueOf(userId)).orElseThrow(() -> new OurException("User " + userId + " is not registered"));
            userRepo.deleteById(Long.valueOf(userId));
            response.setStatusCode(200);
            response.setMessage("User deleted successfully");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {

            response.setStatusCode(500);
            response.setMessage("Error Occurred During User Deletion " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserById(String userId) {
        Response response = new Response();
        try{
            User user = userRepo.findById(Long.valueOf(userId)).orElseThrow(() -> new OurException("User " + userId + " is not registered"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("successfully");
            response.setUser(userDTO);
        }catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {

            response.setStatusCode(500);
            response.setMessage("Error Occured Getting User By Id " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getMyInfo(String email) {
        Response response = new Response();
        try{
            User user = userRepo.findByEmail(email).orElseThrow(() -> new OurException("User " + email + " is not registered"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("successfully");
            response.setUser(userDTO);
        }catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {

            response.setStatusCode(500);
            response.setMessage("Error Occured Getting User By Email " + e.getMessage());
        }
        return response;
    }
}
