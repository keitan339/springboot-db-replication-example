package com.example.springboot.db.replication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * メッセージモデルクラス。
 *
 * <p>
 * アプリケーション内でメッセージデータを表現するためのモデルです。メッセージのIDと内容を保持し、レイヤー間でのデータ転送に使用されます。
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
  private Integer id;
  private String content;
}
