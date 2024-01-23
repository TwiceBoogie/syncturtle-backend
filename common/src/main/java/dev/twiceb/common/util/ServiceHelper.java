package dev.twiceb.common.util;

import dev.twiceb.common.enums.EventStatus;
import dev.twiceb.common.enums.PriorityStatus;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.exception.InputFieldException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static dev.twiceb.common.constants.ErrorMessage.PASSWORDS_NOT_MATCH;
import static dev.twiceb.common.constants.ErrorMessage.PASSWORD_LENGTH_ERROR;

public abstract class ServiceHelper {

    protected abstract EntityManager getEntityManager();

    protected void processInputErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        }
    }

    protected void processPasswordInput(String password1, String password2) {
        if (!password1.equals(password2)) {
            throw new ApiRequestException(PASSWORDS_NOT_MATCH, HttpStatus.BAD_REQUEST);
        }

        if (password1.length() < 9) {
            throw new ApiRequestException(PASSWORD_LENGTH_ERROR, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method dynamically generates SQL 'UPDATE' statement based on the
     * provided entity object,
     * table name, and identifier column. The generated statement is a prepared
     * statement. There must
     * be at least 1 non-null value (aside from id) or it won't work.
     * 
     * @param entity           The update request object that will be used to create
     *                         the query using the entity
     *                         declared fields. The declared fields must be the same
     * @param tableName        The name of table you want to update fields from
     * @param identifierColumn This is the column for 'WHERE'. For now it must be a
     *                         column that
     *                         uses a Long value.
     * @return This will return a UpdateQueryResult object which houses the
     * @throws IllegalAccessException
     */
    protected UpdateQueryResult buildUpdateQuery(Object entity, String tableName, String identifierColumn)
            throws IllegalAccessException {
        Class<?> clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder updateValues = new StringBuilder();
        boolean isFirstColumn = true;
        Map<String, String> values = new HashMap<>();
        ArrayDeque<Object> types = new ArrayDeque<>();
        UpdateQueryResult result = new UpdateQueryResult();
        // This will help with retrieving the values in an ordered way
        // since types will pop the values from the end.
        ArrayDeque<String> keys = new ArrayDeque<>();

        Object idValue = null;

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(entity);

            if (!field.getName().equals(identifierColumn)) {
                if (value != null) {
                    if (!isFirstColumn) {
                        updateValues.append(", ");
                    }
                    updateValues.append(camelCaseToSnakeCase(field.getName())).append(" = :").append(field.getName());
                    isFirstColumn = false;
                    values.put(field.getName(), value.toString());
                    keys.add(field.getName());
                    types.add(field.getType());
                }
            }

            if (field.getName().equals(identifierColumn)) {
                idValue = value;
                if (value instanceof Long) {
                    result.setIdentifierValue((Long) value);
                }
            }
        }

        if (!updateValues.isEmpty() && idValue != null) {
            String query = "UPDATE " + tableName + " SET " + updateValues + " WHERE " + identifierColumn
                    + " = :idValue";
            result.setQuery(query);
            result.setValues(values);
            result.setTypes(types);
            result.setKeys(keys);
            return result;
        } else {
            throw new IllegalArgumentException("No columns found to update or ID value missing");
        }
    }

    private String camelCaseToSnakeCase(String value) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char currentChar = value.charAt(i);

            if (Character.isUpperCase(currentChar) && i > 0) {
                output.append('_');
            }

            output.append(Character.toLowerCase(currentChar));
        }
        return output.toString();
    }

    protected void executeUpdateQuery(UpdateQueryResult result, String identifierColumn) throws ParseException {
        ArrayDeque<Object> types = result.getTypes();
        Map<String, String> values = result.getValues();
        ArrayDeque<String> keys = result.getKeys();
        EntityManager entityManager = getEntityManager();

        Query query = entityManager.createNativeQuery(result.getQuery());
        query.setParameter(identifierColumn, result.getIdentifierValue());

        for (Object type : types) {
            if (type instanceof String) {
                query.setParameter(keys.peek(), (String) values.get(keys.poll()));
            }
            if (type instanceof Date) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(values.get(keys.peek()));

                query.setParameter(keys.poll(), date);
            }
            if (type instanceof PriorityStatus) {
                String priorityStatusStr = values.get(keys.peek()); // Assuming priorityStatus is represented as a
                                                                    // string
                PriorityStatus priorityStatus = PriorityStatus.valueOf(priorityStatusStr);
                query.setParameter(keys.poll(), priorityStatus);
            }
            if (type instanceof EventStatus) {
                String eventStatusStr = values.get(keys.peek());
                EventStatus eventStatus = EventStatus.valueOf(eventStatusStr);
                query.setParameter(keys.poll(), eventStatus);
            }
        }
        query.executeUpdate();
    }
}
