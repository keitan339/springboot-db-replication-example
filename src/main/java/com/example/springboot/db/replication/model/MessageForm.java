package com.example.springboot.db.replication.model;

import lombok.Data;

/**
 * メッセージフォームモデルクラス。
 *
 * <p>
 * メッセージ入力フォームからの入力データを受け取るためのモデルです。Webフォームとコントローラー間でのデータバインディングに使用されます。
 * </p>
 */
@Data
public class MessageForm {
  private String content;
}
