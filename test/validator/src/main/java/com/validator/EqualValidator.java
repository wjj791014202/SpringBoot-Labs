package com.validator;

import com.kucoin.common.validator.IValidator;
import com.kucoin.common.validator.ValidatorException;
import com.kucoin.common.validator.constraint.Equal;
import org.apache.commons.lang3.tuple.Pair;

public class EqualValidator implements IValidator<Equal> {
    public EqualValidator() {
    }

    public Pair<Boolean, String> doValidate(Object value, Equal anno) throws ValidatorException {
        Long param;
        try {
            param = Long.parseLong(value.toString());
        } catch (Exception var5) {
            throw new ValidatorException("value must be number");
        }

        return param != anno.value() ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
    }
}