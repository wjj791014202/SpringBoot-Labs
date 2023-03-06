package com.validator;

import com.kucoin.common.validator.IValidator;
import com.kucoin.common.validator.ValidatorException;
import com.kucoin.common.validator.constraint.NotBlank;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class NotBlankValidator implements IValidator<NotBlank> {
    public NotBlankValidator() {
    }

    public Pair<Boolean, String> doValidate(Object value, NotBlank anno) throws ValidatorException {
        if (value == null) {
            return Pair.of(false, anno.message());
        } else {
            return StringUtils.isBlank(value.toString()) ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
        }
    }
}