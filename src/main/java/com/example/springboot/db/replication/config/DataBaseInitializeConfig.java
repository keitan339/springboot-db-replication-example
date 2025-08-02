package com.example.springboot.db.replication.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * データベース初期化設定クラス。
 *
 * <p>
 * アプリケーション起動時にプライマリデータソースに対して、スキーマとデータの初期化処理を実行するための設定を提供します。
 * </p>
 */
@Configuration
public class DataBaseInitializeConfig {

  @Bean
  DatabaseInitializationSettings databaseInitializationSettings(
      SqlInitializationProperties properties) {
    DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
    settings.setSchemaLocations(properties.getSchemaLocations());
    settings.setDataLocations(properties.getDataLocations());
    settings.setMode(properties.getMode());
    return settings;
  }

  @Bean
  DataSourceScriptDatabaseInitializer primaryDataSourceScriptDatabaseInitializer(
      @Qualifier("primaryDataSource") DataSource dataSource,
      DatabaseInitializationSettings settings) {

    // spring.sql.init.* の設定を "primaryDataSource" に適用して初期化を実行する
    return new DataSourceScriptDatabaseInitializer(dataSource, settings);
  }
}
