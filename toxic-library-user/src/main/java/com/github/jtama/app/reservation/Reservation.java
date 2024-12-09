package com.github.jtama.app.reservation;


import com.github.jtama.app.hostels.Hostel;
import com.github.jtama.app.rocket.Rocket;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import java.util.Optional;

@Entity
@RegisterForReflection
public class Reservation extends PanacheEntity {

    private String userName;

    private int month;

    @OneToOne
    @JoinColumn(name = "hostel_id")
    private Hostel hostel;

    @OneToOne
    @JoinColumn(name = "rocket_id")
    private Rocket rocket;

    public static Boolean existsByUserNameAndMonthAndHostelName(String user, int month, String name) {
        return count("userName = ?1 and month = ?2 and hostel.name = ?3", user, month, name) > 0;
    }

    public static Optional<Reservation> findByUserNameAndMonthAndHostelIsNotNull(String user, int month) {
        return find("userName = ?1 and month = ?2 and hostel is not null", user, month).firstResultOptional();
    }

    public static Boolean existsByMonthAndRocketName(int month, String name) {
        return count("month = ?1 and rocket.name = ?2", month, name) > 0;
    }

    public Reservation() {
    }

    public Reservation(String userName, int month) {
        this.userName = userName;
        this.month = month;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Hostel getHostel() {
        return hostel;
    }

    public void setHostel(Hostel house) {
        this.hostel = house;
    }

    public Rocket getRocket() {
        return rocket;
    }

    public void setRocket(Rocket rocket) {
        this.rocket = rocket;
    }
}
