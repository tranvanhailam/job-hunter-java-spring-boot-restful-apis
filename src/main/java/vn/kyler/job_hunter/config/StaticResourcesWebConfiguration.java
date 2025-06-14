package vn.kyler.job_hunter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourcesWebConfiguration implements WebMvcConfigurer {
    @Value("${job-hunter.upload-file.base-uri}")
    private String baseUri;

    @Value("${job-hunter.upload-file.base-uri-in-project}")
    private String baseUriInProject;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/storage/**")
                .addResourceLocations(baseUri);
    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // Truy cập file từ thư mục thật trong hệ thống file
//        String filePath = Paths.get(System.getProperty("user.dir"), baseUriInProject).toUri().toString();
//
//        registry.addResourceHandler("/storage/**")
//                .addResourceLocations("file:" + filePath);
//    }
}
