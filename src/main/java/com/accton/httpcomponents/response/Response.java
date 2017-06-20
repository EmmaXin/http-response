package com.accton.httpcomponents.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import java.util.Iterator;

public class Response {
    public enum StatusCode {
        SUCCESS(200), BAD_REQUEST(400), UNAUTHORIZED(401);

        private final int value;
        private StatusCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private StatusCode code;
    private JsonNode data;

    Response(StatusCode code, String message) {
        this.code = code;

        this.data = JsonNodeFactory.instance.objectNode();
        ((ObjectNode)this.data).put("messgae", message);
    }

    Response(StatusCode code, JsonNode data) {
        this.code = code;
        this.data = data;
    }

    Response(StatusCode code, String message, ProcessingReport report) {
        this.code = code;

        this.data = JsonNodeFactory.instance.objectNode();
        ((ObjectNode)this.data).put("messgae", message);
        ((ObjectNode)this.data).putArray("errors").addAll(processReportToJsonArray(report));
    }

    public static ArrayNode processReportToJsonArray(ProcessingReport report) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode jsonNodes = mapper.createArrayNode();

        for (Iterator<ProcessingMessage> iterator = report.iterator(); iterator.hasNext();) {
            ProcessingMessage processingMessage = iterator.next();
            jsonNodes.add(processingMessage.asJson());
        }

        return jsonNodes;
    }

    public JsonNode toJson() {
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("code", code.getValue());
        json.put("data", data);

        return json;
    }
}
