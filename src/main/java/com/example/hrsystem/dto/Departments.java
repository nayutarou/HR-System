package com.example.hrsystem.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotBlank; // JSR 301
import jakarta.validation.constraints.Size;
@Data
public class Departments {
    //  部署entity
    //  部署id
    //  部署名(○○科・○○部)
    //  所在地(八戸市、盛岡市)
    private Long id;
    @NotBlank(message = "部署名は必須項目です") // ◀︎ 空白NG
    @Size(max = 15, message = "部署名は15文字以内で入力してください")
    private String name;
    @NotBlank(message = "所在地は必須項目です")
    @Size(max = 10,message = "所在地は10文字以内で入力してください")
    private String location;
    // 追加推奨
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
