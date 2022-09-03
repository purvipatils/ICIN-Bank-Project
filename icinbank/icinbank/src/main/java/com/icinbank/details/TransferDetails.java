package com.icinbank.details;

public class TransferDetails {

	private long savingAccount;
	private long primaryAccount;
	private int amount;
	private String Username;
	private String ifsc;

	public long getSavingAccount() {
		return savingAccount;
	}

	public void setSavingAccount(long savingAccount) {
		this.savingAccount = savingAccount;
	}

	public long getPrimaryAccount() {
		return primaryAccount;
	}

	public void setPrimaryAccount(long primaryAccount) {
		this.primaryAccount = primaryAccount;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

}
