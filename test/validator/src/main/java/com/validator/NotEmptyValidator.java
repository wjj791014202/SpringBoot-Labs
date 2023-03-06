package com.validator;

import com.kucoin.common.validator.IValidator;
import com.kucoin.common.validator.ValidatorException;
import com.kucoin.common.validator.constraint.NotEmpty;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class NotEmptyValidator implements IValidator<NotEmpty> {
    public NotEmptyValidator() {
    }

    public Pair<Boolean, String> doValidate(Object value, NotEmpty anno) throws ValidatorException {
        if (value == null) {
            return Pair.of(false, anno.message());
        } else if (value instanceof String) {
            String param = (String)value;
            return StringUtils.isEmpty(param) ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            Collection collection = (Collection)value;
            return collection.isEmpty() ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
        } else if (Map.class.isAssignableFrom(value.getClass())) {
            Map map = (Map)value;
            return map.isEmpty() ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
        } else if (Object[].class.isAssignableFrom(value.getClass())) {
            Object[] objects = (Object[])((Object[])value);
            return objects.length == 0 ? Pair.of(false, anno.message()) : Pair.of(true, (Object)null);
        } else {
            throw new ValidatorException("unsupport class type");
        }
    }
}
