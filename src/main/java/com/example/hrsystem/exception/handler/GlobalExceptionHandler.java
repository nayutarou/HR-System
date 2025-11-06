package com.example.hrsystem.exception.handler;

import com.example.hrsystem.exception.BadRequestException;
import com.example.hrsystem.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException e){
        return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
    }

    // 500の例外処理を追加
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception e) {
        // 予期せぬエラーの詳細をサーバー側のログに出力（デバッグのため）
        // logger.error("予期せぬサーバーエラー:", e);

        // クライアントには詳細を隠し、統一したメッセージだけを返す
        return new ResponseEntity<>("サーバーで予期せぬエラーが発生しました。",
                HttpStatus.INTERNAL_SERVER_ERROR); // 500を返す
    }

}
