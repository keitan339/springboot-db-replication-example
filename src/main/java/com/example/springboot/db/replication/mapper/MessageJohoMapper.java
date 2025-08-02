package com.example.springboot.db.replication.mapper;

import com.example.springboot.db.replication.entity.MessageJoho;
import java.util.List;

/**
 * 統一Mapperインターフェース
 *
 * <p>
 * Primary/ReadReplicaの両方で使用
 * </p>
 */
public interface MessageJohoMapper {
  // 読み取り系メソッド
  MessageJoho selectByPrimaryKey(Integer no);

  List<MessageJoho> selectAll();

  long count();

  // 更新系メソッド（Primary専用）
  int deleteByPrimaryKey(Integer no);

  int insert(MessageJoho data);

  int insertSelective(MessageJoho data);

  int updateByPrimaryKeySelective(MessageJoho data);

  int updateByPrimaryKey(MessageJoho data);
}
