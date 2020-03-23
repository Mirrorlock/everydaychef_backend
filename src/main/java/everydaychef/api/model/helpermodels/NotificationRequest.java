package everydaychef.api.model.helpermodels;

import everydaychef.api.model.Device;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationRequest {
    private ArrayList<Device> target;
    private String title;
    private String body;

    public ArrayList<Device> getTarget() {
        return target;
    }

    public void setTarget(ArrayList<Device> target) {
        this.target = target;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public NotificationRequest(ArrayList<Device> target, String title, String body) {
        this.target = target;
        this.title = title;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationRequest that = (NotificationRequest) o;
        return Objects.equals(target, that.target) &&
                Objects.equals(title, that.title) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, title, body);
    }

    @Override
    public String toString() {
        return "NotificationRequest{" +
                "target='" + target + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
