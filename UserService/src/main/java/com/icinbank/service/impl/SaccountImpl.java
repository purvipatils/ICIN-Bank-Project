package com.icinbank.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.icinbank.dao.AccountRepository;
import com.icinbank.dao.SaccountRepository;
import com.icinbank.dao.UserRepository;
import com.icinbank.model.Account;
import com.icinbank.model.Saccount;
import com.icinbank.model.User;
import com.icinbank.response.DepositResponse;
import com.icinbank.response.TransferResponse;
import com.icinbank.response.WithdrawResponse;
import com.icinbank.service.SaccountService;
import com.icinbank.service.TransferHistoryService;
import com.icinbank.service.UserHistoryService;

@Service
public class SaccountImpl implements SaccountService {

	@Autowired
	private SaccountRepository savingAccRepo;

	@Autowired
	private UserHistoryService userHistory;

	@Autowired
	private TransferHistoryService transferHistory;

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private UserRepository userRepo;

	private final String bankCode = "3914";
	private final String countryCode = "91";
	private final String branchCode = "820";
	private final String accountcode = "2";

	@Override
	public Saccount getAccount(String username) {
		return savingAccRepo.findByUsername(username);
	}

	@Override
	public Saccount getAccountDetails(long account) {
		// TODO Auto-generated method stub
		return savingAccRepo.findByAccno(account);
	}

	public long generate_saving(int userId) {
		String accNo = bankCode + countryCode + branchCode + accountcode + String.valueOf(userId);
		return Long.parseLong(accNo);
	}

	@Override
	public ResponseEntity<Saccount> newSavingAccount(String username) {

		final long accNo;
		final Saccount checkAccount;
		User getUser;
		Saccount newAccount = new Saccount();

		try {
			getUser = userRepo.findByUsername(username);
			accNo = generate_saving(getUser.getId());
			checkAccount = this.getAccountDetails(accNo);

			if (checkAccount == null) {
				newAccount.setUsername(username);
				newAccount.setUser(getUser);
				newAccount.setAccno(accNo);
				savingAccRepo.save(newAccount);
				return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
			} else {

				if (checkAccount.getAccno() == accNo) {
					return new ResponseEntity<>(checkAccount, HttpStatus.ALREADY_REPORTED);
				}

				return new ResponseEntity<>(checkAccount, HttpStatus.ALREADY_REPORTED);
			}

		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Override
	public DepositResponse deposit(long acc, int amount) {
		DepositResponse response = new DepositResponse();
		boolean flag = true;
		try {
			Saccount account = savingAccRepo.findByAccno(acc);
			account.setBalance(account.getBalance() + amount);
			userHistory.addAction(acc, amount, account.getBalance(), "deposit");
			savingAccRepo.save(account);
			response.setResponseMessage("Rs." + amount + " successfully deposited into your account balance is now Rs."
					+ account.getBalance());
			response.setDepositStatus(flag);
		} catch (Exception e) {
			flag = false;
			response.setResponseMessage("Account number is incorrect");
			response.setDepositStatus(flag);
		}
		response.setAccount(acc);
		return response;
	}

	@Override
	public WithdrawResponse withdraw(long acc, int amount) {
		WithdrawResponse response = new WithdrawResponse();
		boolean flag = true;

		try {
			Saccount account = savingAccRepo.findByAccno(acc);
			User user = userRepo.findByUsername(account.getUsername());
			if (user.getFeatureStatus() == 2 || user.getFeatureStatus() == 3) {

				if (account.getBalance() >= amount) {
					account.setBalance(account.getBalance() - amount);
					userHistory.addAction(acc, amount, account.getBalance(), "withdraw");
					savingAccRepo.save(account);
					response.setResponseMessage("Rs." + amount
							+ " successfully withdrawn your account balance is now Rs." + account.getBalance());
					response.setWithdrawStatus(flag);
				} else {
					flag = false;
					response.setResponseMessage("Insufficient funds to complete the transaction");
					response.setWithdrawStatus(flag);
				}
			} else {
				flag = false;
				response.setResponseMessage("This function is not available for your account");
				response.setWithdrawStatus(flag);
			}
		} catch (Exception e) {
			flag = false;
			response.setResponseMessage("Account number is incorrect");
			response.setWithdrawStatus(flag);
		}
		response.setAccount(acc);
		return response;
	}

	@Override
	public TransferResponse transfer(long saccount, long raccount, int amount) {
		TransferResponse response = new TransferResponse();
		boolean flag = true;

		try {
			Saccount senderAccount = savingAccRepo.findByAccno(saccount);
			if (isprimary(raccount)) {
				Account receiverAccount = accountRepo.findByAccno(raccount);
				if (senderAccount.getAccno() != receiverAccount.getAccno()) {
					if (senderAccount.getBalance() >= amount) {
						User user = userRepo.findByUsername(senderAccount.getUsername());

						if (user.getFeatureStatus() == 3) {
							senderAccount.setBalance(senderAccount.getBalance() - amount);
							receiverAccount.setBalance(receiverAccount.getBalance() + amount);
							transferHistory.addAction(saccount, raccount, amount);
							savingAccRepo.save(senderAccount);
							accountRepo.save(receiverAccount);
							response.setResponseMessage("Rs." + amount + " successfully transferred to account "
									+ receiverAccount.getAccno());
							response.setTransferStatus(flag);
						} else {
							flag = false;
							response.setResponseMessage("This feature is not available for your account");
							response.setTransferStatus(flag);
						}
					} else {
						flag = false;
						response.setResponseMessage("Insufficient funds to complete the transfer");
						response.setTransferStatus(flag);
					}
				} else {
					flag = false;
					response.setResponseMessage("sender and recieiver accounts are same");
					response.setTransferStatus(flag);
				}
			} else {
				Saccount receiverAccount = savingAccRepo.findByAccno(raccount);
				if (senderAccount.getAccno() != receiverAccount.getAccno()) {

					if (senderAccount.getBalance() > amount) {
						User user = userRepo.findByUsername(senderAccount.getUsername());

						if (user.getFeatureStatus() == 3) {
							senderAccount.setBalance(senderAccount.getBalance() - amount);
							receiverAccount.setBalance(receiverAccount.getBalance() + amount);
							transferHistory.addAction(saccount, raccount, amount);
							savingAccRepo.save(senderAccount);
							savingAccRepo.save(receiverAccount);
							response.setResponseMessage("Rs." + amount + " successfully transferred to account "
									+ receiverAccount.getAccno());
							response.setTransferStatus(flag);
						} else {
							flag = false;
							response.setResponseMessage("This function isnt available for the account");
							response.setTransferStatus(flag);
						}
					}

					else {
						flag = false;
						response.setResponseMessage("Insufficient funds to complete the transfer");
						response.setTransferStatus(flag);
					}
				}

				else {
					flag = false;
					response.setResponseMessage("sender and recieiver accounts are same");
					response.setTransferStatus(flag);
				}
			}
		} catch (Exception e) {
			flag = false;
			response.setResponseMessage("Account number is incorrect");
			response.setTransferStatus(flag);
		}
		response.setSaccount(saccount);
		return response;
	}

	public static boolean isprimary(long account) {
		String s = Long.toString(account).substring(0, 10);
		String check = "3914918201";
		if (s.equals(check)) {
			return true;
		} else {
			return false;
		}

	}

}