package com.example.dbaccess.token;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends CrudRepository<NotificationBean, Long> {

}
