package com.validator;

import com.kucoin.common.validator.IValidator;
import com.kucoin.common.validator.PatternMatherUtil;
import com.kucoin.common.validator.ValidatorException;
import com.kucoin.common.validator.constraint.Ip;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class IpValidator implements IValidator<Ip> {
    public static final String IP_RULE = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";

    public IpValidator() {
    }

    public Pair<Boolean, String> doValidate(Object value, Ip anno) throws ValidatorException {
        String param;
        try {
            param = (String)value;
        } catch (Exception var5) {
            throw new ValidatorException("ip must be string");
        }

        if (StringUtils.isEmpty(param)) {
            return anno.require() ? Pair.of(false, anno.message()) : Pair.of(true, "");
        } else {
            return !PatternMatherUtil.matches(param, "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
        }
    }
}
