package cn.iocoder.springboot.lab36.prometheusdemo.service;

import cn.iocoder.springboot.lab36.prometheusdemo.po.PositionDO;
import cn.iocoder.springboot.lab36.prometheusdemo.po.PositionDOExample;
import cn.iocoder.springboot.lab36.prometheusdemo.po.PositionDOExample.Criteria;
import cn.iocoder.springboot.lab36.prometheusdemo.repository.PositionMapper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PositionService {


    @Resource
    PositionMapper positionMapper;

    public List<PositionDO> getPosition(String symbol, String userId) {
        long startTime = System.currentTimeMillis();
        PositionDOExample example = new PositionDOExample();
        Criteria criteria = example.createCriteria();
        criteria.andCurrentQtyNotEqualTo(0L);
        if (!StringUtils.isEmpty(symbol)) {
            criteria.andSymbolEqualTo(symbol);
        }
        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(userId);
        }
        List<PositionDO> list = positionMapper.selectByExample(example);
        System.out.println("getPosition:cost=" + (System.currentTimeMillis() - startTime));
        return list;
    }

}
