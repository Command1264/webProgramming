# 指令Command1
## WebProgramming API文件
##### version: 25w01a
##### last edit time: 2025/01/04 11:22:10 PM

#### [API文件](API%E6%96%87%E4%BB%B6.md)
#### [API文件-Account](API%E6%96%87%E4%BB%B6-Account.md)
#### [API文件-UserChatRoom](API%E6%96%87%E4%BB%B6-UserChatRoom.md)
#### [API文件-Message](API%E6%96%87%E4%BB%B6-Message.md)
#### [API文件-AI](API%E6%96%87%E4%BB%B6-AI.md)
#### [API文件-WebSocket(WIP)](API%E6%96%87%E4%BB%B6-WebSocket.md)

---
### 創建聊天室 [POST]
https://command1264.xserver.tw/api/v1/createUserChatRoom

+ 輸入 (application/json)
    + Body

            {
                "token": "EGW7ZcsAFSdFBH2qTSG3QyIyiKH0LL+5",
                "name": "ChatRoomName",
                "userIds":[
                    "Command1",
                    "Taiwan_PingLord",
                    "5qXJ5yMjQW","Taiwan_PingLord2"
                ]
            }

+ 回傳 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": "room_e3e810ce_9660_43d7_93c0_f68489e72815"
            }

---
### 取得聊天室資訊 [POST]
https://command1264.xserver.tw/api/v1/getUserChatRoom

#### chatRoomName (String/JsonArray)
+ 輸入 (application/json)
    + Body

            {
                "token": "DlyxXh9C9pTCTDUNPFNjEPl/k68oFRQG",
                "chatRoomName": [
                    "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0",
                    "room_a84013c4_06a9_46f6_bdfa_272d408c9581",
                    "room_431c1373_2b76_4176_8962_b8419ffa6112",
                    "room_25f77d46_bbc8_4ba0_bcdd_035226a5296f"
                ]
            }

+ 回傳 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": {
                    "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0": {
                        "uuid": "e9ac0054-fd27-44a8-b2ca-eb704aebf8c0",
                        "name": "chatRoom6",
                        "users": "[\"1\",\"2\",\"3\"]",
                        "lastModify": "2024-06-18 02:59:37",
                        "usersObject": [
                            {
                                "id": "",
                                "userId": "Command1",
                                "name": "指令 Command1",
                                "createTime": "2004-01-06 20:07:09",
                                "photoStickerBase64": "",
                                "deleted": false
                            },
                            {
                                "id": "",
                                "userId": "Taiwan_PingLord",
                                "name": "台灣Ping霸主",
                                "createTime": "2007-09-22 20:04:01",
                                "photoStickerBase64": "",
                                "deleted": false
                            },
                            {
                                "id": "",
                                "userId": "Taiwan_PingLord2",
                                "name": "台灣Ping霸主",
                                "createTime": "2024-06-04 01:18:15",
                                "photoStickerBase64": "",
                                "deleted": false
                            }
                        ],
                        "lastModifyWithTime": "2024-06-18T02:59:37",
                        "userList": [
                            "1",
                            "2",
                            "3"
                        ]
                    },
                    "room_a84013c4_06a9_46f6_bdfa_272d408c9581": {
                        "uuid": "a84013c4-06a9-46f6-bdfa-272d408c9581",
                        "name": "chatRoom4",
                        "users": "[\"1\",\"2\"]",
                        "lastModify": "2024-06-16 18:12:56",
                        "usersObject": [
                            {
                                "id": "",
                                "userId": "Command1",
                                "name": "指令 Command1",
                                "createTime": "2004-01-06 20:07:09",
                                "photoStickerBase64": "",
                                "deleted": false
                            },
                            {
                                "id": "",
                                "userId": "Taiwan_PingLord",
                                "name": "台灣Ping霸主",
                                "createTime": "2007-09-22 20:04:01",
                                "photoStickerBase64": "",
                                "deleted": false
                            }
                        ],
                        "lastModifyWithTime": "2024-06-16T18:12:56",
                        "userList": [
                            "1",
                            "2"
                        ]
                    },
                    "room_431c1373_2b76_4176_8962_b8419ffa6112": {
                        "uuid": "431c1373-2b76-4176-8962-b8419ffa6112",
                        "name": "chatRoom1",
                        "users": "[\"1\"]",
                        "lastModify": "2024-06-04 16:41:17",
                        "usersObject": [
                            {
                                "id": "",
                                "userId": "Command1",
                                "name": "指令 Command1",
                                "createTime": "2004-01-06 20:07:09",
                                "photoStickerBase64": "",
                                "deleted": false
                            }
                        ],
                        "lastModifyWithTime": "2024-06-04T16:41:17",
                        "userList": [
                            "1"
                        ]
                    }
                }
            }

---

#### [API文件](API%E6%96%87%E4%BB%B6.md)
#### [API文件-Account](API%E6%96%87%E4%BB%B6-Account.md)
#### [API文件-UserChatRoom](API%E6%96%87%E4%BB%B6-UserChatRoom.md)
#### [API文件-Message](API%E6%96%87%E4%BB%B6-Message.md)
#### [API文件-AI](API%E6%96%87%E4%BB%B6-AI.md)
#### [API文件-WebSocket(WIP)](API%E6%96%87%E4%BB%B6-WebSocket.md)