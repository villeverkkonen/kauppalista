package kauppalista.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    // Määritellään mihin polkuihin kukin pääsee.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();                      // Poistetaan csrf-tarkistus käytöstä h2-konsolin vuoksi.
        http.headers().frameOptions().sameOrigin(); // Sallitaan framejen käyttö.
        http.authorizeRequests()
                .antMatchers("/kayttajat/**").hasAnyAuthority("USER"); // Kauppalista vain usereille.

        http.formLogin() // Kaikki pääsee kirjautumaan sisään ja ulos.
                .loginPage("/login")
                .defaultSuccessUrl("/etusivu")
                .failureUrl("/kirjautuminen")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/etusivu");      // Uloskirjautuminen ohjaa etusivulle.
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    // Palauttaa bcryptillä salasanasta lasketun kryptografisen tiivisteen (hash).
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
