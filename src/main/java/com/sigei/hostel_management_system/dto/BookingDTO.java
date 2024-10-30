package com.sigei.hostel_management_system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDTO {
    private Long id;

    @NotNull(message = "start date is required")
    private LocalDate startDate;
    @NotNull(message = "end date is required")
    private LocalDate endDate;
    private String bookingStatus;
    private String bookingConfirmationCode;
    private UserDTO user;
    private RoomDTO room;
}
