package com.sigei.hostel_management_system.dblayer.repository;

import com.sigei.hostel_management_system.dblayer.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepo extends JpaRepository<Room, Long> {

    @Query("SELECT DISTINCT r.roomType FROM Room r")
    List<String> findDistinctRoomTypes();


//    @Query("SELECT r FROM Room r WHERE r.roomType LIKE %:roomType% AND r.id NOT IN (SELECT bk.room.id FROM Booking bk WHERE" +
//            "(bk.startDate <= :endDate) AND (bk.endDate >= :startDate))")
//    List<Room> findAvailableRoomsByDatesAndRoomTypes(LocalDate startDate, LocalDate endDate, String roomType);

    @Query("SELECT r FROM Room r WHERE r.roomType LIKE %:roomType% AND r.id NOT IN (SELECT bk.room.id FROM Booking bk WHERE" +
            "(bk.startDate <= :endDate) AND (bk.endDate >= :startDate))")
    List<Room> findAvailableRoomsByDatesAndTypes(LocalDate startDate, LocalDate endDate, String roomType);


    @Query("SELECT r FROM Room r WHERE r.id NOT IN (SELECT b.room.id FROM Booking b)")
    List<Room> getAllAvailableRooms();
    
}



