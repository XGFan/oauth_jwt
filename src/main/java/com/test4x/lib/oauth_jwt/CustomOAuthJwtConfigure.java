package com.test4x.lib.oauth_jwt;

import com.test4x.lib.oauth_jwt.condition.OAuthJwtOnWscAdapter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

@Configuration
@Conditional(OAuthJwtOnWscAdapter.class)
public class CustomOAuthJwtConfigure implements ImportAware, BeanPostProcessor, ApplicationContextAware {
    private Class<?> configType;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.configType = ClassUtils.resolveClassName(importMetadata.getClassName(),
                null);
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        if (this.configType.isAssignableFrom(bean.getClass())
                && bean instanceof WebSecurityConfigurerAdapter) {
            OauthJwtConfigurer configurer = new OauthJwtConfigurer(this.applicationContext);
            ProxyFactory factory = new ProxyFactory();
            factory.setTarget(bean);
            factory.addAdvice(new OAuthJwtSecurityAdapter(configurer));
            bean = factory.getProxy();
        }
        return bean;
    }

    private static class OAuthJwtSecurityAdapter implements MethodInterceptor {

        private OauthJwtConfigurer configurer;

        OAuthJwtSecurityAdapter(OauthJwtConfigurer configurer) {
            this.configurer = configurer;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            if (invocation.getMethod().getName().equals("init")) {
                Method method = ReflectionUtils
                        .findMethod(WebSecurityConfigurerAdapter.class, "getHttp");
                ReflectionUtils.makeAccessible(method);
                HttpSecurity http = (HttpSecurity) ReflectionUtils.invokeMethod(method,
                        invocation.getThis());
                this.configurer.configure(http);
            }
            return invocation.proceed();
        }

    }
}
