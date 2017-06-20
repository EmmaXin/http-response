package com.accton.httpcomponents.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.util.Iterator;

import static com.accton.httpcomponents.response.Response.StatusCode.SUCCESS;

/**
 * Unit test for simple App.
 */
public class ResponseTest
        extends TestCase {

    JsonSchemaManager jsonSchemaManager;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ResponseTest(String testName) {
        super(testName);

        this.jsonSchemaManager = new JsonSchemaManager();

        try {
            jsonSchemaManager.load(this.getClass(), "/schema/foo.json");
        } catch (IOException e) {

        }
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ResponseTest.class);
    }

    void checkResponseFormat(ObjectNode response) {
        assertTrue(response.get("code") != null);
        assertTrue(response.get("data") != null);
        assertTrue((response.get("data").isObject() || response.get("data").isArray()));
    }

    ArrayNode getErrors(ObjectNode response) {
        return (ArrayNode) response.get("data").get("errors");
    }

    void checkErrors(ArrayNode errors) {
        for (Iterator<JsonNode> it = errors.iterator(); it.hasNext();) {
            JsonNode error = it.next();
            assertTrue(error.isObject());
            assertTrue(((ObjectNode)error).get("level") != null);
            assertTrue(((ObjectNode)error).get("schema") != null);
            assertTrue(((ObjectNode)error).get("instance") != null);
            assertTrue(((ObjectNode)error).get("domain") != null);
            assertTrue(((ObjectNode)error).get("keyword") != null);
            assertTrue(((ObjectNode)error).get("message") != null);
        }
    }

    public void testBadRequestResponse() {
        ObjectNode request = JsonNodeFactory.instance.objectNode();
        ProcessingReport report = jsonSchemaManager.check(this.getClass(), request);
        assertTrue(report.isSuccess() == false);

        ObjectNode response = (ObjectNode) (new BadRequestErrorResponse(report).toJson());
        checkResponseFormat(response);

        ArrayNode errors = getErrors(response);
        assertTrue(errors != null);
        checkErrors(errors);

        assertTrue(errors.size() == 1);
        for (Iterator<JsonNode> it = errors.iterator(); it.hasNext();) {
            JsonNode error = it.next();

            assertTrue(((ObjectNode)error).get("keyword").textValue().equals("required"));
            assertTrue(((ObjectNode)error).get("required").toString().equals("[\"id\"]"));
            assertTrue(((ObjectNode)error).get("missing").toString().equals("[\"id\"]"));
        }
    }

    public void testSuccessResponse() {
        ObjectNode response = (ObjectNode) (new Response(SUCCESS, "Success").toJson());
        checkResponseFormat(response);

        JsonNode data = response.get("data");
        assertTrue(data.size() == 1);
    }

    public void testObjectDataResponse() {
        JsonNode data = JsonNodeFactory.instance.objectNode();
        ((ObjectNode) data).put("test", "objectNode");

        ObjectNode response = (ObjectNode) (new Response(SUCCESS, data).toJson());
        checkResponseFormat(response);

        JsonNode respData = response.get("data");
        assertTrue(respData.isObject() == true);
        assertTrue(respData.equals(data));
    }

    public void testArrayDataResponse() {
        JsonNode data = JsonNodeFactory.instance.arrayNode();
        Integer maxCount = 5;

        for (Integer index = 1; index <= maxCount; ++index) {
            ObjectNode obj = JsonNodeFactory.instance.objectNode();
            obj.put(index.toString(), "array" + index);

            ((ArrayNode) data).add(obj);
        }

        ObjectNode response = (ObjectNode) (new Response(SUCCESS, data).toJson());
        checkResponseFormat(response);

        JsonNode respData = response.get("data");
        assertTrue(respData.isArray() == true);
        assertTrue(respData.size() == maxCount);
    }
}
