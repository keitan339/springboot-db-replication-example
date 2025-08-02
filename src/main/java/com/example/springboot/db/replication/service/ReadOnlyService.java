package com.example.springboot.db.replication.service;

import com.example.springboot.db.replication.entity.MessageJoho;
import com.example.springboot.db.replication.mapper.MessageJohoMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 参照処理のサービスクラス
 *
 * <p>
 * Replicaデータベースからの読み取り処理を行う
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, label = "readonlyservice")
public class ReadOnlyService {

  private final MessageJohoMapper messageJohoMapper;

  /**
   * message_johoテーブルから主キーでレコードを検索する
   *
   * @param no 検索対象のID
   * @return 検索結果（見つからない場合はnull）
   */
  public MessageJoho findById(Integer no) {
    return messageJohoMapper.selectByPrimaryKey(no);
  }

  /**
   * message_johoテーブルから全レコードを検索する
   *
   * @return 全レコードのリスト
   */
  public List<MessageJoho> findAll() {
    return messageJohoMapper.selectAll();
  }

  /**
   * message_johoテーブルのレコード数をカウントする
   *
   * @return レコード数
   */
  public long count() {
    return messageJohoMapper.count();
  }
}
