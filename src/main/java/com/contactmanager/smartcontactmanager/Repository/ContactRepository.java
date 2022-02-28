package com.contactmanager.smartcontactmanager.Repository;

import java.util.List;

import com.contactmanager.smartcontactmanager.Entities.Contact;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<Contact,Integer>{
    
    @Query("select c from Contact c where c.user.id =:userId ")
    public Page<Contact> findContactByUser(@Param("userId")int userId,Pageable pageable);
}
