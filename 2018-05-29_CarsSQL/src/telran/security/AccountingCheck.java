package telran.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



@Configuration
public class AccountingCheck implements UserDetailsService{
	@Autowired
	IAccounts accounts;

	/*@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (!accounts.checkUsername(username)) {
			throw new UsernameNotFoundException("");
		}
		
		return new User(username, accounts.getPassword(username), AuthorityUtils.createAuthorityList(accounts.getRoles(username)));
	}
	
	@Bean
	 public static NoOpPasswordEncoder passwordEncoder() {
	 return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	 }*/
	
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	  if (!accounts.checkUsername(username))
	   throw new UsernameNotFoundException("user not found");
	  PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	  UserDetails user = User.withUsername(username).password(encoder.encode(accounts.getPassword(username)))
			  .authorities(accounts.getRoles(username))
	    .build();
	  return user;
	 }


}
