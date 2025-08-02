package com.example.springboot.db.replication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.springboot.db.replication.entity.MessageJoho;
import com.example.springboot.db.replication.mapper.MessageJohoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional(readOnly = true)
class ReadOnlyServiceTest {

  @Autowired
  private ReadOnlyService readOnlyService;

  @Autowired
  private MessageJohoMapper messageJohoMapper;

  private MessageJoho messageJoho;

  @BeforeEach
  void setUp() {
    messageJoho = new MessageJoho();
    messageJoho.setNo(1);
    messageJoho.setMessage("Update Attempt");
  }

  @Test
  @DisplayName("参照が可能であることを確認")
  void testFindById() {
    // when
    MessageJoho result = readOnlyService.findById(1);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getNo()).isEqualTo(1);
    assertThat(result.getMessage()).isEqualTo("メッセージ1");
  }

  @Test
  @DisplayName("更新しようとするとエラーになることを確認")
  void testUpdateShouldFail() {
    // PostgreSQLでは読み取り専用トランザクションで更新操作がエラーになる

    assertThatThrownBy(() -> {
      messageJohoMapper.updateByPrimaryKey(messageJoho);
    }).isInstanceOf(Exception.class).hasMessageContaining("read-only");
  }

  @Test
  @DisplayName("挿入しようとするとエラーになることを確認")
  void testInsertShouldFail() {
    MessageJoho newEntity = new MessageJoho();
    newEntity.setMessage("Insert Attempt");

    // PostgreSQLでは読み取り専用トランザクションで挿入操作がエラーになる
    assertThatThrownBy(() -> {
      messageJohoMapper.insert(newEntity);
    }).isInstanceOf(Exception.class).hasMessageContaining("read-only");
  }

  @Test
  @DisplayName("削除しようとするとエラーになることを確認")
  void testDeleteShouldFail() {
    // PostgreSQLでは読み取り専用トランザクションで削除操作がエラーになる
    assertThatThrownBy(() -> {
      messageJohoMapper.deleteByPrimaryKey(1);
    }).isInstanceOf(Exception.class).hasMessageContaining("read-only");
  }
}
