package SE347.EasyTravel.security;

import SE347.EasyTravel.Config.OAuth2LoginSuccessHandler;
import SE347.EasyTravel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    private UserService userService;
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(UserService userService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userService = userService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService){
        DaoAuthenticationProvider dap = new DaoAuthenticationProvider();
        dap.setUserDetailsService(this.userService);
        dap.setPasswordEncoder(passwordEncoder());
        return dap;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)throws Exception{
        return config.getAuthenticationManager();
    }

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(csrf->csrf.disable())
                .cors(cors -> {
                    cors.configurationSource(request -> {
                        CorsConfiguration corsConfig = new CorsConfiguration();
                        corsConfig.addAllowedOriginPattern(EndPoints.frontend_host);
                        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                        corsConfig.addAllowedHeader("*");
                        corsConfig.setAllowCredentials(true);
                        return corsConfig;
                    });
                })

                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(config->config
                        .requestMatchers("/account/detail").authenticated()
                        .requestMatchers(HttpMethod.POST, EndPoints.PUBLIC_POST_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, EndPoints.PUBLIC_GET_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.DELETE, EndPoints.AUTH_DELETE_ENDPOINTS).hasAnyAuthority("ADMIN", "HOTEL_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/payment/vn-pay-callback").permitAll()
                        .requestMatchers(HttpMethod.GET,EndPoints.ADMIN_GET_ENDPOINTS).hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET,EndPoints.TG_GET_ENDPOINTS).hasAnyAuthority("ADMIN", "TOUR_GUIDE")
                        .requestMatchers(HttpMethod.GET,EndPoints.HM_GET_ENDPOINTS).hasAnyAuthority("ADMIN", "HOTEL_MANAGER")
                        .requestMatchers(HttpMethod.POST, EndPoints.ADMIN_POST_ENDPOINTS).hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, EndPoints.ADMIN_DELETE_ENDPOINTS).hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, EndPoints.ADMIN_PUT_ENDPOINTS).hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, EndPoints.HM_POST_ENDPOINTS).hasAnyAuthority("HOTEL_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, EndPoints.HM_DELETE_ENDPOINTS).hasAnyAuthority("HOTEL_MANAGER")
                        .requestMatchers(HttpMethod.PUT, EndPoints.HM_PUT_ENDPOINTS).hasAnyAuthority("HOTEL_MANAGER")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                )
                .authenticationProvider(authenticationProvider(userService))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
