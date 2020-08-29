package redisson.bean.po;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
	
	private String id;

    private String orderNo;

    private String entrustId;

    private String entrustNo;

    private String userId;

    private String memberId;

    private String otherUserId;

    private String otherMemberId;

    private Integer cycle;

    private String pawnCoinId;

    private String pawnCoinEunit;

    private BigDecimal pawnQuantity;

    private BigDecimal pawnRate;

    private String legalCoinId;

    private String legalCoinEunit;

    private BigDecimal legalQuantity;

    private BigDecimal legalDailyRate;

    private BigDecimal warnPrice;

    private BigDecimal closePrice;

    private BigDecimal fee;

    private Integer alreadyInterestCount;

    private BigDecimal dailyInterest;

    private BigDecimal alreadyInterest;

    private BigDecimal totalInterest;

    private Integer type;

    private Integer direction;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getEntrustNo() {
        return entrustNo;
    }

    public void setEntrustNo(String entrustNo) {
        this.entrustNo = entrustNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getOtherMemberId() {
        return otherMemberId;
    }

    public void setOtherMemberId(String otherMemberId) {
        this.otherMemberId = otherMemberId;
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
        this.pawnCoinId = pawnCoinId;
    }

    public String getPawnCoinEunit() {
        return pawnCoinEunit;
    }

    public void setPawnCoinEunit(String pawnCoinEunit) {
        this.pawnCoinEunit = pawnCoinEunit;
    }

    public BigDecimal getPawnQuantity() {
        return pawnQuantity;
    }

    public void setPawnQuantity(BigDecimal pawnQuantity) {
        this.pawnQuantity = pawnQuantity;
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
        this.legalCoinId = legalCoinId;
    }

    public String getLegalCoinEunit() {
        return legalCoinEunit;
    }

    public void setLegalCoinEunit(String legalCoinEunit) {
        this.legalCoinEunit = legalCoinEunit;
    }

    public BigDecimal getLegalQuantity() {
        return legalQuantity;
    }

    public void setLegalQuantity(BigDecimal legalQuantity) {
        this.legalQuantity = legalQuantity;
    }

    public BigDecimal getLegalDailyRate() {
        return legalDailyRate;
    }

    public void setLegalDailyRate(BigDecimal legalDailyRate) {
        this.legalDailyRate = legalDailyRate;
    }

    public BigDecimal getWarnPrice() {
        return warnPrice;
    }

    public void setWarnPrice(BigDecimal warnPrice) {
        this.warnPrice = warnPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Integer getAlreadyInterestCount() {
        return alreadyInterestCount;
    }

    public void setAlreadyInterestCount(Integer alreadyInterestCount) {
        this.alreadyInterestCount = alreadyInterestCount;
    }

    public BigDecimal getDailyInterest() {
        return dailyInterest;
    }

    public void setDailyInterest(BigDecimal dailyInterest) {
        this.dailyInterest = dailyInterest;
    }

    public BigDecimal getAlreadyInterest() {
        return alreadyInterest;
    }

    public void setAlreadyInterest(BigDecimal alreadyInterest) {
        this.alreadyInterest = alreadyInterest;
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

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
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