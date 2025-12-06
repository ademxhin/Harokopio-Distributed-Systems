package dit.hua.gr.greenride.core.port;

public interface SmsNotificationPort {

    boolean sendSms(final String e164, final String content);
}
