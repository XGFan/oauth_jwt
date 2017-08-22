package com.test4x.lib.oauth_jwt;

import com.test4x.lib.oauth_jwt.oauth.OAuthProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({OAuthJwtConfigure.class})
@EnableConfigurationProperties({OAuthProperties.class})
public @interface EnableOAuthJwt {
}
