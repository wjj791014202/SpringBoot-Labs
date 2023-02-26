package cn.iocoder.springboot.lab39.skywalkingdemo.providerdemo.service;

import cn.iocoder.springboot.lab39.skywalkingdemo.api.User;
import cn.iocoder.springboot.lab39.skywalkingdemo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

@org.apache.dubbo.config.annotation.Service(version = "1.0.0")
public class UserServiceImpl implements UserService {

    @Autowired
    private JdbcTemplate template;
    
    @Override
    public User get(Integer id) {
        return template.queryForObject("SELECT id, name FROM contact WHERE id = ?",
                new BeanPropertyRowMapper<>(User.class), // 结果转换成对应的对象。Object 理论来说是 UserDO.class ，这里偷懒了。
                id);
    }

}
