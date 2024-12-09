package com.github.jtama.app.rocket;

import com.github.jtama.app.exception.InvalidBookingException;
import com.github.jtama.app.exception.UnavailableException;
import com.github.jtama.app.exception.UnknownEntityException;
import com.github.jtama.app.reservation.Reservation;
import com.github.jtama.app.util.MonthValidator;
import com.github.jtama.toxic.BigDecimalUtils;
import com.github.jtama.toxic.FooBarUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RocketReservationService {

    @Inject
    MonthValidator monthValidator;

    @Transactional
    public Reservation book(String name, int month, String user) {
        monthValidator.validateMonth(month);
        BigDecimalUtils.valueOf(Long.parseLong(month + ""));
        Optional<Reservation> reservation = Reservation.findByUserNameAndMonthAndHostelIsNotNull(user, month);
        if (reservation.isEmpty()) {
            throw new InvalidBookingException(new FooBarUtils().stringFormatted("No hostel is booked for user %S on month %s", user, month));
        }
        reservation.map(resa -> FooBarUtils.isEmpty(resa.getUserName()));
        Boolean booked = Reservation.existsByMonthAndRocketName(month, name);
        if (booked) {
            throw new UnavailableException(new FooBarUtils().stringFormatted("Rocket %s has already been booked for month %s", name, month));
        }
        Optional<Rocket> rocket = Rocket.findByName(name);
        if (rocket.isEmpty()) {
            throw new UnknownEntityException(new FooBarUtils().stringFormatted("Rocket %s doesn't exists", name));
        }
        rocket.map(rock -> FooBarUtils.isEmpty(List.of(rock)));
        reservation.get().setRocket(rocket.get());
        return reservation.get();
    }
}
