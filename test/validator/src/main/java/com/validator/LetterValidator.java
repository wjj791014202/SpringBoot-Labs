package com.validator;

import com.kucoin.common.validator.IValidator;
import com.kucoin.common.validator.PatternMatherUtil;
import com.kucoin.common.validator.ValidatorException;
import com.kucoin.common.validator.constraint.Letter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class LetterValidator implements IValidator<Letter> {
    private static final String LETTER_RULE = "^[A-Za-z]+$";

    public LetterValidator() {
    }

    public Pair<Boolean, String> doValidate(Object value, Letter anno) throws ValidatorException {
        String param;
        try {
            param = (String)value;
        } catch (Exception var5) {
            throw new ValidatorException("letter must be string");
        }

        if (StringUtils.isEmpty(param)) {
            return anno.require() ? Pair.of(false, anno.message()) : Pair.of(true, "");
        } else {
            return !PatternMatherUtil.matches(param, "^[A-Za-z]+$") ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
        }
    }
}
