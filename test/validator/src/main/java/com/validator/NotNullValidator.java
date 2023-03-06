package com.validator;

import com.kucoin.common.validator.IValidator;
import com.kucoin.common.validator.ValidatorException;
import com.kucoin.common.validator.constraint.NotNull;
import org.apache.commons.lang3.tuple.Pair;

public class NotNullValidator implements IValidator<NotNull> {
    public NotNullValidator() {
    }

    public Pair<Boolean, String> doValidate(Object value, NotNull anno) throws ValidatorException {
        return value == null ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
    }
}
