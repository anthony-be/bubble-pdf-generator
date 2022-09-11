package be.cocoding.bubblepdf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Value("${AUTHENTICATION_USER}")
    private String authenticationUser;

    @Value("${AUTHENTICATION_PASSWORD}")
    private String authenticationPassword;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .authorizeRequests().anyRequest().permitAll()
//                .authenticated()
//                .and()
//                .httpBasic().authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                .and()
                .build();
    }

//    @Bean
//    public UserDetailsService users() {
//        // The builder will ensure the passwords are encoded before saving in memory
//        User.UserBuilder users = User.withDefaultPasswordEncoder();
//        UserDetails user = users
//                .username(authenticationUser)
//                .password(authenticationPassword)
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }


}
