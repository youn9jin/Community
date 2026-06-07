package com.example.community.post.repository;

import com.example.community.post.Post;
import com.example.community.post.QPost;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.List;

@RequiredArgsConstructor  class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override // 인터페이스의 findActivePosts 메서드를 구현
    public Page<Post> findActivePosts(Pageable pageable) {

        List<Integer> ids = queryFactory
                .select(QPost.post.postId)
                .from(QPost.post) // post 테이블을 조회 대상으로 지정
                .where(QPost.post.deletedAt.isNull()) // delete 되지 않은 게시글만 조회
                .orderBy(QPost.post.createdAt.desc()) // 최신 게시글이 먼저 오도록 생성일 기준 내림차순 정렬
                .offset(pageable.getOffset()) // 현재 페이지가 시작되는 행 위치를 지정
                .limit(pageable.getPageSize()) // 한 페이지에 가져올 게시글 개수를 제한
                .fetch(); // 조건에 맞도록 실제 조회

        //조회한 postId가 없는 경우 처리
        if (CollectionUtils.isEmpty(ids)) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        List<Post> content = queryFactory
                .selectFrom(QPost.post)
                .where(QPost.post.postId.in(ids)) // 앞에서 페이징 처리한 postId 목록에 해당하는 게시글만 조회
                .orderBy(QPost.post.createdAt.desc())
                .fetch();

        long total = queryFactory
                .select(QPost.post.count())
                .from(QPost.post)
                .where(QPost.post.deletedAt.isNull())
                .fetchOne(); // 전체 개수 값을 단일 결과로 가져옴

        return new PageImpl<>(content, pageable, total); // 조회된 게시글 목록, 페이지 정보, 전체 개수를 Page 객체로 반환
    }
}
