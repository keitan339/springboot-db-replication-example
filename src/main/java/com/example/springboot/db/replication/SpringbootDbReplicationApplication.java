package com.example.springboot.db.replication;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot アプリケーションのメインクラス
 *
 * <p>
 * Primary/Replica構成のデータベース接続をサポートするサンプルアプリケーション
 * </p>
 */
@SpringBootApplication
@MapperScan("com.example.springboot.db.replication.mapper")
public class SpringbootDbReplicationApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringbootDbReplicationApplication.class, args);
  }
}
