package com.contactmanager.smartcontactmanager.Helper;

import com.contactmanager.smartcontactmanager.Entities.User;
import com.contactmanager.smartcontactmanager.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailServiceImpl  implements UserDetailsService{


   @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       
      User user =  userRepository.getUserByUserName(username);
      if(user == null){
          throw new UsernameNotFoundException("Could Not Found User!!");
      }
      CustomUserDetails customUserDetails = new CustomUserDetails(user);
        return customUserDetails;
    }
    
}
