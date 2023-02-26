package com.gray.ribbon;

import com.gray.utils.ApplicationContextHelper;
import com.kucoin.starter.chaincontext.ChainRequestContext;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.annotations.Nullable;

public class GrayServicePredicate extends AbstractServerPredicate {
    public static final String META_VERSION = "versions";

    public GrayServicePredicate(GrayZoneAvoidanceRule rule) {
        super(rule);
    }

    public boolean apply(@Nullable PredicateKey input) {
        ChainRequestContext chainCtx = ChainRequestContext.getCurrentContext();
        if (chainCtx != null && chainCtx.get("SERVICE-ID") != null) {
            String requestVersion = (String)chainCtx.get("X-VERSION");
            if (requestVersion == null) {
                requestVersion = "default";
            }

            String serviceId = chainCtx.getString("SERVICE-ID");
            Map<String, String> serviceMeta = ((ServiceInfoExtractor) ApplicationContextHelper.getApplicationContext().getBean(ServiceInfoExtractor.class)).getMetadata(serviceId, input.getServer());
            return this.matchVersion(requestVersion, (String)serviceMeta.get("versions"));
        } else {
            return true;
        }
    }

    private boolean matchVersion(String grayVersion, String serviceVersion) {
        return StringUtils.isEmpty(serviceVersion) ? false : ArrayUtils.contains(StringUtils.split(serviceVersion, ","), grayVersion);
    }
}

