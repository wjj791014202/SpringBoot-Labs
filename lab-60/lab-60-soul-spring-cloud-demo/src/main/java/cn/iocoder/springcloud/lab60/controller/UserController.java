package cn.iocoder.springcloud.lab60.controller;

import cn.iocoder.springcloud.lab60.dto.UserCreateDTO;
import org.dromara.soul.client.springcloud.annotation.SoulSpringCloudClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/get")
    @SoulSpringCloudClient(path = "/user/get", desc = "获得用户详细")
    public String getUser(@RequestParam("id") Integer id) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "DEMO:" + id;
    }

    @PostMapping("/create")
    @SoulSpringCloudClient(path = "/user/create", desc = "创建用户")
    public Integer createUser(@RequestBody UserCreateDTO createDTO) {
        logger.info("[createUser][username({}) password({})]", createDTO.getNickname(), createDTO.getGender());
        return 1;
    }

}
