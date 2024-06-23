package com.github.command1264.webProgramming.controller;

import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@CrossOrigin(origins = "*")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/testMultipart")
    public String testMultipart(@RequestPart("file") MultipartFile file) {
        return file.getOriginalFilename() + " : " + file.getName() + " : " + file.getContentType();
    }

    @PutMapping("/api/v1/uploadFile")
    public ReturnJsonObject uploadFile(HttpServletRequest request,
                                       @RequestPart("token") String token,
                                       @RequestPart("chatRoomName") String chatRoomName,
                                       @RequestPart("type") String type,
                                       @RequestPart("file") MultipartFile file) {
        return fileService.uploadFile(request, token, chatRoomName, type, file);
    }
}
