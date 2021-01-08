package ua.ies.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Qualifier("userDetailsServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /*
    @Override
    protected void configure(HttpSecurity http) throws Exception 
    {
        http
         .csrf().disable()
         .authorizeRequests().anyRequest().authenticated()
         .and()
         .httpBasic();
    }
    */
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // added
            .authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/registration").permitAll() // TODO meter o post (ou os 2) do /api/users pra dar pra todos
            //.antMatchers("/css/**", "/js/**", "/login").permitAll() // TODO meter o post (ou os 2) do /api/users pra dar pra todos
                .anyRequest().authenticated()
            .and() // added
                // added
                //.httpBasic()
                //.and()
                // ate aqui



            .formLogin()
                .loginPage("/login")
                .permitAll()
            .and()
                // added
                // .httpBasic()
                // .and()

                // ate aqui
            .logout()
                .permitAll()
                .and()
                .httpBasic() // added
                ;//.and()
    
    }
    

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
}