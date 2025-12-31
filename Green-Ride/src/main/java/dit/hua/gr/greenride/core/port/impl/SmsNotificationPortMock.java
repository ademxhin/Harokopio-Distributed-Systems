package dit.hua.gr.greenride.core.port.impl;

import dit.hua.gr.greenride.core.port.SmsNotificationPort;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationPortMock implements SmsNotificationPort {

    @Override
    public boolean sendSms(String phone, String content) {
        System.out.println("MOCK SMS to " + phone + ": " + content);
        return true;
    }
}
