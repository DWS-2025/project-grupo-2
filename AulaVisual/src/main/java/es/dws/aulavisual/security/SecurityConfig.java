package es.dws.aulavisual.security;

import es.dws.aulavisual.security.jwt.JwtRequestFilter;
import es.dws.aulavisual.security.jwt.UnauthorizedHandlerJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	RepositoryUserDetailsService userDetailsService;

	@Autowired
	private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	static RoleHierarchy roleHierarchy() {
		return RoleHierarchyImpl.withDefaultRolePrefix()
				.role("ADMIN").implies("TEACHER")
				.role("TEACHER").implies("USER")
				.role("USER").implies("ANONYMOUS")
				.build();
	}

	@Bean
	static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setRoleHierarchy(roleHierarchy);
		return expressionHandler;
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

		String commonPath = "/api/";

		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setRoleHierarchy(roleHierarchy());

		http.authenticationProvider(authenticationProvider());
		
		http
			.securityMatcher("/api/**")
			.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));
		
		http
			.authorizeHttpRequests(authorize -> authorize
                    // PRIVATE ENDPOINTS
					//USERS
					.requestMatchers(HttpMethod.GET, "/api/users/*/").hasRole("USER")
					.requestMatchers(HttpMethod.PUT, "/api/users/*/").hasRole("USER")
					.requestMatchers(HttpMethod.PUT, "/api/users/*/image/").hasRole("USER")
					.requestMatchers(HttpMethod.GET, "/api/users/*/image/").hasRole("USER")
					.requestMatchers(HttpMethod.DELETE, "/api/users/*/").hasRole("ADMIN")
					//SUBMISSIONS
					.requestMatchers(HttpMethod.GET, "/api/course/*/submissions").hasRole("TEACHER")
					.requestMatchers(HttpMethod.GET, "/api/user/*/submissions").hasRole("USER")
					.requestMatchers(HttpMethod.GET, "/api/submission/*").hasRole("USER")
					.requestMatchers(HttpMethod.POST, "/api/submission/").hasRole("USER")
					.requestMatchers(HttpMethod.PUT, "/api/submission/*/content").hasRole("USER")
					.requestMatchers(HttpMethod.GET, "/api/submission/*/content").hasRole("USER")
					.requestMatchers(HttpMethod.PUT, "/api/submission/*/grade").hasRole("TEACHER")
					.requestMatchers(HttpMethod.DELETE, "/api/submission/*").hasRole("TEACHER")
					.requestMatchers(HttpMethod.GET, "/api/course/*/submission/*/comments").hasRole("USER")
					.requestMatchers(HttpMethod.POST, "/api/course/*/submission/*/comment").hasRole("USER")
					//COURSES
					.requestMatchers(HttpMethod.GET, "/api/courses/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET, "/api/user/*/courses/").hasRole("USER")
					.requestMatchers(HttpMethod.GET, "/api/course/*/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.POST, "/api/courses/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET, "/api/course/*/users/").hasRole("TEACHER")
					.requestMatchers(HttpMethod.DELETE, "/api/course/*/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.PUT, "/api/course/*/image/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET, "/api/course/*/image/").hasRole("USER")
					.requestMatchers(HttpMethod.PUT, "/api/course/*/").hasRole("ADMIN")
					//MODULES
					.requestMatchers(HttpMethod.GET, "/api/course/*/modules/").hasRole("USER")
					.requestMatchers(HttpMethod.GET, "/api/course/*/module/*/").hasRole("USER")
					.requestMatchers(HttpMethod.GET, "/api/course/*/module/*/content/").hasRole("USER")
					.requestMatchers(HttpMethod.PUT, "/api/course/*/module/*/content/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.POST, "/api/course/*/module/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.DELETE, "/api/course/*/module/*/").hasRole("ADMIN")
					// PUBLIC ENDPOINTS
					.requestMatchers(HttpMethod.POST, "/api/users/").permitAll() //register
					.requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/users/").permitAll() //if not conflict vs webFilterChain
			);
		
        // Disable Form login Authentication
        http.formLogin(AbstractHttpConfigurer::disable);

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(AbstractHttpConfigurer::disable);

        // Disable Basic Authentication
        http.httpBasic(AbstractHttpConfigurer::disable);

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Add JWT Token filter
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setRoleHierarchy(roleHierarchy());

		http.authenticationProvider(authenticationProvider());

		http
				.authorizeHttpRequests(authorize -> authorize
						// PUBLIC PAGES
						.requestMatchers("/").permitAll()
						.requestMatchers("/register").permitAll()
						.requestMatchers("/images/**").permitAll()
						.requestMatchers("/css/**").permitAll()
						.requestMatchers("/js/**").permitAll()
						.requestMatchers("/error").permitAll()
						.requestMatchers("/login/success").permitAll()
						.requestMatchers("/users/{id}/delete").permitAll()
						// PRIVATE PAGES
						.requestMatchers("/admin/**").hasAnyRole("ADMIN")
						.requestMatchers("/courses/**").hasAnyRole("USER")
						.requestMatchers("/teacher/**").hasAnyRole("TEACHER")
						.requestMatchers("/user_pfp/*").hasAnyRole("USER")
						.requestMatchers("/profile/**").hasAnyRole("USER")
						.requestMatchers("/**").permitAll()
				)
				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.failureUrl("/loginerror")
						.defaultSuccessUrl("/login/success")
						.permitAll()
				)
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll()
				)
				.exceptionHandling(ex -> ex
						.accessDeniedHandler(customAccessDeniedHandler) // Solo para 403
				);

		return http.build();
	}
}
