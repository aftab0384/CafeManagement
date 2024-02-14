package com.cafe.jwt;
import com.cafe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepo;
    private com.cafe.model.User userdetail;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        userdetail = userRepo.findByEmail(username);

        System.out.println(userdetail);
//        Collection<GrantedAuthority> authorities = userdetail.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//                .collect(Collectors.toList());


        if(!Objects.isNull(userdetail)){
            List<String> roles = new ArrayList<>(userdetail.getRoles());
            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
            System.out.println("user details****************");
            System.out.println(authorities);
            return new CustomUserDetails(
                    userdetail.getEmail(),
                    userdetail.getPassword(),
                    authorities,
                    userdetail.getStatus()
            );
        }else{
            throw new UsernameNotFoundException("user not exist with name: " + username);
        }

    }
    public com.cafe.model.User userdetail(){
        return userdetail;
    }
}
