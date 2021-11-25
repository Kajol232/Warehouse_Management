package com.muhammad.warehouse_management.config;

import com.muhammad.warehouse_management.config.JWTConfig.JWTAccessDeniedHandler;
import com.muhammad.warehouse_management.config.JWTConfig.JWTAuthenticationEntryPoint;
import com.muhammad.warehouse_management.config.JWTConfig.JWTAuthorizationFilter;
import com.muhammad.warehouse_management.config.constant.SecurityConstant;
import com.muhammad.warehouse_management.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private JWTAuthorizationFilter authorizationFilter;
    private JWTAccessDeniedHandler accessDeniedHandler;
    private JWTAuthenticationEntryPoint authenticationEntryPoint;
    private UserServiceImpl userService;
    private BCryptPasswordEncoder passwordEncoder;


    public SecurityConfiguration(JWTAuthorizationFilter authorizationFilter, JWTAccessDeniedHandler accessDeniedHandler,
                                 JWTAuthenticationEntryPoint authenticationEntryPoint, UserServiceImpl userService,
                                 BCryptPasswordEncoder passwordEncoder){
        this.authorizationFilter = authorizationFilter;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                //.and().authorizeRequests().antMatchers(SecurityConstant.PUBLIC_URL).permitAll()
                .and().authorizeRequests().antMatchers(AUTH_WHITELIST).permitAll()
                .and().authorizeRequests().antMatchers(ADMIN_WHITELIST).hasRole("ADMIN")
                .and().authorizeRequests().antMatchers(WORKER_USER_WHITELIST).hasRole("WORKER_USER")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);

    }
    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return  super.authenticationManagerBean();
    }
    private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",
            // other public endpoints of your API may be appended to this array
            "/user/login",
            "/user/resetPassword/**",
            "/user/register",
            "/auth/*"
    };
    private static final String[] ADMIN_WHITELIST = {
            "/user/delete"

    };
    private static final String[]  WORKER_USER_WHITELIST = {
            "/user/delete"

    };
}
