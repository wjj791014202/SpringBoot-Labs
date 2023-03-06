package com.micrometer;


@Configuration
@AutoConfigureBefore({WebMvcMetricsAutoConfiguration.class})
public class MicrometerConfigutaion {
    public MicrometerConfigutaion() {
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(@Value("${spring.application.name}") String application) {
        return (registry) -> {
            registry.config().commonTags(new String[]{"application", application});
        };
    }
}

