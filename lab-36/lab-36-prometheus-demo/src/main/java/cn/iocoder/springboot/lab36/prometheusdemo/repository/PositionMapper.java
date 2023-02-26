package cn.iocoder.springboot.lab36.prometheusdemo.repository;

import cn.iocoder.springboot.lab36.prometheusdemo.po.PositionDO;
import cn.iocoder.springboot.lab36.prometheusdemo.po.PositionDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PositionMapper {

    /**
     * insert one record into table, ignore nullable value,
     */
    int insertSelective(PositionDO record);

    /**
     * update one record by primary key, ignore nullable value
     */
    int updateByPrimaryKeySelective(PositionDO record);

    /**
     * update record by example, ignore nullable value
     */
    int updateByExampleSelective(@Param("record") PositionDO record, @Param("example") PositionDOExample example);

    /**
     * delete one record by primary key
     */
    int deleteByPrimaryKey(String id);

    /**
     * delete record by example
     */
    int deleteByExample(PositionDOExample example);

    /**
     * get one record by primary key
     */
    PositionDO selectByPrimaryKey(String id);

    /**
     * get record by example
     */
    List<PositionDO> selectByExample(PositionDOExample example);

    /**
     * get one record by example
     */
    PositionDO selectOneByExample(PositionDOExample example);

    /**
     * select count(*) from table by example
     */
    long countByExample(PositionDOExample example);

    List<PositionDO> selectPositionsBy(@Param("symbol") String symbol,
            @Param("userId") String userId,
            @Param("positionId") String positionId,
            @Param("side") String side,
            @Param("currentQty") Long currentQty,
            @Param("pageSize") Integer pageSize);
}