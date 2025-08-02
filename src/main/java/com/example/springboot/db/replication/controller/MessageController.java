package com.example.springboot.db.replication.controller;

import com.example.springboot.db.replication.entity.MessageJoho;
import com.example.springboot.db.replication.model.Message;
import com.example.springboot.db.replication.model.MessageForm;
import com.example.springboot.db.replication.service.ReadOnlyService;
import com.example.springboot.db.replication.service.UpdateService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * メッセージの一覧表示と登録を行うコントローラ
 *
 * <p>
 * Primary/Replica構成において、読み取り処理と書き込み処理を分離して実行する
 * </p>
 */
@Controller
@RequiredArgsConstructor
public class MessageController {

  private final ReadOnlyService readOnlyService;

  private final UpdateService updateService;

  @GetMapping("/")
  public String index() {
    return "redirect:/list";
  }

  /**
   * メッセージ一覧画面を表示する. Replicaデータベースから読み取りを行う
   *
   * @param model Viewに渡すModelオブジェクト
   * @return メッセージ一覧画面のテンプレート名
   */
  @GetMapping("/list")
  public String list(Model model) {
    System.out.println("list");
    List<MessageJoho> list = readOnlyService.findAll();
    List<Message> messages =
        list.stream().map(table -> new Message(table.getNo(), table.getMessage())).toList();
    model.addAttribute("messages", messages);
    return "list";
  }

  @GetMapping("/regist")
  public String registForm(Model model) {
    model.addAttribute("messageForm", new MessageForm());
    return "regist";
  }

  /**
   * メッセージを登録する. Primaryデータベースへ書き込みを行う
   *
   * @param messageForm 登録するメッセージフォーム
   * @param redirectAttributes リダイレクト先に引き継ぐ情報
   * @return リダイレクト先のURL
   */
  @PostMapping("/regist")
  public String regist(@ModelAttribute MessageForm messageForm,
      RedirectAttributes redirectAttributes) {
    MessageJoho newData = new MessageJoho();
    newData.setMessage(messageForm.getContent());
    updateService.insert(newData);
    return "redirect:/list";
  }
}
