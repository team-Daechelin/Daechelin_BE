package daedeok.daechelin.domain.token.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken", timeToLive = 86400)
@AllArgsConstructor
@Getter
public class RefreshToken {
    @Id
    private String refreshToken;

    private String username;
}
