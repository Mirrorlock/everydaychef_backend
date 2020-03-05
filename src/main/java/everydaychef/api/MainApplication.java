package everydaychef.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import everydaychef.api.model.helpermodels.FileStorageProperties;
import org.jboss.jandex.Main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class MainApplication extends SpringBootServletInitializer {

    public static String IMAGE_DIR;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MainApplication.class);
    }

    public static void main(String[] args) throws IOException {
        IMAGE_DIR = new File(".").getCanonicalPath() + "/img/";
        SpringApplication.run(MainApplication.class, args);
    }
}
