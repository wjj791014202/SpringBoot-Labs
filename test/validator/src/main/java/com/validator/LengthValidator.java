package com.validator;

import com.kucoin.common.validator.IValidator;
import com.kucoin.common.validator.ValidatorException;
import com.kucoin.common.validator.constraint.Length;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class LengthValidator implements IValidator<Length> {
    public LengthValidator() {
    }

    public Pair<Boolean, String> doValidate(Object value, Length anno) throws ValidatorException {
        String param;
        try {
            param = (String)value;
        } catch (Exception var5) {
            throw new ValidatorException("only string value can use length validator");
        }

        if (StringUtils.isEmpty(param)) {
            return anno.require() ? Pair.of(false, anno.message()) : Pair.of(true, "");
        } else {
            int len = param.length();
            return len >= anno.min() && len <= anno.max() ? Pair.of(true, (Object)null) : Pair.of(false, anno.message());
        }
    }
}
