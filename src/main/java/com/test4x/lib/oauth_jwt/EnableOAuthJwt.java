package com.test4x.lib.oauth_jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DefaultOAuthJwtConfigure.class, CustomOAuthJwtConfigure.class, CommonConfigure.class})
@EnableConfigurationProperties({OauthJwtProp.class})
public @interface EnableOAuthJwt {
}
