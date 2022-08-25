package com.yu.reggie_take_out.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class Sms {
    private static String accountSID;
    private static String authToken;
    private static String trialNumber;
    @Value("${twilio.accountSID}")
    public void setAccountSID(String sAccountSID)
    {
        accountSID = sAccountSID;
    }


    @Value("${twilio.authToken}")
    public void setAuthToken(String sAuthToken)
    {
        authToken = sAuthToken;
    }

    @Value("${twilio.trialNumber}")
    public void setTrialNumber(String sTrialNumber)
    {
        System.out.println(sTrialNumber);
        Sms.trialNumber = sTrialNumber;
    }

    public static void send(String msg, String phone) {
        msg = "Reggi:Verify code " + msg;
        Twilio.init(accountSID, authToken);
        Message message = Message.creator(new PhoneNumber(phone),
                new PhoneNumber(trialNumber),
                msg).create();
        System.out.println(message.getSid());
    }
}
