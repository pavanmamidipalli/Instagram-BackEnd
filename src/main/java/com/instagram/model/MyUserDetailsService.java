//package com.instagram.model;
//
//import com.instagram.entity.User;
//
//import com.instagram.entity.UserDetailsImplementation;
//import com.instagram.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.util.ObjectUtils;
//
//import java.util.ArrayList;
//import java.util.Collection;
//
//@Service
//public class MyUserDetailsService implements UserDetailsService {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//     User user=userRepository.findByUserNameAndIsDeleted(username,Boolean.FALSE);
//        if(ObjectUtils.isEmpty(user)){
//        throw new UsernameNotFoundException("User not found");
//        }
////        Collection<GrantedAuthority> authorities=new ArrayList<GrantedAuthority>();
////        authorities.add(new SimpleGrantedAuthority("USER"));
//        return new UserDetailsImplementation(user);
//    }
//}
