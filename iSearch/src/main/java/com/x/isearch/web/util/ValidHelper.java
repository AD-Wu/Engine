package com.x.isearch.web.util;

import java.util.List;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * @author AD
 * @date 2022/1/19 15:44
 */
public class ValidHelper {

    public static String getError(BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder sb = new StringBuilder("{ ");
            List<FieldError> errors = result.getFieldErrors();
            for (int i = 0, N = errors.size(); i < N; i++) {
                FieldError error = errors.get(i);
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(error.getField()).append(":").append(error.getDefaultMessage());
            }
            sb.append(" }");
            return sb.toString();
        }
        return "";
    }
}
