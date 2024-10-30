package com.sigei.hostel_management_system.service.impl;

import com.sigei.hostel_management_system.dblayer.entity.Room;
import com.sigei.hostel_management_system.dblayer.repository.BookingRepo;
import com.sigei.hostel_management_system.dblayer.repository.RoomRepo;
import com.sigei.hostel_management_system.dto.Response;
import com.sigei.hostel_management_system.dto.RoomDTO;
import com.sigei.hostel_management_system.exception.OurException;
import com.sigei.hostel_management_system.service.interfac.IRoomService;
import com.sigei.hostel_management_system.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService implements IRoomService {

    private final RoomRepo roomRepo;
    private final BookingRepo bookingRepo;

    @Override
    public Response addNewRoom(String roomStatus, Integer roomNumber, String roomType) {
        Response response = new Response();

        try {
            Room room = new Room();
            room.setRoomNumber(roomNumber);
            room.setRoomStatus(roomStatus);
            room.setRoomType(roomType);
            Room savedRoom = roomRepo.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoom(roomDTO);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error adding a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepo.findDistinctRoomTypes();
    }

    @Override
    public Response getAllRooms() {
        Response response = new Response();

        try {
            List<Room> roomList = roomRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Rooms found");
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateRoom(Long roomId, String roomStatus, String roomType, Integer roomNumber) {
        Response response = new Response();
        try{
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new OurException("Room not found"));
            if (roomNumber != null) room.setRoomNumber(roomNumber);
            if (roomType != null) room.setRoomType(roomType);
            if (roomStatus != null) room.setRoomStatus(roomStatus);

            Room updatedRoom = roomRepo.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoom(roomDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getRoomById(Long roomId) {
        Response response = new Response();

        try {
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new OurException("Room not found"));
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTOPlusBookings(room);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoom(roomDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteRoom(Long roomId) {
        Response response = new Response();

        try {
            roomRepo.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            roomRepo.deleteById(roomId);
            response.setStatusCode(200);
            response.setMessage("Room successfully deleted");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting a room " + e.getMessage());
        }
        return response;
    }

        @Override
    public Response getAvailableRoomsByDataAndRoomType(LocalDate startDate, LocalDate endDate, String roomType) {
        Response response = new Response();

        try {
            List<Room> availableRooms = roomRepo.findAvailableRoomsByDatesAndTypes(startDate, endDate, roomType);
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(availableRooms);
            response.setStatusCode(200);
            response.setMessage("Room available");
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting the available room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllAvailableRooms() {
        Response response = new Response();

        try {
            List<Room> roomList = roomRepo.getAllAvailableRooms();
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Room available");
            response.setRoomList(roomDTOList);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting available the rooms " + e.getMessage());
        }
        return response;
    }
}





