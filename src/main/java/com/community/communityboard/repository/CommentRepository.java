package com.community.communityboard.repository;

import com.community.communityboard.domain.Comment;
import com.community.communityboard.domain.Post;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  // 게시글에 달린 모든 댓글들(부모/자식 구분 없이, 최신순)
  @Query("""
    SELECT c
    FROM Comment c
    WHERE c.post = :post
    ORDER BY c.createdAt DESC
    """)
  Page<Comment> findAllCommentsByPost(Post post, Pageable pageable);

  // 특정 댓글 + deletedAt이 NULL
  @Query("""
    SELECT c
    FROM Comment c
    WHERE c.id = :id
      AND c.deletedAt IS NULL
    """)
  Optional<Comment> findActiveById(Long id);
}
