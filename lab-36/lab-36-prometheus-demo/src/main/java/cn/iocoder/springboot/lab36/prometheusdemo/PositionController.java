package cn.iocoder.springboot.lab36.prometheusdemo;

import cn.iocoder.springboot.lab36.prometheusdemo.po.PositionDO;
import cn.iocoder.springboot.lab36.prometheusdemo.po.PositionDOExample;
import cn.iocoder.springboot.lab36.prometheusdemo.po.PositionDOExample.Criteria;
import cn.iocoder.springboot.lab36.prometheusdemo.repository.PositionMapper;
import cn.iocoder.springboot.lab36.prometheusdemo.service.PositionService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PositionController {

	@Autowired
	PositionService positionService;


	@GetMapping(value = "/getPosition")
	public List<PositionDO> getPosition(@RequestParam(value = "symbol",required = false) String symbol, @RequestParam(value = "userId",required = false) String userId) {
		return positionService.getPosition(symbol,userId);
	}


	
}
