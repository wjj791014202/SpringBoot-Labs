package com.validator;

import com.kucoin.common.validator.IValidator;
import com.kucoin.common.validator.PatternMatherUtil;
import com.kucoin.common.validator.ValidatorException;
import com.kucoin.common.validator.constraint.Mobile;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class MobileValidator implements IValidator<Mobile> {
    public static final String MOBILE_RULE = "^[0-9]{11}$";

    public MobileValidator() {
    }

    public Pair<Boolean, String> doValidate(Object value, Mobile anno) throws ValidatorException {
        String param;
        try {
            param = (String)value;
        } catch (Exception var5) {
            throw new ValidatorException("mobile must be string");
        }

        if (StringUtils.isEmpty(param)) {
            return anno.require() ? Pair.of(false, anno.message()) : Pair.of(true, "");
        } else {
            return !PatternMatherUtil.matches(param, "^[0-9]{11}$") ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
        }
    }
}
