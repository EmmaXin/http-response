package com.accton.httpcomponents.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonSchemaManager {
    private final JsonValidator validator = JsonSchemaFactory.byDefault().getValidator();
    private Map<Class<?>, JsonNode> jsonNodeMap = new HashMap<>();

    public void load(Class<?> className, String schema) throws IOException {
        JsonNode schemaFromDisk = JsonLoader.fromURL(this.getClass().getResource(schema));
        jsonNodeMap.put(className, schemaFromDisk);
    }


    public ProcessingReport check(Class<?> className, JsonNode toBeValidate) {

        ProcessingReport report = null;
        try {
            report = validator.validate(jsonNodeMap.get(className), toBeValidate);
            return report;
        } catch (ProcessingException e) {
            throw new RuntimeJsonMappingException("ERROR -->" + e.toString());
        }
    }
}

