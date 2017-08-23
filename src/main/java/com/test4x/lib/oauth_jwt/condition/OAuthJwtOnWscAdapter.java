package com.test4x.lib.oauth_jwt.condition;

import com.test4x.lib.oauth_jwt.EnableOAuthJwt;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 判断是否
 * EnableOAuthJwt annotation on WebSecurityConfigurerAdapter
 */
public class OAuthJwtOnWscAdapter extends SpringBootCondition {
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context,
                                            AnnotatedTypeMetadata metadata) {
        String[] enablers = context.getBeanFactory()
                .getBeanNamesForAnnotation(EnableOAuthJwt.class);
        ConditionMessage.Builder message = ConditionMessage
                .forCondition("@EnableOAuthJwt Condition");
        for (String name : enablers) {
            if (context.getBeanFactory().isTypeMatch(name,
                    WebSecurityConfigurerAdapter.class)) {
                return ConditionOutcome.match(message
                        .found("@EnableOAuthJwt annotation on WebSecurityConfigurerAdapter")
                        .items(name));
            }
        }
        return ConditionOutcome.noMatch(message.didNotFind(
                "@EnableOAuthJwt annotation " + "on any WebSecurityConfigurerAdapter")
                .atAll());
    }

}
