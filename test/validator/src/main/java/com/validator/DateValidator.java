package com.validator;

import com.kucoin.common.validator.IValidator;
import com.kucoin.common.validator.PatternMatherUtil;
import com.kucoin.common.validator.ValidatorException;
import com.kucoin.common.validator.constraint.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class DateValidator implements IValidator<Date> {
    private static final String DATE_RULE = "^\\d{4}(\\-|\\/|\\.)\\d{1,2}\\1\\d{1,2}$";

    public DateValidator() {
    }

    public Pair<Boolean, String> doValidate(Object value, Date anno) throws ValidatorException {
        String dateStr;
        try {
            dateStr = (String)value;
        } catch (Exception var5) {
            throw new ValidatorException("date must be string");
        }

        if (StringUtils.isEmpty(dateStr)) {
            return anno.require() ? Pair.of(false, anno.message()) : Pair.of(true, "");
        } else {
            return !PatternMatherUtil.matches(dateStr, "^\\d{4}(\\-|\\/|\\.)\\d{1,2}\\1\\d{1,2}$") ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
        }
    }
}
