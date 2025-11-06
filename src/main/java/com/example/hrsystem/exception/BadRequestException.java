package com.example.hrsystem.exception;

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

public class BadRequestException extends RuntimeException {

    public  BadRequestException(String message){
        super(message);
    }

}
