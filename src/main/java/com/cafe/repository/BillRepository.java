package com.cafe.repository;

import com.cafe.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill,Integer> {
    @Query(value = "select * from bill order by bill_id desc;", nativeQuery = true)
    List<Bill> getAllBill();

    @Query(value = "SELECT * FROM bill AS b WHERE b.created_by=?1 ORDER BY bill_id DESC;", nativeQuery = true)
    List<Bill> getBillByUserId(int createdBy);
}
