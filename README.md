# DBをPrimary/ReadReplica構成にした場合のデータソース使い分け

性能改善のため、更新系の処理はPrimaryインスタンスを参照し、参照系の処理はReadReplicaインスタンスを参照する方法。

## データソースの設定

primaryおよびread-replicaの接続先情報をapplication.ymlに設定する。

```yml
spring:
  # Primary DB 設定
  datasource:
    primary:
      jdbc-url: jdbc:postgresql://db_primary:5432/postgres
      username: postgres
      password: password
      driver-class-name: org.postgresql.Driver
      hikari:
        pool-name: PrimaryPool
        maximum-pool-size: 10
        minimum-idle: 5
        connection-timeout: 30000
        idle-timeout: 600000
        max-lifetime: 1800000

    # Read Replica DB 設定
    read-replica:
      jdbc-url: jdbc:postgresql://db_read_replica:5432/postgres
      username: postgres
      password: password
      driver-class-name: org.postgresql.Driver
      hikari:
        pool-name: ReadReplicaPool
        maximum-pool-size: 10
        minimum-idle: 5
        connection-timeout: 30000
        idle-timeout: 600000
        max-lifetime: 1800000
```

## 該当トランザクションが更新処理か参照処理かの宣言

参照系処理の場合にはトランザクションのアノテーション(@Transaction)にreadOnlyを設定する。

- 参照系

  ```java
  @Transactional(readOnly = true)
  public class ReadOnlyService {
    ～～～
  }
  ```

- 更新系

  ```java
  @Transactional
  public class UpdateService {
    ～～～
  }
  ```

## トランザクションの種別に応じたDataSourceの設定

トランザクションのタイプ（ReadOnlyか否か）に応じて、PrimaryDataSoruceかReadReplicaDataSoruceかを設定するRoutingDataSourceを作成し、SqlSesstionFactoryやTransactionManagerに設定するDataSoruceを可変にする。

### DataSourceConfig.java

```java
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
  DataSource routingDataSource(
      @Qualifier("primaryDataSource") DataSource primaryDataSource,
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
```

### DataSourceType.java

```java
public enum DataSourceType {
  PRIMARY,
  READ_REPLICA
}
```

### RoutingDataSource.java

```java
public class RoutingDataSource extends AbstractRoutingDataSource {

  @Override
  protected DataSourceType determineCurrentLookupKey() {
    // Transactionモードを判定
    boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

    // トランザクションがアクティブで読み取り専用の場合はReadReplicaを使用
    // それ以外はPrimaryを使用
    DataSourceType dataSourceType =
        isReadOnly ? DataSourceType.READ_REPLICA : DataSourceType.PRIMARY;

    return dataSourceType;
  }
}
```

## データベースの初期化

application.ymlのspring.sql.initによるデータベースの初期化は、データソースが複数ある場合に動かないため、PrimaryDataSourceを用いて初期化を実行する設定を追加。

```java
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
```



## サンプルアプリケーション

### アプリケーションの起動

```sh
mvn spring-boot:run
```

### 動作確認

[http://localhost:8080](http://localhost:8080)にブラウザでアクセス。
