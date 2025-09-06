package dev.twiceb.userservice.domain.model.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class JsonDefaults {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonDefaults() {}

    public static ObjectNode profileOnboarding() {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("profile_complete", false);
        n.put("workspace_create", false);
        n.put("workspace_invite", false);
        n.put("workspace_join", false);
        return n;
    }

}
