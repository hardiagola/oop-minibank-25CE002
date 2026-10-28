package util;

import model.annotation.MaxLength;
import model.annotation.Positive;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AnnotationValidator {

    public static String[] validate(Object obj) {
        List<String> errors = new ArrayList<>();

        // Traverse the class hierarchy to pick up fields from parent classes
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);

                try {
                    Object value = field.get(obj);

                    // Check @Positive
                    if (field.isAnnotationPresent(Positive.class)) {
                        Positive positive = field.getAnnotation(Positive.class);
                        if (value instanceof Number num && num.longValue() <= 0) {
                            errors.add(field.getName() + ": " + positive.message());
                        }
                    }

                    // Check @MaxLength
                    if (field.isAnnotationPresent(MaxLength.class)) {
                        MaxLength maxLength = field.getAnnotation(MaxLength.class);
                        if (value instanceof String str
                                && str.length() > maxLength.value()) {
                            errors.add(field.getName()
                                    + ": max length is " + maxLength.value());
                        }
                    }

                } catch (IllegalAccessException e) {
                    errors.add("Cannot access field: " + field.getName());
                }
            }

            clazz = clazz.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}
