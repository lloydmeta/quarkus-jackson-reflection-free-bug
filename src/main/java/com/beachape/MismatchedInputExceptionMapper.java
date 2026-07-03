package com.beachape;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class MismatchedInputExceptionMapper implements ExceptionMapper<MismatchedInputException> {
    @Override
    public Response toResponse(MismatchedInputException exception) {
        return Response.status(422)
                .type("application/json")
                .entity("{\"handledBy\":\"MismatchedInputExceptionMapper\"}")
                .build();
    }
}
