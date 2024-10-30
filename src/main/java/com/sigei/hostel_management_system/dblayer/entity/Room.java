package com.sigei.hostel_management_system.dblayer.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number")
    private int roomNumber;
    @Column(name = "room_type")
    private String roomType;
    @Column(name = "room_status")
    private String roomStatus;

    @OneToMany(mappedBy = "room")
    private List<Booking> bookings;
}









//    @ManyToOne
//    @JoinColumn(name = "hostel_id")
//    private Hostel hostel;
