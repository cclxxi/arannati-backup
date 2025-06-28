package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Page<Wishlist> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Wishlist> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    long countByUserId(Long userId);

    @Query("SELECT w.product.id, COUNT(w) as wishlistCount FROM Wishlist w " +
            "GROUP BY w.product.id ORDER BY wishlistCount DESC")
    List<Object[]> findMostWishedProducts(Pageable pageable);

    @Query("SELECT w FROM Wishlist w JOIN FETCH w.product p JOIN FETCH p.brand WHERE w.user.id = :userId AND p.active = true")
    List<Wishlist> findActiveWishlistItemsByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Wishlist w WHERE w.user.id = :userId AND w.product.id = :productId")
    void deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);


}
