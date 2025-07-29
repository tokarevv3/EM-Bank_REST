package com.example.bankcards.util;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CardSpecifications {

    public static Specification<Card> byUserAndStatus(User user, CardFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("user"), user));
            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), Status.valueOf(filter.status())));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
