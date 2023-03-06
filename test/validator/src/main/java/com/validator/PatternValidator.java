package com.validator;

import com.kucoin.common.validator.IValidator;
import com.kucoin.common.validator.PatternMatherUtil;
import com.kucoin.common.validator.ValidatorException;
import com.kucoin.common.validator.constraint.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class PatternValidator implements IValidator<Pattern> {
    public PatternValidator() {
    }

    public Pair<Boolean, String> doValidate(Object value, Pattern anno) throws ValidatorException {
        if (value == null) {
            return anno.require() ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
        } else {
            String param;
            try {
                param = (String)value;
            } catch (Exception var5) {
                throw new ValidatorException("ip must be string");
            }

            if (StringUtils.isEmpty(param)) {
                return anno.require() ? Pair.of(false, anno.message()) : Pair.of(true, "");
            } else {
                return !PatternMatherUtil.matches(param, anno.value()) ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
            }
        }
    }
}
