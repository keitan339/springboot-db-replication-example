package com.example.springboot.db.replication.config;

/**
 * データソースタイプ列挙型。
 *
 * <p>
 * データベース接続の種類を表現する列挙型です。Primary（書き込み用）とReadReplica（読み取り専用）のデータソースタイプを定義します。
 * </p>
 */
public enum DataSourceType {
  PRIMARY, READ_REPLICA
}
