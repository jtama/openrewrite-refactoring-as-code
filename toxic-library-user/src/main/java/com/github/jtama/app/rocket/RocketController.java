package com.github.jtama.app.rocket;

import com.github.jtama.app.reservation.Reservation;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.resteasy.annotations.Query;

import java.util.List;

@Path("/api/rockets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RocketController {

    @Inject
    RocketReservationService rocketReservationService;

    @GET
    public List<Rocket> getAll() {
        return Rocket.listAll();
    }

    @POST
    @RolesAllowed("ADMIN")
    @Transactional
    public Response create(@Valid Rocket rocket) {
        return Response.status(Response.Status.CREATED).entity(Rocket.persistIfNotExists(rocket)).build();

    }

    @POST
    @Path("/{name}/book")
    @RolesAllowed("USER")
    public Reservation book(@PathParam("name") String name, @QueryParam("month") Integer month, @Context SecurityContext securityContext) {
        return rocketReservationService.book(name, month, securityContext.getUserPrincipal().getName());
    }

}
