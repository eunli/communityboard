package com.community.communityboard.repository;

import com.community.communityboard.domain.Post;
import com.community.communityboard.domain.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  Optional<Post> findById(Long id);

  // 게시글 + 작성자 닉네임 검색 (title, content, user.nickname 중 하나라도 keyword를 포함하면 검색)
  @Query("""
    SELECT p
    FROM Post p
      JOIN p.user u
    WHERE lower(p.title)   LIKE lower(concat('%', :keyword, '%'))
       OR lower(p.content) LIKE lower(concat('%', :keyword, '%'))
       OR lower(u.nickname) LIKE lower(concat('%', :keyword, '%'))
    """)
  Page<Post> searchPostsByKeyword(String keyword, Pageable pageable);
}
