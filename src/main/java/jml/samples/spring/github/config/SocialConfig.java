package jml.samples.spring.github.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.web.ReconnectFilter;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;

/**
 * Spring Social Configuration.
 * This configuration is demonstrating the use of the simplified Spring Social configuration options from Spring Social 1.1.
 * 
 * @author Craig Walls
 */
@Configuration
@EnableSocial
public class SocialConfig extends SocialConfigurerAdapter {

	
	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
	public GitHub github(ConnectionRepository repository) {
		Connection<GitHub> connection = repository.findPrimaryConnection(GitHub.class);
		return connection != null ? connection.getApi() : null;
	}
	
	
	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) 
	{
		GitHubConnectionFactory gitHubConnectionFactory = new GitHubConnectionFactory(env.getProperty("github.appKey"), env.getProperty("github.appSecret"));
		gitHubConnectionFactory.setScope("repo user");
		cfConfig.addConnectionFactory(gitHubConnectionFactory);
	}
	
	@Override
	public UserIdSource getUserIdSource() {
		return new AuthenticationNameUserIdSource();
	}
	
	@Inject private ConnectionSignUp connectionSignUp;
	
	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		InMemoryUsersConnectionRepository connectionRepository = new InMemoryUsersConnectionRepository(connectionFactoryLocator);
		connectionRepository.setConnectionSignUp(connectionSignUp);
		return connectionRepository;
	}

	@Bean
	public ReconnectFilter apiExceptionHandler(UsersConnectionRepository usersConnectionRepository, UserIdSource userIdSource) {
		return new ReconnectFilter(usersConnectionRepository, userIdSource);
	}
	
//    @Bean
//    public ConnectController connectController() {
//        ConnectController controller = new ConnectController(
//            connectionFactoryLocator(), connectionRepository());
//        controller.setApplicationUrl("http://localhost:8080");
//        return controller;
//    }

	
//	@Bean
//	public ProviderSignInController providerSignInController(
//	            ConnectionFactoryLocator connectionFactoryLocator,
//	            UsersConnectionRepository usersConnectionRepository) {
//	    return new ProviderSignInController(
//	        connectionFactoryLocator,
//	        usersConnectionRepository,
//	        new SimpleSignInAdapter(new HttpSessionRequestCache()));
//	}

}