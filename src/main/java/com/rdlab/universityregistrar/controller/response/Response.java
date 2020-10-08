package com.rdlab.universityregistrar.controller.response;

import lombok.Builder;
import lombok.Data;

/**
 * Formalized server return message POJO
 */
@Data
@Builder
public class Response {
    private String message;
    private long timeStamp;
    private Object responseBody;
}
