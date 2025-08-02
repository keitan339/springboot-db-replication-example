package com.example.springboot.db.replication.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

/**
 * データソース設定クラス。
 *
 * <p>
 * Primary/Replicaデータベース構成をサポートするためのデータソース設定を提供します。
 * プライマリデータソースと読み取り専用レプリカデータソースを設定し、MyBatisのSqlSessionFactoryやトランザクションマネージャーを構成します。
 * </p>
 */
@Configuration
public class DataSourceConfig {

  @Bean(name = "primaryDataSource")
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource.primary")
  DataSource primaryDataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean(name = "readReplicaDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.read-replica")
  DataSource readReplicaDataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean(name = "routingDataSource")
  @Primary
  DataSource routingDataSource(@Qualifier("primaryDataSource") DataSource primaryDataSource,
      @Qualifier("readReplicaDataSource") DataSource readReplicaDataSource) {
    RoutingDataSource routingDataSource = new RoutingDataSource();

    Map<Object, Object> dataSourceMap = new HashMap<>();
    dataSourceMap.put(DataSourceType.PRIMARY, primaryDataSource);
    dataSourceMap.put(DataSourceType.READ_REPLICA, readReplicaDataSource);

    routingDataSource.setTargetDataSources(dataSourceMap);
    routingDataSource.setDefaultTargetDataSource(primaryDataSource);

    return routingDataSource;
  }

  @Bean(name = "mainDataSource")
  DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
    return new LazyConnectionDataSourceProxy(routingDataSource);
  }

  @Bean(name = "sqlSessionFactory")
  @Primary
  SqlSessionFactory sqlSessionFactory(@Qualifier("mainDataSource") DataSource mainDataSource)
      throws Exception {
    SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(mainDataSource);
    sessionFactory.setMapperLocations(
        new PathMatchingResourcePatternResolver().getResources("classpath:mapper/**/*.xml"));
    sessionFactory.setTypeAliasesPackage("com.example.springboot.db.replication.entity");
    return sessionFactory.getObject();
  }

  @Bean(name = "sqlSessionTemplate")
  @Primary
  SqlSessionTemplate sqlSessionTemplate(
      @Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }

  @Bean(name = "transactionManager")
  @Primary
  DataSourceTransactionManager transactionManager(
      @Qualifier("mainDataSource") DataSource mainDataSource) {
    return new DataSourceTransactionManager(mainDataSource);
  }
}
