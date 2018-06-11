package telran.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.security.repo.AccountRepository;

@Service
public class Accountslmpl implements IAccounts{
@Autowired
AccountRepository accounts;

	@Override
	public String getPassword(String username) {
		AccountMongo accountMongo=accounts.findById(username).orElse(null);
		return accountMongo.getPassword();
	}

	@Override
	public String[] getRoles(String username) {
		AccountMongo accountMongo=accounts.findById(username).get();
		return accountMongo.getRoles();
	}
	
	@Override
	public boolean checkUsername(String username) {
		return accounts.existsById(username);	
		//return accounts.findById(username).orElse(null)==null?false:true;
		}

}
