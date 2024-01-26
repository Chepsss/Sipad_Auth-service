package it.almaviva.difesa.cessazione.auth.data.repository;

import it.almaviva.difesa.cessazione.auth.data.common.GenericRepository;
import it.almaviva.difesa.cessazione.auth.data.entity.UserToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends GenericRepository<UserToken, Long> {

    Optional<UserToken> findByUuid(String uuid);

    Optional<UserToken> findByRefreshToken(String refreshToken);

    Optional<UserToken> findByInsertCode(String insertCode);

    @Modifying
    @Query("delete from UserToken ut where ut.insertCode = :fiscalCode")
    void deleteByInsertCode(String fiscalCode);

    default void deleteUserByFiscalCodeAndFlush(String fiscalCode) {
        deleteByInsertCode(fiscalCode);
        flush();
    }

    Optional<UserToken> findByUserId(Long userId);

}
