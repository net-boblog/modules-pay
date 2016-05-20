package com.xabaohui.modules.pay.bean;

import java.util.Date;


/**
 * Account entity. @author MyEclipse Persistence Tools
 */

public class Account  implements java.io.Serializable {


    // Fields    

     private Integer accountId;
     private Integer userId;
     private Double balance;
     private Double frozenMoney;
     private String type;
     private String status;
     private Date gmtCreate;
     private Date gmtModify;
     private Integer version;


    // Constructors

    /** default constructor */
    public Account() {
    }

    
    /** full constructor */
    public Account(Integer userId, Double balance, Double frozenMoney, String type, String status, Date gmtCreate, Date gmtModify, Integer version) {
        this.userId = userId;
        this.balance = balance;
        this.frozenMoney = frozenMoney;
        this.type = type;
        this.status = status;
        this.gmtCreate = gmtCreate;
        this.gmtModify = gmtModify;
        this.version = version;
    }

   
    // Property accessors

    public Integer getAccountId() {
        return this.accountId;
    }
    
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getUserId() {
        return this.userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getBalance() {
        return this.balance;
    }
    
    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getFrozenMoney() {
        return this.frozenMoney;
    }
    
    public void setFrozenMoney(Double frozenMoney) {
        this.frozenMoney = frozenMoney;
    }

    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return this.gmtCreate;
    }
    
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return this.gmtModify;
    }
    
    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    public Integer getVersion() {
        return this.version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
   








}