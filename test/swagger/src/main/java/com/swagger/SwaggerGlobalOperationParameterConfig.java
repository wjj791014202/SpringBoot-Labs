package com.swagger;

import java.util.List;
import springfox.documentation.service.Parameter;

public interface SwaggerGlobalOperationParameterConfig {
    List<Parameter> getGlobalOperationParameter();
}
