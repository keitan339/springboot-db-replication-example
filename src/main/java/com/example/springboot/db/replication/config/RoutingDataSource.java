package com.example.springboot.db.replication.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * トランザクションの読み取り専用フラグに基づいてPrimary/ReadReplicaデータソースを動的に切り替えるクラス
 */
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
