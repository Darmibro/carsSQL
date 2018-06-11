package telran.security.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import telran.security.AccountMongo;

@Repository
public interface AccountRepository extends MongoRepository<AccountMongo, String>{

}
