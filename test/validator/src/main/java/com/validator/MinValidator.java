package com.validator;

import com.kucoin.common.validator.IValidator;
import com.kucoin.common.validator.ValidatorException;
import com.kucoin.common.validator.constraint.Min;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.lang3.tuple.Pair;

public class MinValidator implements IValidator<Min> {
    public MinValidator() {
    }

    public Pair<Boolean, String> doValidate(Object value, Min anno) throws ValidatorException {
        if (value == null) {
            return anno.require() ? Pair.of(false, anno.message()) : Pair.of(true, "");
        } else if (value instanceof BigDecimal) {
            BigDecimal realValue = (BigDecimal)BigDecimal.class.cast(value);
            return (!anno.includeMin() || realValue.compareTo(new BigDecimal(anno.value())) >= 0) && (anno.includeMin() || realValue.compareTo(new BigDecimal(anno.value())) > 0) ? Pair.of(true, (Object)null) : Pair.of(false, anno.message());
        } else if (value instanceof BigInteger) {
            BigInteger realValue = (BigInteger)BigInteger.class.cast(value);
            return (!anno.includeMin() || realValue.compareTo(BigInteger.valueOf(anno.value())) >= 0) && (anno.includeMin() || realValue.compareTo(BigInteger.valueOf(anno.value())) > 0) ? Pair.of(true, (Object)null) : Pair.of(false, anno.message());
        } else {
            Long param;
            try {
                param = Long.parseLong(value.toString());
            } catch (Exception var5) {
                throw new ValidatorException("value must be number");
            }

            return (!anno.includeMin() || param >= anno.value()) && (anno.includeMin() || param > anno.value()) ? Pair.of(true, (Object)null) : Pair.of(false, anno.message());
        }
    }
}
