package com.example.springboot.db.replication.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

/**
 * メッセージ情報エンティティクラス。
 *
 * <p>
 * データベースのメッセージテーブルに対応するエンティティです。メッセージの番号とメッセージ内容を保持します。
 * </p>
 */
@Data
@ToString
public class MessageJoho implements Serializable {

  private Integer no;

  private String message;

  private static final long serialVersionUID = 1L;
}
