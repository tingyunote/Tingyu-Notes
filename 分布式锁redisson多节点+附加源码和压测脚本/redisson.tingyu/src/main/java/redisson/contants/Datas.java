package redisson.contants;

public class Datas {

	public static final String OK = "OK";
	
	public static final String ENTRUST_TYPE_BUY = "1";
	
	public static final String ENTRUST_TYPE_SELL = "2";
	
	/**  委托类型-买-1 */
	public static final int ENTRUST_TYPE_BUY_INT = 1;

	/** 委托类型-卖-2 */
	public static final int ENTRUST_TYPE_SELL_INT = 2;
	
	public static final String ACCESS_TOKEN_EXPIRED = "Access token expired";
	
	/** 资产流水类型-加-1 */
	public static final int ASSET_TYPE_ADD = 1;

	/** 资产流水类型-减-2 */
	public static final int ASSET_TYPE_LESS = 2;
	
	/** 资产streamType-其他-10 */
	public static final int ASSET_STREAM_TYPE_OTHER = 10;
	
	/** 当前委托-1 */
	public static final int CURRENT_ENTRUST_TYPE = 1;
	/** 历史委托-2 */
	public static final int HISTORY_ENTRUST_TYPE = 2;
	
	/**
	 * 委托状态-已撤销-1
	 */
	public static final int ENTRUST_STATUS_CANCEL = 1;

	/**
	 * 委托状态-已成交-2
	 */
	public static final int ENTRUST_STATUS_TRADE = 2;
	
	public static final String UNFREEZE_TEXT = "解冻";
	
	public static final String FREEZE_TEXT = "冻结";

	public static final String EUNIT_CNT = "CNT";

	/**
	 * 提前还款费率
	 */
	public static final double ADVANCE_REPAY_DEFAULT_RATE = 0.1;
	
	public static final double ADVANCE_REPAY_RETURN_RATE = 0.9;
	
}
