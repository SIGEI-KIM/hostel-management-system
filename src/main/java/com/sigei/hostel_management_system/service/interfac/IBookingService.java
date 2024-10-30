package com.sigei.hostel_management_system.service.interfac;

import com.sigei.hostel_management_system.dblayer.entity.Booking;
import com.sigei.hostel_management_system.dto.Response;

public interface IBookingService {
    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);

    Response findBookingByConfirmationCode(String confirmationCode);

    Response getAllBookings();

    Response cancelBooking(Long bookingId);
}
