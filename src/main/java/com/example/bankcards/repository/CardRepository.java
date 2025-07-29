package com.example.bankcards.repository;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.util.CardSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    List<Card> findAllByOwner(User owner);

    default Page<Card> findByUserWithFilter(User user, CardFilter filter, Pageable pageable) {
        Specification<Card> spec = CardSpecifications.byUserAndStatus(user, filter);
        return findAll(spec, pageable);
    }
}
