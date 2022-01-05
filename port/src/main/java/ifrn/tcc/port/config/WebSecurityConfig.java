package ifrn.tcc.port.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/portPlus").permitAll()
				.antMatchers("/perfil").hasRole("ALUNO")
				.antMatchers("/portPlus/criarCurso/**", "/perfil").hasRole("INSTRUTOR")
				.anyRequest().authenticated().and()
				.formLogin(form -> form.loginPage("/portPlus/login").defaultSuccessUrl("/portPlus", true).permitAll()).csrf()
				.disable();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/Styles/**", "/Imagens/**");
	}
}
