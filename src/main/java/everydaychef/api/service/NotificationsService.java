package everydaychef.api.service;

import everydaychef.api.controller.UserController;
import everydaychef.api.model.Device;
import everydaychef.api.model.User;
import everydaychef.api.model.helpermodels.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.messaging.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;


public class NotificationsService {

    private Logger logger = LoggerFactory.getLogger(NotificationsService.class);

//    @Value("${firebase.notifications.send}")
//    private String firebaseNotifSendUrl;
//
//    @Value("${firebase.notifications.key}")
//    private String firebaseNotifSendKey;



    public Boolean[] sendNotificationToUser(NotificationRequest notificationRequest){
        ArrayList<String> firebaseTokens = notificationRequest.getTarget().stream().map(Device::getFirebaseToken).collect(Collectors.toCollection(ArrayList::new));
        Boolean[] result = new Boolean[firebaseTokens.size()];
        for (int i = 0; i < firebaseTokens.size(); i++) {
            result[i] = sendNotificationToDevice(firebaseTokens.get(i), notificationRequest);
        }
        return result;
    }

    private Boolean sendNotificationToDevice(String token, NotificationRequest notificationRequest) {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(new Notification(notificationRequest.getTitle(), notificationRequest.getBody()))
                .putData("content", notificationRequest.getTitle())
                .putData("body", notificationRequest.getBody())
                .build();


        boolean response = false;
        try {
            FirebaseMessaging.getInstance().send(message);
            response = true;
        } catch (FirebaseMessagingException e) {
//            logger.error("Fail to send firebase notification", e);
        }

        return response;
    }

}
