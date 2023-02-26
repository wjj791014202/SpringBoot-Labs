package cn.iocoder.springboot.lab37.loggingdemo.controller;

import org.apache.logging.log4j.spi.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.logging.LogLevel;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    LoggersEndpoint loggersEndpoint;

    @GetMapping("/debug")
    public void debug() {
        logger.debug("debug");
    }

    @GetMapping("/info")
    public void info() {
        logger.info("info");
    }

    @GetMapping("/error")
    public void error() {
        logger.error("error");
    }

    @GetMapping("/changeLevel")
    public void changeLevel(@RequestParam("name") String name, @RequestParam("level")  LogLevel configuredLevel){
        loggersEndpoint.configureLogLevel(name,configuredLevel);
    }

}
