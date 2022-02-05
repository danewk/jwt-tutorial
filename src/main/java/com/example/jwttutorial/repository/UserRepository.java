package com.example.jwttutorial.repository;

import com.example.jwttutorial.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  @EntityGraph(attributePaths = "authorities") //@EntityGraph 쿼리수행시 eager로 동작
  Optional<User> findOneWithAuthoritiesByUsername(String username);
}
