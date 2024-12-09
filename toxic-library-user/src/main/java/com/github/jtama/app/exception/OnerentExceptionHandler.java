package com.github.jtama.app.exception;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Provider
public class OnerentExceptionHandler implements ExceptionMapper<Exception> {

    private static Map<Class<?>, Function<Throwable, Response>> mappers;
    private static Function<Throwable, Response> functionalError =  e -> Response.status(Response.Status.OK).entity(e.getMessage()).build();
    private static Function<Throwable, Response> defaultHandler = e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(e.getMessage())
            .build();

    static {
        mappers = new HashMap<>();
        mappers.put(DuplicateEntityException.class, e -> Response.status(Response.Status.CONFLICT)
                .entity(e.getMessage())
                .build());
        mappers.put(UnknownEntityException.class, e -> Response.status(Response.Status.NOT_FOUND)
                .entity(e.getMessage())
                .build());
        mappers.put(NotAuthorizedException.class, e -> Response.status(Response.Status.UNAUTHORIZED)
                .entity("He non du con")
                .build());
        mappers.put(InvalidBookingException.class,functionalError);
        mappers.put(InvalidNameException.class,functionalError);
        mappers.put(UnavailableException.class,functionalError);

    }

    @Override
    public Response toResponse(Exception exception) {
        return mappers.getOrDefault(exception.getClass(), defaultHandler).apply(exception);
    }
}
