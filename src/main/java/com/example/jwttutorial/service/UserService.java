package com.example.jwttutorial.service;

import com.example.jwttutorial.dto.UserDto;
import com.example.jwttutorial.entity.Authority;
import com.example.jwttutorial.entity.User;
import com.example.jwttutorial.exception.DuplicateMemberException;
import com.example.jwttutorial.repository.UserRepository;
import com.example.jwttutorial.util.SecurityUtil;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public UserDto signup(UserDto userDto) {
    if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
      throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
    }

    Authority authority = Authority.builder()
        .authorityName("ROLE_USER")
        .build();

    User user = User.builder()
        .username(userDto.getUsername())
        .password(passwordEncoder.encode(userDto.getPassword()))
        .nickname(userDto.getNickname())
        .authorities(Collections.singleton(authority))
        .activated(true)
        .build();

    return UserDto.from(userRepository.save(user));
  }

  @Transactional(readOnly = true)
  public UserDto getUserWithAuthorities(String username) {
    return UserDto.from(userRepository.findOneWithAuthoritiesByUsername(username).orElse(null));
  }

  @Transactional(readOnly = true)
  public UserDto getMyUserWithAuthorities() {
    return UserDto.from(SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername).orElse(null));
  }

}
