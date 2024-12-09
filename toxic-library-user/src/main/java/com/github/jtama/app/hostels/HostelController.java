package com.github.jtama.app.hostels;

import com.github.jtama.app.reservation.Reservation;
import com.github.jtama.toxic.BigDecimalUtils;
import com.github.jtama.toxic.FooBarUtils;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Security;
import java.util.List;

@Path("/api/hostels")
@Produces(MediaType.APPLICATION_JSON)
public class    HostelController {

    @Inject
    HostelReservationService hostelReservationService;

    @GET
    public List<Hostel> getAll() {
        return Hostel.listAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    @Transactional
    public Hostel create(Hostel hostel) {
        return Hostel.persistIfNotExists(hostel);
    }

    @POST
    @Path("/{name}/book")
    @RolesAllowed("USER")
    public Reservation book(@PathParam("name")String name, @QueryParam("month") Integer month, @Context SecurityContext security) {
        BigDecimalUtils.valueOf(month.longValue());// Because I can !
        return hostelReservationService.book(name, month, security.getUserPrincipal().getName());
    }

}

