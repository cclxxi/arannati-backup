package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.CosmetologistVerification;
import kz.arannati.arannati.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CosmetologistVerificationRepository extends JpaRepository<CosmetologistVerification, Long> {

    Optional<CosmetologistVerification> findByUserId(Long userId);

    Page<CosmetologistVerification> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    List<CosmetologistVerification> findByStatusOrderByCreatedAtDesc(String status);

    @Query("SELECT cv FROM CosmetologistVerification cv WHERE cv.status = 'PENDING' " +
            "ORDER BY cv.createdAt ASC")
    Page<CosmetologistVerification> findPendingVerifications(Pageable pageable);

    @Query("SELECT cv FROM CosmetologistVerification cv WHERE cv.verifiedBy.id = :adminId " +
            "ORDER BY cv.verifiedAt DESC")
    Page<CosmetologistVerification> findVerificationsByAdmin(@Param("adminId") Long adminId, Pageable pageable);

    boolean existsByUserIdAndStatus(Long userId, String status);

    long countByStatus(String status);

    long countByVerifiedBy(User verifiedBy);
}
