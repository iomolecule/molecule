package org.molecule.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

public class JSONUtilsTests {

    @Test
    public void testMergeNodes() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode baseJsonNode = mapper.readTree(JSONUtilsTests.class.getResource("/base.json"));
        JsonNode overridingJsonNode = mapper.readTree(JSONUtilsTests.class.getResource("/overriding.json"));

        JSONUtils.merge(overridingJsonNode,baseJsonNode,false);

        System.out.println(baseJsonNode);
    }
}
