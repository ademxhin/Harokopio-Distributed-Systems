package dit.hua.gr.greenride.core.port;

public interface SmsNotificationPort {

    /**
     * Sends an SMS message to the given phone number.
     *
     * @param phone   the phone number in E.164 format
     * @param content the message content
     * @return true if the SMS was sent successfully, false otherwise
     */
    boolean sendSms(String phone, String content);
}
