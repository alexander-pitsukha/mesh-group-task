package com.meshgroup.task.security;

import com.meshgroup.task.repository.UserRepository;
import com.meshgroup.task.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserCache userCache;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        var userDetails = userCache.getUserFromCache(username);
        if (userDetails == null) {
            userDetails = Optional.ofNullable(username.matches(Constants.PHONE_REGEXP)
                            ? userRepository.findByPhone(username)
                            : userRepository.findByEmail(username)).map(UserDetailsImpl::new)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found."));
            userCache.putUserInCache(userDetails);
        }
        return userDetails;
    }

}
