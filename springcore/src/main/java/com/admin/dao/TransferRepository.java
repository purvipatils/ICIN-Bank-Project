package com.admin.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.admin.model.Transfer;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Integer> {
	public List<Transfer>findBySaccount(long saccount);
	public List<Transfer>findByRaccount(long raccount);

}
