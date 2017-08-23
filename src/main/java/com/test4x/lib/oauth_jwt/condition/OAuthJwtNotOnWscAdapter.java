package com.test4x.lib.oauth_jwt.condition;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 判断是否
 * EnableOAuthJwt annotation *NOT* on WebSecurityConfigurerAdapter
 */
public class OAuthJwtNotOnWscAdapter extends OAuthJwtOnWscAdapter {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context,
                                            AnnotatedTypeMetadata metadata) {
        return ConditionOutcome.inverse(super.getMatchOutcome(context, metadata));
    }

}
