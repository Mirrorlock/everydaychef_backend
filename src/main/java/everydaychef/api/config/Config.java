package everydaychef.api.config;

import everydaychef.api.service.NotificationsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public NotificationsService getNotificationService(){
        return new NotificationsService();
    }
}
