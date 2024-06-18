package allofhealth.messenger.auth;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthRepository extends R2dbcRepository<User_Auth, String> {
}