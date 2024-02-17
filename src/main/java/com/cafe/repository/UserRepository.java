package com.cafe.repository;

import com.cafe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Objects;

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByEmail(String email);
    User findByUserId(int userId);



   // @Query(value = "CALL GET_UserDetails(:page_id);", nativeQuery = true)
   // void deletePageCatalogById(@Param("page_id") Long page_id);
   @Query(value = "CALL GET_UserDetails();", nativeQuery = true)
   List<Object[]> callGetUserDetailsProcedure();

    @Query(value="SELECT u.user_id, u.name, u.contact_number,u.email,u.status FROM user AS u JOIN user_roles AS ur ON u.user_id=ur.user_id WHERE ur.role='USER'",nativeQuery = true)
    List<Object[]> getAllUser();

    @Query(value="SELECT u.email FROM user AS u JOIN user_roles AS ur ON u.user_id=ur.user_id WHERE ur.role='ADMIN'",nativeQuery = true)
    List<String> getAllAdmin();
}
