package com.example.community.image;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

class ImageRepositoryImpl implements ImageRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Image> findByUserUserIdInAndActiveTrue(List<Integer> userIds) {
        return entityManager.createQuery(
                        "SELECT i FROM Image i WHERE i.user.userId IN :userIds AND i.active = true",
                        Image.class)
                .unwrap(org.hibernate.query.Query.class)
                .setParameterList("userIds", userIds)
                .getResultList();
    }
}
