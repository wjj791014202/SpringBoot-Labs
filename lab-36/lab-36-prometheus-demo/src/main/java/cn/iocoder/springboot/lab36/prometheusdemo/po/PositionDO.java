package cn.iocoder.springboot.lab36.prometheusdemo.po;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;

@Data
public class PositionDO {

    /**
     * 仓位系统唯一编号，一个用户只有一个仓位
     */
    private String id;

    /**
     * 合约编号
     */
    private String symbol;

    /**
     * 操作用户编号
     */
    private String userId;

    /**
     * 风险限额
     */
    private Integer riskLimit;

    /**
     * 自动追加保证金，0为不自动追加，1为自动追加
     */
    private Boolean autoDeposit;

    /**
     * 是否全仓，0:逐仓，1:全仓
     */
    private Integer marginType;

    /**
     * 是否全仓，0:未开仓，1:已开仓
     */
    private Boolean isOpen;

    /**
     * 平均开仓价格，USD计价
     */
    private BigDecimal avgEntryPrice;

    /**
     * 用户当前仓位
     */
    private Long currentQty;

    /**
     * 总的仓位价值，XBT计价，做多为负数
     */
    private BigDecimal currentCost;

    /**
     * 总的手续费，XBT计价，收取为正
     */
    private BigDecimal currentComm;

    /**
     * 未实现价值，加仓和平仓计算不一致
     */
    private BigDecimal unrealisedCost;

    /**
     * 暂时和unrealisedCost相等
     */
    private BigDecimal posCost;

    /**
     * 保证金的增加与减少，(不包括手续费和杠杆冻结)
     */
    private BigDecimal posCrossMargin;

    /**
     * 杠杆保证金部分，每一笔成交转移，由Trade计算，当前size/成交price/当前leverage=价值/lev，转移之后不会变化
     */
    private BigDecimal posInit;

    /**
     * 除资金费用外的所有手续费
     */
    private BigDecimal posCommCommon;

    /**
     * 特指资金费用增加时，新增的手续费
     */
    private BigDecimal posCommFunding;

    /**
     * 特指资金费用减少的资金，保证金减少pos_cross
     */
    private BigDecimal posLoss;

    /**
     * 特指资金费用增加的资金，保证金减少pos_cross
     */
    private BigDecimal posProfit;

    /**
     * 维持保证金(包含费用)，posComm+abs(posCost)*min(1/Lev, maintMarginReq+max(0, Fr*G(Qty)) G函数，如果参数>0，返回1，如果<0，返回-1，否则0
     */
    private BigDecimal posMaint;

    /**
     * 强平价格，USD计价
     */
    private BigDecimal liquidationPrice;

    /**
     * 破产价格，USD计价
     */
    private BigDecimal bankruptPrice;

    /**
     * 开仓时间
     */
    private Long openingTimestamp;

    /**
     * 快照id，单调递增，当前仓位变化所对应的id，用于消息接受方恢复仓位
     */
    private Long snapshotId;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;


}