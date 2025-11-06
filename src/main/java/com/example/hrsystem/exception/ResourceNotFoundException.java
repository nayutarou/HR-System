package com.example.hrsystem.exception;

import jakarta.servlet.annotation.*;

public class ResourceNotFoundException extends RuntimeException {

    // idが見つからないときの例外処理
    public ResourceNotFoundException(long id){
        super("指定された" + id + "というidが見つかりませんでした。");
    }
    // リソースタイプが不明な場合や、特定のメッセージを渡したい場合のコンストラクタを追加
    public ResourceNotFoundException(String message){
        super(message);
    }

}
