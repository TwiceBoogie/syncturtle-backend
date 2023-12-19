package dev.twiceb.common.util;

import dev.twiceb.common.exception.InputFieldException;
import org.springframework.validation.BindingResult;

public abstract class ServiceHelper {
    public void processInputErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        }
    }
}
