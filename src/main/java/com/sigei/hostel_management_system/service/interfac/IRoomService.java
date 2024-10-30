package com.sigei.hostel_management_system.service.interfac;

import com.sigei.hostel_management_system.dto.Response;

import java.time.LocalDate;
import java.util.List;

public interface IRoomService {
    Response addNewRoom(String roomStatus, Integer roomNumber, String roomType);
    List<String> getAllRoomTypes();
    Response getAllRooms();
    Response updateRoom(Long roomId, String roomStatus, String roomType, Integer roomNumber);
    Response getRoomById(Long roomId);
    Response deleteRoom(Long roomId);
    Response getAvailableRoomsByDataAndRoomType(LocalDate startDate, LocalDate endDate, String roomTYpe);
    Response getAllAvailableRooms();
}


