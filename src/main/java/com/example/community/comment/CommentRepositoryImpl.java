package com.example.community.comment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

class CommentRepositoryImpl implements CommentRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> countCommentsByPostIds(List<Integer> postIds) {
        return entityManager.createQuery(
                        "SELECT c.post.postId AS postId, COUNT(c) AS count " +
                                "FROM Comment c " +
                                "WHERE c.post.postId IN :postIds AND c.deletedAt IS NULL " +
                                "GROUP BY c.post.postId",
                        Object[].class)
                .unwrap(org.hibernate.query.Query.class)
                .setParameterList("postIds", postIds)
                .getResultList();
    }
}
