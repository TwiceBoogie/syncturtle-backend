package dev.twiceb.common.util;

import dev.twiceb.common.exception.InputFieldException;
import jakarta.persistence.Column;
import org.springframework.validation.BindingResult;

import java.lang.reflect.Field;

public abstract class ServiceHelper {
    public void processInputErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        }
    }

    protected String buildUpdateQuery(Object entity, String tableName, String identifierColumn)
            throws IllegalAccessException {
        Class<?> clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder updateValues = new StringBuilder();
        boolean isFirstColumn = true;

        Object idValue = null;

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(entity);

            if (!field.getName().equals(identifierColumn)) {
                if (value != null) {
                    if (!isFirstColumn) {
                        updateValues.append(", ");
                    }
                    updateValues.append(field.getName()).append(" = :").append(field.getName());
                    isFirstColumn = false;
                }
            }

            if (field.getName().equals(identifierColumn)) {
                idValue = value;
            }
        }

        if (!updateValues.isEmpty() && idValue != null) {
            return "UPDATE " + tableName + " SET " + updateValues + " WHERE " + identifierColumn + " = :idValue";
        } else {
            throw new IllegalArgumentException("No columns found to update or ID value missing");
        }
    }

//    protected String buildUpdateQuery(Object entity, String tableName, String identifierColumn)
//            throws IllegalAccessException {
//        Class<?> clazz = entity.getClass();
//        Field[] fields = clazz.getDeclaredFields();
//        StringBuilder updateValues = new StringBuilder();
//        boolean isFirstColumn = true;
//
//        Object idValue = null;
//
//        for (Field field : fields) {
//            field.setAccessible(true);
//            Object value = field.get(entity);
//
//            if (!field.getName().equals(identifierColumn)) {
//                if (value != null) {  // Check for null before appending
//                    if (!isFirstColumn) {
//                        updateValues.append(", ");
//                    }
//                    updateValues.append(field.getName()).append(" = '").append(value).append("'");
//                    isFirstColumn = false;
//                }
//            }
//
//            if (field.getName().equals(identifierColumn)) {
//                idValue = value;
//            }
//        }
//
//        if (!updateValues.isEmpty() && idValue != null) {
//            return "UPDATE " + tableName + " SET " + updateValues + " WHERE " + identifierColumn + " = " + idValue;
//        } else {
//            throw new IllegalArgumentException("No columns found to update or ID value missing");
//        }
//    }
}
