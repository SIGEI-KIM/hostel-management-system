package com.sigei.hostel_management_system.service.impl;

import com.sigei.hostel_management_system.dblayer.entity.Booking;
import com.sigei.hostel_management_system.dblayer.entity.Room;
import com.sigei.hostel_management_system.dblayer.entity.User;
import com.sigei.hostel_management_system.dblayer.repository.BookingRepo;
import com.sigei.hostel_management_system.dblayer.repository.RoomRepo;
import com.sigei.hostel_management_system.dblayer.repository.UserRepo;
import com.sigei.hostel_management_system.dto.BookingDTO;
import com.sigei.hostel_management_system.dto.Response;
import com.sigei.hostel_management_system.exception.OurException;
import com.sigei.hostel_management_system.service.interfac.IBookingService;
import com.sigei.hostel_management_system.service.interfac.IRoomService;
import com.sigei.hostel_management_system.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {
    private final BookingRepo bookingRepo;
    private final IRoomService roomService;
    private final RoomRepo roomRepo;
    private final UserRepo userRepo;

    @Override
    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        Response response = new Response();
        try {
            // Ensure startDate and endDate are not null
            if (bookingRequest.getStartDate() == null || bookingRequest.getEndDate() == null) {
                throw new IllegalArgumentException("Start date and end date must not be null");
            }

            // Validate that endDate is not before startDate
            if (bookingRequest.getEndDate().isBefore(bookingRequest.getStartDate())) {
                throw new IllegalArgumentException("End date cannot be before start date");
            }

            // Fetch room and user from the repository
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new OurException("Room not found"));
            User user = userRepo.findById(userId).orElseThrow(() -> new OurException("User not found"));

            // Check if room is available for the given dates
            List<Booking> existingBookings = room.getBookings();
            if (!roomIsAvailable(bookingRequest, existingBookings)) {
                throw new OurException("Room is not available for the selected date range");
            }

            // Now construct the booking object, ensure endDate is properly assigned
            Booking booking = new Booking();
            booking.setStartDate(bookingRequest.getStartDate());
            booking.setEndDate(bookingRequest.getEndDate());
            booking.setUser(user);
            booking.setRoom(room);

            // Generate a booking confirmation code
            String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);
            booking.setBookingConfirmationCode(bookingConfirmationCode);

            // Save the booking to the repository
            bookingRepo.save(booking);

            // Respond with success
            response.setStatusCode(200);
            response.setMessage("Booking successfully saved");
            response.setBookingConfirmationCode(bookingConfirmationCode);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Saving a booking: " + e.getMessage());
        }
        System.out.println("saving the booking:" + response);
        return response;
    }


//    @Override
//    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
//        Response response = new Response();
//        try{
//            if(bookingRequest.getEndDate().isBefore(bookingRequest.getStartDate())){
//                throw new IllegalArgumentException("End date cannot be before start date");
//            }
//            Room room = roomRepo.findById(roomId).orElseThrow(() -> new OurException("Room not found"));
//            User user = userRepo.findById(userId).orElseThrow(() -> new OurException("User not found"));
//
//            List<Booking> existingBookings = room.getBookings();
//
//            if (!roomIsAvailable(bookingRequest, existingBookings)){
//                throw new OurException("Room is not available for the selected date range");
//            }
//            bookingRequest.setRoom(room);
//            bookingRequest.setUser(user);
//            String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);
//            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
//            bookingRepo.save(bookingRequest);
//            response.setStatusCode(200);
//            response.setMessage("Booking successfully saved");
//            response.setBookingConfirmationCode(bookingConfirmationCode);
//        } catch (OurException e) {
//            response.setStatusCode(404);
//            response.setMessage(e.getMessage());
//
//        } catch (Exception e) {
//            response.setStatusCode(500);
//            response.setMessage("Error Saving a booking: " + e.getMessage());
//
//        }
//        return response;
//    }

    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {
        Response response = new Response();
        try{
            Booking booking = bookingRepo.findByBookingConfirmationCode(confirmationCode).orElseThrow(() -> new OurException("Booking not found"));
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRooms(booking, true);
            response.setStatusCode(200);
            response.setMessage("Booking successfully found");
            response.setBooking(bookingDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Finding a booking: " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response getAllBookings() {
        Response response = new Response();
        try{
            List<Booking> bookingList = bookingRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<BookingDTO> bookingDTOList = Utils.mapBookingListEntityToBookingListDTO(bookingList);
            response.setStatusCode(200);
            response.setMessage("Bookings found successfully");
            response.setBookingList(bookingDTOList);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Getting all bookings: " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response cancelBooking(Long bookingId) {
        Response response = new Response();

        try {
            bookingRepo.findById(bookingId).orElseThrow(() -> new OurException("Booking Does Not Exist"));
            bookingRepo.deleteById(bookingId);
            response.setStatusCode(200);
            response.setMessage("Booking successfully cancelled");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Cancelling a booking: " + e.getMessage());

        }
        return response;
    }

    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {

        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getStartDate().equals(existingBooking.getStartDate())
                                || bookingRequest.getEndDate().isBefore(existingBooking.getEndDate())
                                || (bookingRequest.getStartDate().isAfter(existingBooking.getStartDate())
                                && bookingRequest.getStartDate().isBefore(existingBooking.getEndDate()))
                                || (bookingRequest.getStartDate().isBefore(existingBooking.getStartDate())

                                && bookingRequest.getEndDate().equals(existingBooking.getEndDate()))
                                || (bookingRequest.getStartDate().isBefore(existingBooking.getStartDate())

                                && bookingRequest.getEndDate().isAfter(existingBooking.getEndDate()))

                                || (bookingRequest.getStartDate().equals(existingBooking.getEndDate())
                                && bookingRequest.getEndDate().equals(existingBooking.getStartDate()))

                                || (bookingRequest.getStartDate().equals(existingBooking.getEndDate())
                                && bookingRequest.getEndDate().equals(bookingRequest.getStartDate()))
                );
    }
}
