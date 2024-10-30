package com.sigei.hostel_management_system.service.interfac;

import com.sigei.hostel_management_system.dblayer.entity.User;
import com.sigei.hostel_management_system.dto.LoginRequest;
import com.sigei.hostel_management_system.dto.Response;

public interface IUserService {
    Response register(User user);

    Response login(LoginRequest loginRequest);

    Response getAllUsers();

    Response getUserBookingHistory(String userId);

    Response deleteUser(String userId);

    Response getUserById(String userId);

    Response getMyInfo(String email);
}
