package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.accouunt.UserAndRooms;
import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.FileDao;
import com.github.command1264.webProgramming.dao.MessagesDao;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.MessageSendReceive;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.text.MessageFormat;

@Component
public class FileService {
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private MessagesDao messagesDao;
    @Autowired
    private FileDao fileDao;

    @Value(("${web.upload-path}"))
    private String uploadPath;

    public ReturnJsonObject uploadFile(HttpServletRequest request, String token, String chatRoomName, String type, MultipartFile file) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (request == null || token == null || chatRoomName == null || type == null || file == null)
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());

        UserAndRooms userAndRooms = accountDao.getUserAndRoomsWithId(accountDao.getIdWithToken(token));
        if (userAndRooms == null)
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.tokenNoPermission.getMessage());

        if (!userAndRooms.getChatRooms().containsKey(chatRoomName))
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.tokenNoPermission.getMessage());

        MessageSendReceive messageSendReceive = messagesDao.getUserChatRoomLastChat(token, chatRoomName);
        String fileMessageId = (messageSendReceive == null) ? "0" : String.valueOf(messageSendReceive.getId() + 1);

//        String typePath = switch (type.toLowerCase()) {
//            case "image" -> "imageData";
//            case "video" -> "videoData";
//            default -> "fileData";
//        };


        Path filePath = Path.of(chatRoomName, fileMessageId, file.getOriginalFilename());
        Path saveFilePath = Path.of(uploadPath, filePath.toString());

        if (!fileDao.saveMultipartFile(saveFilePath.toFile(), file))
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.messageSaveFailed.getMessage());

//        return returnJsonObject
//                .setSuccessAndData(
//                        MessageFormat.format("{0}://{1}:{2,number,#}/{3}",
//                                request.getScheme(),
//                                request.getServerName(),
//                                request.getServerPort(),
//                                StringUtils.replace(
//                                    filePath.toString(),
//                                    "\\", "/"
//                                )
//                ));
        return returnJsonObject
                .setSuccessAndData("/" + StringUtils.replace(
                        filePath.toString(),
                        "\\", "/"
                )
        );
    }
}
