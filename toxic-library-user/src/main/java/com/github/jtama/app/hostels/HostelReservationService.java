package com.github.jtama.app.hostels;

import com.github.jtama.app.exception.UnavailableException;
import com.github.jtama.app.exception.UnknownEntityException;
import com.github.jtama.app.reservation.Reservation;
import com.github.jtama.app.util.MonthValidator;
import com.github.jtama.toxic.FooBarUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class HostelReservationService {

    @Inject
    MonthValidator monthValidator;

    @Transactional
    public Reservation book(String name, int month, String userName) {
        monthValidator.validateMonth(month);
        System.out.println(FooBarUtils.isEmpty(userName));
        if(Reservation.existsByUserNameAndMonthAndHostelName(userName, month, name)){
            throw new UnavailableException("Hostel %s is already booked for month %s".formatted(name, month));
        }

        Reservation reservation = new Reservation(userName,month);

        Optional<Hostel> hostel = Hostel.findByName(name);
        if (hostel.isEmpty()){
            throw new UnknownEntityException("The hostel %s doesn't exist".formatted(name));
        }
        reservation.setHostel(hostel.get());
        return reservation;
    }

}
