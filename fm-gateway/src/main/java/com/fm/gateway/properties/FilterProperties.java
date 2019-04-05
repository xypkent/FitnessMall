package com.fm.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "fm.filter")
public class FilterProperties {
    private List<String> allowPaths;
}
