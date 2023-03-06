package com.validator;

import com.kucoin.base.result.CommonCode;
import com.kucoin.base.result.GenericHasMoreResult;
import com.kucoin.base.result.GenericListResult;
import com.kucoin.base.result.GenericPageResult;
import com.kucoin.base.result.GenericResult;
import com.kucoin.common.validator.ValidateProcessHandler;
import com.kucoin.common.validator.ValidatorException;
import com.kucoin.common.validator.constraint.ErrorTip;
import com.kucoin.starter.i18n.I18nConfigration;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
@AutoConfigureAfter({I18nConfigration.class})
public class ValidatorAspectConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(ValidatorAspectConfiguration.class);

    public ValidatorAspectConfiguration() {
    }

    @Pointcut("within(@org.springframework.stereotype.Controller *) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllercut() {
    }

    @Around("controllercut()")
    public Object validate(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        Annotation[][] paramterAnnotations = method.getParameterAnnotations();
        int i = 0;

        for(int len = args.length; i < len; ++i) {
            if (paramterAnnotations[i] != null && paramterAnnotations[i].length != 0) {
                Pair validateResult = null;

                try {
                    validateResult = ValidateProcessHandler.annotationValidate(args[i], paramterAnnotations[i]);
                } catch (ValidatorException var10) {
                    LOG.error("fail to validate request params with value " + args[i], var10);
                }

                if (validateResult != null && !(Boolean)validateResult.getLeft()) {
                    String localeTip = (String)validateResult.getRight();
                    List<String> errorTipResult = this.getErrorTipArg(args, paramterAnnotations);
                    if (errorTipResult == null) {
                        if (GenericResult.class.isAssignableFrom(method.getReturnType())) {
                            return GenericResult.fail(CommonCode.CODE_PARAM_ERROR, false, new String[]{localeTip});
                        }

                        if (GenericPageResult.class.isAssignableFrom(method.getReturnType())) {
                            return GenericPageResult.fail(false, CommonCode.CODE_PARAM_ERROR, new String[]{localeTip});
                        }

                        if (GenericListResult.class.isAssignableFrom(method.getReturnType())) {
                            return GenericListResult.fail(CommonCode.CODE_PARAM_ERROR, false, new String[]{localeTip});
                        }

                        if (GenericHasMoreResult.class.isAssignableFrom(method.getReturnType())) {
                            return GenericHasMoreResult.fail(CommonCode.CODE_PARAM_ERROR, false, new String[]{localeTip});
                        }

                        throw new ValidatorException(localeTip);
                    }

                    errorTipResult.add(localeTip);
                }
            }
        }

        return joinPoint.proceed();
    }

    private List<String> getErrorTipArg(Object[] args, Annotation[][] annos) {
        int i = 0;

        for(int len = annos.length; i < len; ++i) {
            Annotation[] argAnno = annos[i];
            if (argAnno != null && argAnno.length != 0) {
                Annotation[] var6 = argAnno;
                int var7 = argAnno.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    Annotation anno = var6[var8];
                    if (anno instanceof ErrorTip) {
                        return (List)args[i];
                    }
                }
            }
        }

        return null;
    }
}
