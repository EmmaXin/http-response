package com.accton.httpcomponents.response;

import com.github.fge.jsonschema.core.report.ProcessingReport;

public class BadRequestErrorResponse extends Response {
    public BadRequestErrorResponse(ProcessingReport report) {
        super(StatusCode.BAD_REQUEST, "Bad Request", report);
    }
}
