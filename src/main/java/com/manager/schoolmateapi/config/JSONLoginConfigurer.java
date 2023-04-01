package com.manager.schoolmateapi.config;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class JSONLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
              AbstractAuthenticationFilterConfigurer<H, JSONLoginConfigurer<H>, UsernamePasswordAuthenticationFilter> {

        public JSONLoginConfigurer() {
            super(new JSONAuthenticationFilter(), null);
        }

        @Override
        public JSONLoginConfigurer<H> loginPage(String loginPage) {
            return super.loginPage(loginPage);
        }

        @Override
        protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
            return new AntPathRequestMatcher(loginProcessingUrl, "POST");
        }

    }
