package daedeok.daechelin.domain.token.repository;

import daedeok.daechelin.domain.token.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}
