package com.gray.ribbon;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.CompositePredicate.Builder;
import com.netflix.loadbalancer.ZoneAvoidanceRule;

public class GrayZoneAvoidanceRule extends ZoneAvoidanceRule {
    protected CompositePredicate compositePredicate;

    public GrayZoneAvoidanceRule() {
        this((CustomServicePredicate)null);
    }

    public GrayZoneAvoidanceRule(CustomServicePredicate customServerPredicate) {
        Builder builder = CompositePredicate.withPredicates(new AbstractServerPredicate[]{super.getPredicate(), new GrayServicePredicate(this)});
        if (customServerPredicate != null) {
            customServerPredicate.setRule(this);
            builder.addFallbackPredicate(customServerPredicate);
        }

        builder.addFallbackPredicate(new GrayServiceFallbackPredicate(this));
        this.compositePredicate = builder.build();
    }

    public AbstractServerPredicate getPredicate() {
        return this.compositePredicate;
    }
}

