package dblab.sharing_platform.config.security;

import dblab.sharing_platform.config.oauth.PrincipalOauth2UserService;
import dblab.sharing_platform.config.security.detailsService.MemberDetailsService;
import dblab.sharing_platform.config.security.handler.JwtAccessDeniedHandler;
import dblab.sharing_platform.config.security.handler.JwtAuthenticationEntryPoint;
import dblab.sharing_platform.config.security.jwt.filter.JwtExceptionFilter;
import dblab.sharing_platform.config.security.jwt.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final MemberDetailsService memberDetailsService;
    private final JwtFilter jwtFilter;
    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfigurationSource());

        http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtFilter.class)
                .userDetailsService(memberDetailsService)

                .authorizeRequests()
                .antMatchers( "/api/auth/sign-up", "/api/auth/login").permitAll()
                .antMatchers("/swagger-uri/**", "/swagger-resources/**", "/v3/api-docs/**").permitAll()

                .antMatchers(HttpMethod.GET, "/api/members").permitAll()
                .antMatchers(HttpMethod.GET, "/api/members/my-profile").permitAll()
                .antMatchers(HttpMethod.PATCH, "/api/members/oauth").permitAll()

                .antMatchers(HttpMethod.DELETE, "/api/category/**").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/category/**").hasAuthority("ADMIN")

                .antMatchers(HttpMethod.GET, "/api/messages/received").permitAll()
                .antMatchers(HttpMethod.GET, "/api/messages/sent").permitAll()
                .antMatchers(HttpMethod.GET, "/api/messages/{messageId}").access("@sendMessageGuard.check(#messageId) or @receiveMessageGuard.check(#messageId)")
                .antMatchers(HttpMethod.DELETE, "/api/messages/{messageId}/sent").access("@sendMessageGuard.check(#messageId)")
                .antMatchers(HttpMethod.DELETE, "/api/messages/{messageId}/received").access("@receiveMessageGuard.check(#messageId)")

                .antMatchers(HttpMethod.DELETE, "/api/posts/{postId}").access("@postGuard.check(#postId)")
                .antMatchers(HttpMethod.PATCH, "/api/posts/{postId}").access("@postGuard.check(#postId)")

                .antMatchers(HttpMethod.DELETE, "/api/comments/{commentId}").access("@commentGuard.check(#commentId)")

                .antMatchers(HttpMethod.POST, "/api/trades/{postId}").access("@postGuard.check(#postId)")
                .antMatchers(HttpMethod.PATCH, "/api/trades/trade/{tradeId}").access("@tradeGuard.check(#tradeId)")
                .antMatchers(HttpMethod.DELETE, "/api/trades/{tradeId}").access("@tradeGuard.check(#tradeId)")

                .antMatchers(HttpMethod.POST, "/api/reviews/{tradeId}").access("@reviewGuard.check(#tradeId)")
                .antMatchers(HttpMethod.DELETE, "/api/reviews/{tradeId}").access("@reviewGuard.check(#tradeId)")
                .antMatchers(HttpMethod.GET, "/api/reviews/all").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/reviews").hasAnyAuthority("ADMIN", "MANAGER", "USER")

                .antMatchers(HttpMethod.DELETE, "/api/reports/{reportId}").access("@reportGuard.check(#reportId)")
                .antMatchers(HttpMethod.GET, "/api/reports").hasAnyAuthority("ADMIN")

                .and()
                .oauth2Login()
                .userInfoEndpoint().userService(principalOauth2UserService);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);

        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        FilterRegistrationBean bean
                = new FilterRegistrationBean(new CorsFilter(source));

        bean.setOrder(0);
        return source;
    }

}
