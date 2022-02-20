package com.prateekb.blogsecure.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.prateekb.blogsecure.model.MailingList;



public interface MailingRepository extends JpaRepository<MailingList,Integer> {

}
