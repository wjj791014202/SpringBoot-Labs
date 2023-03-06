package com.validator;

import com.kucoin.common.validator.IValidator;
import com.kucoin.common.validator.ValidatorException;
import com.kucoin.common.validator.constraint.In;
import java.util.Arrays;
import org.apache.commons.lang3.tuple.Pair;

public class InValidator implements IValidator<In> {
    public InValidator() {
    }

    public Pair<Boolean, String> doValidate(Object value, In anno) throws ValidatorException {
        if (value == null) {
            return anno.require() ? Pair.of(false, anno.message()) : Pair.of(true, "");
        } else {
            String[] rangeArray = anno.value();
            if (rangeArray != null && rangeArray.length != 0) {
                String param = value.toString();
                return !Arrays.asList(rangeArray).contains(param) ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
            } else {
                return Pair.of(true, (Object)null);
            }
        }
    }
}
