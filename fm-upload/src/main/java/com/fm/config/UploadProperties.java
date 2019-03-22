package com.fm.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "fm.upload")
public class UploadProperties {

    private String baseUrl;
    private List<String> allowTypes;
}
