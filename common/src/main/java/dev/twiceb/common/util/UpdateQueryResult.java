package dev.twiceb.common.util;

import lombok.Data;

import java.util.ArrayDeque;
import java.util.Map;

@Data
public class UpdateQueryResult {

    private Long identifierValue;
    private ArrayDeque<Object> types;
    private Map<String, String> values;
    private ArrayDeque<String> keys;
    private String query;
}
