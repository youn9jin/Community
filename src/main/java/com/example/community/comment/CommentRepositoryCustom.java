package com.example.community.comment;

import java.util.List;

public interface CommentRepositoryCustom {
    List<Object[]> countCommentsByPostIds(List<Integer> postIds);
}
