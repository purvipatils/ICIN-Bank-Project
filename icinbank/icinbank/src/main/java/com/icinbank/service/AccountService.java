package com.icinbank.service;


import org.springframework.http.ResponseEntity;

import com.icinbank.model.Account;
import com.icinbank.response.DepositResponse;
import com.icinbank.response.TransferResponse;
import com.icinbank.response.WithdrawResponse;


public interface AccountService {

	public ResponseEntity<Account> newAccount(String username);
	public Account getAccount(String username);
	public DepositResponse deposit(long acc,int amount);
	public WithdrawResponse withdraw(long acc,int amount);
	public TransferResponse transfer(long saccount,long raccount,int amount);
	public Account getAccountDetails(long account);
	public Account updateAccount(Account account);
	
}