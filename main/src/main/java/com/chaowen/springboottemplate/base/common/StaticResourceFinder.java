package com.chaowen.springboottemplate.base.common;

import java.util.Arrays;
import java.util.List;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

public class StaticResourceFinder {

    private final List<String> staticLocations;
    private final ResourceLoader resourceLoader;

    public StaticResourceFinder(List<String> staticLocations, ResourceLoader resourceLoader) {
        this.staticLocations = staticLocations;
        this.resourceLoader = resourceLoader;
    }

    public Resource findStaticResource(String path) {
        for (String location : staticLocations) {
            String fullPath = location + path;
            Resource resource = resourceLoader.getResource(fullPath);
            try {
                if (resource.exists() && resource.isReadable()) {
                    return resource;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }


  public static List<String> getConfiguredStaticLocations(WebProperties.Resources resources) {
    return Arrays.asList(resources.getStaticLocations());
  }
}
