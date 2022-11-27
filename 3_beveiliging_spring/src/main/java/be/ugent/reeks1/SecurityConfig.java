package be.ugent.reeks1;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Value("${users.admin.password}")
    private String adminPassword;

    @Value("${users.admin.username}")
    private String adminUsername;

    @Value("${users.admin.encoded_password}")
    private String adminEncodedPassword;

    @Bean
    public DataSource datasource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
                .build();
    };

    // Configuration for jdbc authentication
    @Bean
    public UserDetailsManager users(DataSource datasource) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        Logger.getLogger(SecurityConfig.class).info("Encoded password: " + encoder.encode(adminPassword));
        UserDetails admin = User.withUsername(adminUsername).password(encoder.encode(adminPassword)).roles("ADMIN").build();
        // Better use an externally hashed password to avoid clear text passwords in source or memory
        UserDetails admin2 = User.withUsername("admin2").password(adminEncodedPassword).roles("ADMIN").build();
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(datasource);
        users.createUser(admin);
        users.createUser(admin2);
        return users;
    }

    // Configuration for in-memory authentication
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails user = User.withUsername("test").password("test").roles("ADMIN").build();
//        return new InMemoryUserDetailsManager(user);
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .mvcMatchers("/admin.html").hasRole("ADMIN")
            .anyRequest().permitAll()
            .and().httpBasic(Customizer.withDefaults())
            .csrf().disable()
            .headers().frameOptions().sameOrigin();
        return http.build();
    }
}
