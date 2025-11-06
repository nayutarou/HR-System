package com.example.hrsystem.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class Employees {
    // 従業員entity
    // 従業員id
    // 苗字
    // 名前
    // メルアド
    // 所属部署ID FK
    // 役職
    // 採用日
    private Long id;
    @NotBlank(message = "姓は必須項目です") // 空白（スペースのみ）も許さない
    @Size(max = 10, message = "姓は10文字以内で入力してください")
    private String lastName;

    @NotBlank(message = "名は必須項目です")
    @Size(max = 10, message = "名は10文字以内で入力してください")
    private String firstName;

    @NotBlank(message = "メールアドレスは必須項目です")
    @Email(message = "メールアドレスの形式が正しくありません") // @ ちゃんとか
    @Size(max = 100, message = "メールアドレスは100文字以内で入力してください")
    private String email;

    // ★ 修正点: Long (オブジェクト型) なので @NotBlank ではなく @NotNull
    @NotNull(message = "所属部署を選択してください")
    private Long departmentId;

    @NotBlank(message = "役職は必須項目です")
    @Size(max = 15, message = "役職は15文字以内で入力してください")
    private String position;

    // ★ 修正点: LocalDate にも @NotNull
    @NotNull(message = "入社日は必須項目です")
    // ★ おまけ: 未来の日付での入社は禁止する（「今日」または「過去」のみ許可）
    @PastOrPresent(message = "入社日は、本日または過去の日付である必要があります")
    private LocalDate hireDate;
    private OffsetDateTime createdAt;
    // 更新日時 (レスポンスとして追加推奨)
    private OffsetDateTime updatedAt;

}
