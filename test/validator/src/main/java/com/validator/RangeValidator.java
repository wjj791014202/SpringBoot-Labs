package com.validator;

import com.kucoin.common.validator.IValidator;
import com.kucoin.common.validator.ValidatorException;
import com.kucoin.common.validator.constraint.Range;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.lang3.tuple.Pair;

public class RangeValidator implements IValidator<Range> {
    public RangeValidator() {
    }

    public Pair<Boolean, String> doValidate(Object value, Range anno) throws ValidatorException {
        if (value == null) {
            return anno.require() ? Pair.of(false, anno.message()) : Pair.of(true, "");
        } else if (value instanceof BigDecimal) {
            BigDecimal realValue = (BigDecimal)BigDecimal.class.cast(value);
            return (anno.includeMin() && realValue.compareTo(new BigDecimal(anno.min())) >= 0 || !anno.includeMin() && realValue.compareTo(new BigDecimal(anno.min())) > 0) && (anno.includeMax() && realValue.compareTo(new BigDecimal(anno.max())) <= 0 || !anno.includeMax() && realValue.compareTo(new BigDecimal(anno.max())) < 0) ? Pair.of(true, (Object)null) : Pair.of(false, anno.message());
        } else if (value instanceof BigInteger) {
            BigInteger realValue = (BigInteger)BigInteger.class.cast(value);
            return realValue.compareTo(BigInteger.valueOf(anno.min())) <= 0 && (!anno.includeMin() || realValue.compareTo(BigInteger.valueOf(anno.min())) != 0) || realValue.compareTo(BigInteger.valueOf(anno.max())) >= 0 && (!anno.includeMax() || realValue.compareTo(BigInteger.valueOf(anno.max())) != 0) ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
        } else {
            Long param;
            try {
                param = Long.parseLong(value.toString());
            } catch (Exception var5) {
                throw new ValidatorException("value must be number");
            }

            return param <= anno.min() && (!anno.includeMin() || param != anno.min()) || param >= anno.max() && (!anno.includeMax() || param != anno.max()) ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
        }
    }
}
