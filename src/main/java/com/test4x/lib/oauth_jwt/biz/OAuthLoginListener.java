package com.test4x.lib.oauth_jwt.biz;

import com.test4x.lib.oauth_jwt.oauth.OAuthLoginBean;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;

import java.util.Map;

/**
 * 监听oauth登陆成功的event
 */
public class OAuthLoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {
    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        Class<?> generatedBy = event.getGeneratedBy();
        OAuthLoginBean source = (OAuthLoginBean) event.getSource();
        Map<String, Object> accessToken = source.accessToken;
        Map<String, Object> userInfo = source.userInfo;
        consume(accessToken, userInfo);
    }

    public void consume(Map<String, Object> accessToken, Map<String, Object> userInfo) {

    }
}
