package redisson.bean.po;

import java.math.BigDecimal;
import java.util.Date;

public class Entrust {
	
    private String id;

    private String entrustNo;

    private String userId;

    private String memberId;

    private Integer cycle;

    private String pawnCoinId;

    private String pawnCoinEunit;

    private BigDecimal pawnTotalQuantity;

    private BigDecimal pawnTradeQuantity;

    private BigDecimal pawnRate;

    private String legalCoinId;

    private String legalCoinEunit;

    private BigDecimal legalTotalQuantity;

    private BigDecimal legalTradeQuantity;

    private BigDecimal legalDailyRate;

    private BigDecimal totalInterest;

    private Integer type;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getEntrustNo() {
        return entrustNo;
    }

    public void setEntrustNo(String entrustNo) {
        this.entrustNo = entrustNo == null ? null : entrustNo.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId == null ? null : memberId.trim();
    }

    public Integer getCycle() {
        return cycle;
    }

    public void setCycle(Integer cycle) {
        this.cycle = cycle;
    }

    public String getPawnCoinId() {
        return pawnCoinId;
    }

    public void setPawnCoinId(String pawnCoinId) {
        this.pawnCoinId = pawnCoinId == null ? null : pawnCoinId.trim();
    }

    public String getPawnCoinEunit() {
        return pawnCoinEunit;
    }

    public void setPawnCoinEunit(String pawnCoinEunit) {
        this.pawnCoinEunit = pawnCoinEunit == null ? null : pawnCoinEunit.trim();
    }

    public BigDecimal getPawnTotalQuantity() {
        return pawnTotalQuantity;
    }

    public void setPawnTotalQuantity(BigDecimal pawnTotalQuantity) {
        this.pawnTotalQuantity = pawnTotalQuantity;
    }

    public BigDecimal getPawnTradeQuantity() {
        return pawnTradeQuantity;
    }

    public void setPawnTradeQuantity(BigDecimal pawnTradeQuantity) {
        this.pawnTradeQuantity = pawnTradeQuantity;
    }

    public BigDecimal getPawnRate() {
        return pawnRate;
    }

    public void setPawnRate(BigDecimal pawnRate) {
        this.pawnRate = pawnRate;
    }

    public String getLegalCoinId() {
        return legalCoinId;
    }

    public void setLegalCoinId(String legalCoinId) {
        this.legalCoinId = legalCoinId == null ? null : legalCoinId.trim();
    }

    public String getLegalCoinEunit() {
        return legalCoinEunit;
    }

    public void setLegalCoinEunit(String legalCoinEunit) {
        this.legalCoinEunit = legalCoinEunit == null ? null : legalCoinEunit.trim();
    }

    public BigDecimal getLegalTotalQuantity() {
        return legalTotalQuantity;
    }

    public void setLegalTotalQuantity(BigDecimal legalTotalQuantity) {
        this.legalTotalQuantity = legalTotalQuantity;
    }

    public BigDecimal getLegalTradeQuantity() {
        return legalTradeQuantity;
    }

    public void setLegalTradeQuantity(BigDecimal legalTradeQuantity) {
        this.legalTradeQuantity = legalTradeQuantity;
    }

    public BigDecimal getLegalDailyRate() {
        return legalDailyRate;
    }

    public void setLegalDailyRate(BigDecimal legalDailyRate) {
        this.legalDailyRate = legalDailyRate;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
}