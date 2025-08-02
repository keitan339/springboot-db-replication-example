package com.example.springboot.db.replication.service;

import static org.assertj.core.api.Assertions.assertThat;

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
@Transactional
class UpdateServiceTest {

  @Autowired
  private UpdateService updateService;

  @Autowired
  private MessageJohoMapper messageJohoMapper;

  private MessageJoho messageJoho;

  @BeforeEach
  void setUp() {
    messageJoho = new MessageJoho();
    messageJoho.setMessage("New Test Data");
  }

  @Test
  @DisplayName("データの挿入が可能であることを確認")
  void testInsert() {
    // when
    int result = updateService.insert(messageJoho);

    // then
    assertThat(result).isEqualTo(1);
    assertThat(messageJoho.getNo()).isNotNull();

    // 挿入されたデータを確認
    MessageJoho inserted = messageJohoMapper.selectByPrimaryKey(messageJoho.getNo());
    assertThat(inserted).isNotNull();
    assertThat(inserted.getMessage()).isEqualTo("New Test Data");
  }

  @Test
  @DisplayName("データの更新が可能であることを確認")
  void testUpdate() {
    // given
    MessageJoho existing = messageJohoMapper.selectByPrimaryKey(1);
    existing.setMessage("Updated Data");

    // when
    int result = updateService.update(existing);

    // then
    assertThat(result).isEqualTo(1);

    MessageJoho updated = messageJohoMapper.selectByPrimaryKey(1);
    assertThat(updated.getMessage()).isEqualTo("Updated Data");
  }

  @Test
  @DisplayName("データの削除が可能であることを確認")
  void testDelete() {
    // when
    int result = updateService.delete(3);

    // then
    assertThat(result).isEqualTo(1);

    MessageJoho deleted = messageJohoMapper.selectByPrimaryKey(3);
    assertThat(deleted).isNull();
  }

  @Test
  @DisplayName("データの参照が可能であることを確認")
  void testSelect() {
    // when
    MessageJoho result = messageJohoMapper.selectByPrimaryKey(1);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getNo()).isEqualTo(1);
    assertThat(result.getMessage()).isEqualTo("メッセージ1");
  }
}
