package com.example.jwttutorial.service;

import com.example.jwttutorial.entity.User;
import com.example.jwttutorial.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findOneWithAuthoritiesByUsername(username)
        .map(user -> createUser(user, username))
        .orElseThrow(() -> new UsernameNotFoundException(username + "가 데이터베이스에 존재하지 않습니다 "));
  }

  private org.springframework.security.core.userdetails.User createUser(User user,
      String username) {
    if (!user.isActivated()) {
      throw new RuntimeException(username + " 가 활성화되지 않았습니다");
    }

    List<GrantedAuthority> grantedAuthorities = user.getAuthorities()
        .stream()
        .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
        .collect(Collectors.toList());

    return new org.springframework.security.core.userdetails.User(user.getUsername(),
        user.getPassword(), grantedAuthorities);
  }
}
