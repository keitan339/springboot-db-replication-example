package com.example.springboot.db.replication.service;

import com.example.springboot.db.replication.entity.MessageJoho;
import com.example.springboot.db.replication.mapper.MessageJohoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 更新処理を行うサービスクラス
 *
 * <p>
 * Primaryデータベースへの書き込み処理を行う
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateService {

  private final MessageJohoMapper messageJohoMapper;

  /**
   * message_johoテーブルに新規レコードを挿入する
   *
   * @param messageJoho 挿入するデータ
   * @return 挿入件数
   */
  public int insert(MessageJoho messageJoho) {
    return messageJohoMapper.insert(messageJoho);
  }

  /**
   * message_johoテーブルのレコードを更新する
   *
   * @param messageJoho 更新するデータ
   * @return 更新件数
   */
  public int update(MessageJoho messageJoho) {
    return messageJohoMapper.updateByPrimaryKey(messageJoho);
  }

  /**
   * message_johoテーブルからレコードを削除する
   *
   * @param colA 削除対象のID
   * @return 削除件数
   */
  public int delete(Integer colA) {
    return messageJohoMapper.deleteByPrimaryKey(colA);
  }
}
