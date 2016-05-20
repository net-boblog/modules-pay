package com.xabaohui.modules.pay.bean;

import java.util.Date;


/**
 * FreezeLog entity. @author MyEclipse Persistence Tools
 */

public class FreezeLog  implements java.io.Serializable {


    // Fields    

     private Integer freezeLogId;
     private Integer accountId;
     private Double frozenMoney;
     private Integer bizRef;
     private String operateType;
     private Date gmtCreate;
     private Date gmtModify;
     private Integer version;


    // Constructors

    /** default constructor */
    public FreezeLog() {
    }

    
    /** full constructor */
    public FreezeLog(Integer accountId, Double frozenMoney, Integer bizRef, String operateType, Date gmtCreate, Date gmtModify, Integer version) {
        this.accountId = accountId;
        this.frozenMoney = frozenMoney;
        this.bizRef = bizRef;
        this.operateType = operateType;
        this.gmtCreate = gmtCreate;
        this.gmtModify = gmtModify;
        this.version = version;
    }

   
    // Property accessors

    public Integer getFreezeLogId() {
        return this.freezeLogId;
    }
    
    public void setFreezeLogId(Integer freezeLogId) {
        this.freezeLogId = freezeLogId;
    }

    public Integer getAccountId() {
        return this.accountId;
    }
    
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Double getFrozenMoney() {
        return this.frozenMoney;
    }
    
    public void setFrozenMoney(Double frozenMoney) {
        this.frozenMoney = frozenMoney;
    }

    public Integer getBizRef() {
        return this.bizRef;
    }
    
    public void setBizRef(Integer bizRef) {
        this.bizRef = bizRef;
    }

    public String getOperateType() {
        return this.operateType;
    }
    
    public void setOperateType(String operateType) {
        this.operateType = operateType;
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