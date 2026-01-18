package com.fa.training.config;

import com.fa.training.constant.FileConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory(FileConstants.UPLOAD_URL_PREFIX, FileConstants.UPLOAD_DIR, registry);
    }

    private void exposeDirectory(String urlPattern, String uploadDir, ResourceHandlerRegistry registry) {
        Path path = Paths.get(uploadDir);
        String absolutePath = path.toFile().getAbsolutePath();

        if (urlPattern.endsWith("/")) {
            urlPattern = urlPattern + "**";
        } else {
            urlPattern = urlPattern + "/**";
        }

        registry.addResourceHandler(urlPattern)
                .addResourceLocations("file:/" + absolutePath + "/");
    }
}
