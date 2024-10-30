package com.sigei.hostel_management_system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomDTO {

    private Long id;
    private int roomNumber;
    private String roomType;
    private String roomStatus;
    private List<BookingDTO> bookings;
}





