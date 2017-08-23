package com.test4x.lib.oauth_jwt;

import com.test4x.lib.oauth_jwt.condition.OAuthJwtNotOnWscAdapter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.util.ClassUtils;

@Configuration
@Conditional(OAuthJwtNotOnWscAdapter.class)
public class DefaultOAuthJwtConfigure extends WebSecurityConfigurerAdapter implements Ordered {

    private final ApplicationContext applicationContext;

    public DefaultOAuthJwtConfigure(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**").authorizeRequests().anyRequest().authenticated();
        OauthJwtConfigurer configurer = new OauthJwtConfigurer(this.applicationContext);
        configurer.configure(http);
    }

    @Override
    public int getOrder() {
        if (ClassUtils.isPresent(
                "org.springframework.boot.actuate.autoconfigure.ManagementServerProperties",
                null)) {
            // If > BASIC_AUTH_ORDER then the existing rules for the actuator
            // endpoints will take precedence. This value is < BASIC_AUTH_ORDER.
            return SecurityProperties.ACCESS_OVERRIDE_ORDER - 5;
        }
        return SecurityProperties.ACCESS_OVERRIDE_ORDER;
    }

}
