package jml.samples.spring.github.config;


import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.social.UserIdSource;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	
	@Bean
	public InMemoryUserDetailsManager userDetailsManager() {
		ArrayList<UserDetails> userDetails = new ArrayList<>();
		userDetails.add( new org.springframework.security.core.userdetails.User("jmlamare", "secret", Arrays.asList(new SimpleGrantedAuthority("ACCESS"))) );
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager(userDetails);
	    return manager;
	}
	
	@Autowired
	public void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.eraseCredentials(true)
			.userDetailsService(userDetailsManager())
			;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
				.antMatchers("/favicon.ico", "/resources/**", "/auth/**", "/signin/**", "/signup/**", "/disconnect/github").permitAll()
				.anyRequest().authenticated()
			.and().exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/auth/github"))
			.and().apply(new SpringSocialConfigurer())
			.and()
				.formLogin().disable()
				.logout().disable()
			;
	}

	
	@Bean
	public SocialUserDetailsService socialUsersDetailService() {
		return new SocialUserDetailsService() {

			final UserDetailsService userDetailsService = userDetailsService();
			
			@Override
			public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException 
			{
				UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
				return new SocialUser(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
			}
		};
	}
	
	@Bean
	public ConnectionSignUp connectionSignUp()
	{
		
		return new ConnectionSignUp() 
		{
			@Override
			public String execute(Connection<?> connection) 
			{
				if( !userDetailsManager().userExists(connection.getDisplayName()) )
				{
					UserDetails userDetails = new org.springframework.security.core.userdetails.User(connection.getDisplayName(), null, Arrays.asList(new SimpleGrantedAuthority("ACCESS")));
					userDetailsManager().createUser(userDetails);
				}
				return connection.getDisplayName();
			}
		};
	}
	
	@Bean
	public UserIdSource userIdSource() 
	{
		return new AuthenticationNameUserIdSource();
	}
	
}