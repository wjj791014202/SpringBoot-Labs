package com.validator;

import java.lang.annotation.Annotation;
import org.apache.commons.lang3.tuple.Pair;

public interface IValidator<T extends Annotation> {
    Pair<Boolean, String> doValidate(Object value, T anno) throws ValidatorException;
}
