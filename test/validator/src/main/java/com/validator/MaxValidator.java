package com.validator;

import com.kucoin.common.validator.IValidator;
import com.kucoin.common.validator.ValidatorException;
import com.kucoin.common.validator.constraint.Max;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.lang3.tuple.Pair;

public class MaxValidator implements IValidator<Max> {
    public MaxValidator() {
    }

    public Pair<Boolean, String> doValidate(Object value, Max anno) throws ValidatorException {
        if (value == null) {
            return anno.require() ? Pair.of(false, anno.message()) : Pair.of(true, "");
        } else if (value instanceof BigDecimal) {
            BigDecimal realValue = (BigDecimal)BigDecimal.class.cast(value);
            return (!anno.includeMax() || realValue.compareTo(new BigDecimal(anno.value())) <= 0) && (anno.includeMax() || realValue.compareTo(new BigDecimal(anno.value())) <= 0) ? Pair.of(true, (Object)null) : Pair.of(false, anno.message());
        } else if (value instanceof BigInteger) {
            BigInteger realValue = (BigInteger)BigInteger.class.cast(value);
            return (!anno.includeMax() || realValue.compareTo(BigInteger.valueOf(anno.value())) <= 0) && (anno.includeMax() || realValue.compareTo(BigInteger.valueOf(anno.value())) < 0) ? Pair.of(true, (Object)null) : Pair.of(false, anno.message());
        } else {
            Long param;
            try {
                param = Long.parseLong(value.toString());
            } catch (Exception var5) {
                throw new ValidatorException("value must be number");
            }

            return (!anno.includeMax() || param <= anno.value()) && (anno.includeMax() || param < anno.value()) ? Pair.of(true, (Object)null) : Pair.of(false, anno.message());
        }
    }
}
