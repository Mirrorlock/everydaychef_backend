package everydaychef.api.config;

import everydaychef.api.MainApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
public class StaticResourceConfiguration extends WebConfig {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**").addResourceLocations("file:" + MainApplication.IMAGE_DIR);
        super.addResourceHandlers(registry);
    }
}
