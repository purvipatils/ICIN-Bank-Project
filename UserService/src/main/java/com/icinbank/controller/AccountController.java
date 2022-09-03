package com.icinbank.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.icinbank.dao.AccountRepository;
import com.icinbank.dao.SaccountRepository;
import com.icinbank.details.TransactionDetails;
import com.icinbank.details.TransferDetails;
import com.icinbank.model.Account;
import com.icinbank.model.Saccount;
import com.icinbank.model.Transfer;
import com.icinbank.model.UserHistory;
import com.icinbank.response.DepositResponse;
import com.icinbank.response.TransferResponse;
import com.icinbank.response.WithdrawResponse;
import com.icinbank.service.AccountService;
import com.icinbank.service.SaccountService;
import com.icinbank.service.TransferHistoryService;
import com.icinbank.service.UserHistoryService;

@RestController

@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private SaccountService savingAccService;

	@Autowired
	private UserHistoryService userHistoryService;

	@Autowired
	private TransferHistoryService transferHistoryService;

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private SaccountRepository savingAccountRepo;

	private final String ifsc = "ICIN7465";

	public static boolean isprimary(long account) {
		String s = Long.toString(account).substring(0, 10);
		String check = "3914918201";
		if (s.equals(check)) {
			return true;
		} else {
			return false;
		}
	}

	@GetMapping("/account/details/{account}")
	public Account getAccountDetails(@PathVariable("account") int account) {
		return accountService.getAccountDetails(account);
	}

	@PostMapping("/account/newAccount")
	public ResponseEntity<Account> newAccount(@RequestBody Account account) {
		return accountService.newAccount(account.getUsername());
	}

	@PostMapping("/saving/newAccount")
	public ResponseEntity<Saccount> newAccount(@RequestBody Saccount account) {
		return savingAccService.newSavingAccount(account.getUsername());
	}

	@PutMapping("/account/profile")
	public Account updateProfile(@RequestBody Account account) {
		return accountService.updateAccount(account);
	}

	@GetMapping("/account/getprimary/{username}")
	public Account getPrimarydetails(@PathVariable("username") String username) {
		return accountService.getAccount(username);
	}

	@GetMapping("/account/getsaving/{username}")
	public Saccount getSavingdetails(@PathVariable("username") String username) {
		return savingAccService.getAccount(username);
	}

	@PostMapping("/account/deposit")
	public DepositResponse deposit(@RequestBody TransactionDetails details) {
		// adao.findByUsername(adao.findByAccno(details.getAccount()).getUsername());
		if (isprimary(details.getAccount())) {
			return accountService.deposit(details.getAccount(), details.getAmount());
		} else {
			return savingAccService.deposit(details.getAccount(), details.getAmount());
		}
	}

	@PostMapping("/account/withdraw")
	public WithdrawResponse withdraw(@RequestBody TransactionDetails details) {
		if (isprimary(details.getAccount())) {
			return accountService.withdraw(details.getAccount(), details.getAmount());
		} else {
			return savingAccService.withdraw(details.getAccount(), details.getAmount());
		}
	}

	@PostMapping("/account/transfer")
	public TransferResponse transfer(@RequestBody TransferDetails details) {
		try {
			
			System.out.println("Transfer Starts");
			System.out.println(details.getIfsc());
			System.out.println(ifsc);
			System.out.println(details.getIfsc().equals(ifsc));			
			
			System.out.println("Transfer Ends");
			
			if (details.getIfsc().equals(ifsc)) {
				Account primary = accountRepo.findByUsername(details.getUsername());
				Saccount saving = savingAccountRepo.findByUsername(details.getUsername());
				
				System.out.println(primary.getAccno());
				System.out.println(details.getSavingAccount());
				System.out.println(saving.getAccno());

				if (primary.getAccno() == details.getSavingAccount() || saving.getAccno() == details.getSavingAccount()) {
					// String len = Integer.toString(details.getSaccount());
					if (isprimary(details.getSavingAccount())) {
						return accountService.transfer(details.getSavingAccount(), details.getPrimaryAccount(),details.getAmount());								
					} else {
						return savingAccService.transfer(details.getSavingAccount(), details.getPrimaryAccount(),details.getAmount());								
					}
				} else {
					TransferResponse response = new TransferResponse();
					response.setSaccount(details.getSavingAccount());
					response.setResponseMessage("Dear user You can only transfer funds from the accounts registered with you");							
					response.setTransferStatus(false);
					return response;
				}
			} else {
				TransferResponse response = new TransferResponse();
				response.setSaccount(details.getSavingAccount());
				response.setResponseMessage("IFSC code is incorrect");
				response.setTransferStatus(false);
				return response;
			}
		} catch (Exception e) {
			TransferResponse response = new TransferResponse();
			response.setSaccount(details.getSavingAccount());
			response.setResponseMessage("Please provide an IFSC code");
			response.setTransferStatus(false);
			return response;

		}
	}

	@GetMapping("/account/getHistory/{account}")
	public List<UserHistory> getHistory(@PathVariable("account") long account) {
		List<UserHistory> history = userHistoryService.getHistory(account);
		Collections.reverse(history);
		return history;
	}

	@GetMapping("/account/getTransfers/{account}")
	public List<Transfer> getTransfers(@PathVariable("account") long account) {
		return transferHistoryService.getTransfers(account);
	}

}