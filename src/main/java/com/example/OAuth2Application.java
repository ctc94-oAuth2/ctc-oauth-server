package com.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.Data; 

@SpringBootApplication
@EnableWebSecurity
public class OAuth2Application extends WebSecurityConfigurerAdapter {
	//
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//
		http.authorizeRequests().anyRequest().authenticated()
			.and()
				.formLogin()
			.and()
				.csrf().disable()
				.logout();
	}
 
	/** 
	 * API를 조회시 출력될 테스트 데이터 
	 * @param memberRepository 
	 * @return 
	 */ 
	@Bean 
	public CommandLineRunner commandLineRunner(MemberRepository memberRepository) {
		return args -> { 
			memberRepository.save(new Member("이철수", "chulsoo", "test111"));
			memberRepository.save(new Member("김정인", "jungin11", "test222"));
			memberRepository.save(new Member("류정우", "jwryu991", "test333"));
		}; 
	} 
	
	
	/*@Bean
	public TokenStore jdbcTokenStore(DataSource dataSource) {
		return new JdbcTokenStore(dataSource);
	}*/

	public static void main(String[] args) {
		SpringApplication.run(OAuth2Application.class, args);
	}
}

@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfiguration {
	//
	private static final Logger log = LoggerFactory.getLogger(AuthorizationServerConfiguration.class);
	
/*	@Autowired
	private AuthorizationCodeServices authorizationCodeServices;
	
	@Autowired
	private ApprovalStore approvalStore;*/
	
	/*@Autowired
	private TokenStore tokenStore;
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		//
		endpoints
			.requestValidator(new AuthRequestValidator())
			.tokenStore(tokenStore);
			//.authorizationCodeServices(authorizationCodeServices)
			//.approvalStore(approvalStore);
	}*/

//	@Override
//	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
//		//
//	}
	
/*	@Bean
	public AuthorizationCodeServices jdbcAuthorizationCodeServices(DataSource dataSource) {
		//
		log.debug("\n## in jdbcAuthorizationCodeServices()");
		return new JdbcAuthorizationCodeServices(dataSource);
	}
	
	@Bean
	public ApprovalStore jdbcApprovalStore(DataSource dataSource) {
		//
		log.debug("\n## in jdbcApprovalStore()");
		return new JdbcApprovalStore(dataSource);
	}*/
	
/*	@Bean
	@Primary
	public ClientDetailsService jdbcClientDetailsService(DataSource dataSource) {
		//
		log.debug("\n## in jdbcClientDetailsService()");
		return new JdbcClientDetailsService(dataSource);
	}*/
	
	@Bean
	public TokenStore jdbcTokenStore(DataSource dataSource) {
		//
		log.debug("\n## in jdbcTokenStore()");
		return new JdbcTokenStore(dataSource);
	}

}


@RepositoryRestResource 
interface MemberRepository extends PagingAndSortingRepository<Member, Long> {}

@Data 
@Entity 
class Member { 
	@Id 
	@GeneratedValue 
	Long id;
	String name;
	String username;
	String remark;
	public Member() {} 
	public Member(String name, String username, String remark) {
		this.name = name;
		this.username = username;
		this.remark = remark;
	} 
} 

/** 
 * 권한 코드 테스트를 위해 만든 컨트롤러 
 */ 
@Controller 
@RequestMapping("test") 
class TestController { 
	@RequestMapping("authorization-code") 
	@ResponseBody 
	public String authorizationCodeTest(@RequestParam("code") String code) {
		String curl = String.format("curl " +
				"-F \"grant_type=authorization_code\" " + 
				"-F \"code=%s\" " + 
				"-F \"scope=read\" " + 
				"-F \"client_id=foo\" " + 
				"-F \"client_secret=bar\" " + 
				"-F \"redirect_uri=http://localhost:8080/test/authorization-code\" " + 
				"\"http://foo:bar@localhost:8080/oauth/token\"", code);
		return curl;
	} 
} 


/** 
 * 권한 코드 테스트를 위해 만든 컨트롤러 
 */ 
@Controller 
@RequestMapping("/") 
class HomeController { 
	@RequestMapping("home") 
	@ResponseBody 
	public String home() {
		return "home";
	}
}