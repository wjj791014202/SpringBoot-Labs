package com.validator;

import com.kucoin.common.validator.ValidatorConst.Code;
import com.kucoin.common.validator.constraint.Constraint;
import com.kucoin.common.validator.constraint.Or;
import com.kucoin.common.validator.constraint.Validate;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateProcessHandler {
    private static final Map<Class<? extends Annotation>, IValidator<?>> CACHE_MAP = new HashMap();
    private static final Logger LOG = LoggerFactory.getLogger(ValidateProcessHandler.class);

    public ValidateProcessHandler() {
    }

    public static Pair<Boolean, String> beanValidate(Object value) throws ValidatorException {
        try {
            List<Field> allFields = getAllFields(value);
            Iterator var2 = allFields.iterator();

            while(var2.hasNext()) {
                Field field = (Field)var2.next();
                if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                    Annotation[] annos = field.getAnnotations();
                    if (annos != null && annos.length != 0) {
                        field.setAccessible(true);
                        Pair<Boolean, String> result = annotationValidate(field.get(value), annos);
                        if (!(Boolean)result.getLeft()) {
                            return result;
                        }
                    }
                }
            }
        } catch (Exception var6) {
            throw new ValidatorException(var6);
        }

        return Pair.of(true, (Object)null);
    }

    public static Pair<Boolean, String> annotationValidate(Object value, Annotation[] annotationArray) throws ValidatorException {
        if (annotationArray != null && annotationArray.length != 0) {
            if (hasModelValidateFlag(annotationArray)) {
                return beanValidate(value);
            } else {
                boolean isOr = hasCompositeOr(annotationArray);
                Pair<Code, String> pair = null;
                int var6;
                if (isOr) {
                    String failSb = null;
                    Annotation[] var10 = annotationArray;
                    var6 = annotationArray.length;

                    for(int var11 = 0; var11 < var6; ++var11) {
                        Annotation anno = var10[var11];
                        pair = singleValidate(value, anno);
                        if (Code.OK.equals(pair.getLeft())) {
                            return Pair.of(true, pair.getRight());
                        }

                        if (Code.FAIL.equals(pair.getLeft())) {
                            failSb = (String)pair.getRight();
                        }
                    }

                    return Pair.of(false, failSb);
                } else {
                    Annotation[] var4 = annotationArray;
                    int var5 = annotationArray.length;

                    for(var6 = 0; var6 < var5; ++var6) {
                        Annotation anno = var4[var6];
                        pair = singleValidate(value, anno);
                        if (Code.FAIL.equals(pair.getLeft())) {
                            return Pair.of(false, pair.getRight());
                        }
                    }

                    return Pair.of(true, pair == null ? null : (String)pair.getRight());
                }
            }
        } else {
            return Pair.of(true, (Object)null);
        }
    }

    public static Pair<Boolean, String> validate(Object value, Annotation anno) throws ValidatorException {
        Pair<Code, String> pair = singleValidate(value, anno);
        return Code.FAIL.equals(pair.getLeft()) ? Pair.of(false, pair.getRight()) : Pair.of(true, pair.getRight());
    }

    private static <T extends Annotation> Pair<Code, String> singleValidate(Object value, T anno) throws ValidatorException {
        IValidator<T> validator = getValidator(anno.annotationType());
        if (validator == null) {
            return Pair.of(Code.NO_VALIDATOR, (Object)null);
        } else {
            Pair<Boolean, String> result = validator.doValidate(value, anno);
            return (Boolean)result.getLeft() ? Pair.of(Code.OK, result.getRight()) : Pair.of(Code.FAIL, result.getRight());
        }
    }

    private static boolean hasModelValidateFlag(Annotation[] annos) {
        Annotation[] var1 = annos;
        int var2 = annos.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Annotation anno = var1[var3];
            if (anno instanceof Validate) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasCompositeOr(Annotation[] annos) {
        Annotation[] var1 = annos;
        int var2 = annos.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Annotation anno = var1[var3];
            if (anno instanceof Or) {
                return true;
            }
        }

        return false;
    }

    private static final IValidator<?> getValidator(Class<? extends Annotation> clazz) {
        if (CACHE_MAP.containsKey(clazz)) {
            return (IValidator)CACHE_MAP.get(clazz);
        } else {
            Constraint constraint = (Constraint)clazz.getAnnotation(Constraint.class);
            if (constraint == null) {
                return null;
            } else {
                try {
                    IValidator<?> validator = (IValidator)constraint.validator().newInstance();
                    CACHE_MAP.put(clazz, validator);
                    return validator;
                } catch (Exception var3) {
                    LOG.error("fail to get validator", var3);
                    return null;
                }
            }
        }
    }

    private static List<Field> getAllFields(Object bean) {
        List<Field> allFields = new ArrayList();
        allFields.addAll(Arrays.asList(bean.getClass().getDeclaredFields()));
        if (bean.getClass().getSuperclass() != Object.class) {
            allFields.addAll(Arrays.asList(bean.getClass().getSuperclass().getDeclaredFields()));
        }

        return allFields;
    }
}
