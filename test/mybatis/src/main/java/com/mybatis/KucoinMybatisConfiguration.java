package com.mybatis;

import com.kucoin.starter.mybatis.plugin.DbInsertIntecerptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KucoinMybatisConfiguration {
    public KucoinMybatisConfiguration() {
    }

    @Bean
    public DbInsertIntecerptor dbInsertIntecerptor() {
        return new DbInsertIntecerptor();
    }
}
