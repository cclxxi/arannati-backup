package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUserIdOrderByCreatedAtAsc(Long userId);

    Optional<Cart> findByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT c FROM Cart c JOIN c.product p " +
            "WHERE c.user.id = :userId AND p.active = true")
    List<Cart> findActiveCartItemsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user.id = :userId AND c.product.id = :productId")
    void deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    long countByUserId(Long userId);

    @Query("SELECT SUM(c.quantity) FROM Cart c WHERE c.user.id = :userId")
    Integer getTotalQuantityByUserId(@Param("userId") Long userId);
}
