# 指令Command1
## 網路程式設計 API文件
##### version: 24w53a
##### last edit time: 2024/12/31 05:36:30 PM

#### [API文件](API%E6%96%87%E4%BB%B6.md)
#### [API文件-Account](API%E6%96%87%E4%BB%B6-Account.md)
#### [API文件-UserChatRoom](API%E6%96%87%E4%BB%B6-UserChatRoom.md)
#### [API文件-Message](API%E6%96%87%E4%BB%B6-Message.md)
#### [API文件-AI](API%E6%96%87%E4%BB%B6-AI.md)
#### [API文件-WebSocket(WIP)](API%E6%96%87%E4%BB%B6-WebSocket.md)

---
### 取得聊天室訊息 [POST]
https://command1264.xserver.tw/api/v1/getUserChatRoomChats

#### chatRoomName (String/JsonArray)
+ 輸入 (application/json)

    + Body

            {
                "token": "wIzBwZ9P/6EOdhMAKczQNMVvxTxfQMMN",
                "chatRoomName": [
                    "room_431c1373_2b76_4176_8962_b8419ffa6112",
                    "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0"
                ]
            }

+ 回傳 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": {
                    "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0": [
                        {
                            "id": 1,
                            "sender": "指令 Command1",
                            "senderId": "Command1",
                            "message": {
                                "message": ""
                            },
                            "type": "space",
                            "time": "2024-05-30 01:10:15",
                            "modify": false,
                            "deleted": true
                        },
                        {
                            "id": 9,
                            "sender": "台灣Ping霸主",
                            "senderId": "Taiwan_PingLord2",
                            "message": {
                                "message": "測試訊息1"
                            },
                            "type": "text",
                            "time": "2024-05-30 01:10:17",
                            "modify": false,
                            "deleted": false
                        },
                        {
                            "id": 10,
                            "sender": null,
                            "senderId": null,
                            "message": {
                                "message": "test1"
                            },
                            "type": "text",
                            "time": "2024-06-09 22:30:25",
                            "modify": false,
                            "deleted": false
                        }
                    ]
                }
            }

---
### 使用者傳送訊息 [PUT]
https://command1264.xserver.tw/api/v1/userSendMessage

#### chatRoomName
+ 輸入 (application/json)

    + Body

            {
                "token": "wIzBwZ9P/6EOdhMAKczQNMVvxTxfQMMN",
                "chatRoomName": "room_6747db05_4d30_430c_bec6_1c49f942a7c3",
                "message" : {
                    "sender" : "Taiwan_PingLord2",
                    "message": {
                        "message": "測試訊息1"
                    },
                    "type" : "text",
                    "time" : "2024-05-30 01:10:17"
                }
            }

+ 回傳 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": ""
            }

#### File
+ 輸入 (application/json)

    + Body

            {
                "token": "kBeoN9AN5W6pq5FVoU7BJN+Hy1Z9d9mK",
                "chatRoomName": "room_431c1373_2b76_4176_8962_b8419ffa6112",
                "message" : {
                    "sender" : "Command1",
                    "message": {
                        "image" : [
                            "/room_431c1373_2b76_4176_8962_b8419ffa6112/0/9cf292d2dc9f4af790d56d54b7e5359a.jpg",
                            "/room_431c1373_2b76_4176_8962_b8419ffa6112/0/0b5367b81f6b135598440fa10b9070d4.png",
                            "/room_431c1373_2b76_4176_8962_b8419ffa6112/0/0b099a07f76b1b32.gif"
                        ]
                    },
                    "type" : "image",
                    "time" : "2024-06-23 20:42:17"
                }
            }

+ 回傳 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": ""
            }
---
### 上傳聊天室檔案 [PUT]
https://command1264.xserver.tw/api/v1/uploadFile

#### chatRoomName
+ 輸入 (application/json)
    + Header

            "Content-Type": "multipart/form-data"

    + Body From-data

            "token": "aKbjO7IHCCDjaXFlvw1qP3MUH87fjsL2"
            "chatRoomName": "room_431c1373_2b76_4176_8962_b8419ffa6112"
            "type": 檔案類型
            "file": 檔案(可以多個)

+ 回傳 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": [
                    "/room_431c1373_2b76_4176_8962_b8419ffa6112/0/9cf292d2dc9f4af790d56d54b7e5359a.jpg",
                    "/room_431c1373_2b76_4176_8962_b8419ffa6112/0/0b5367b81f6b135598440fa10b9070d4.png",
                    "/room_431c1373_2b76_4176_8962_b8419ffa6112/0/0b099a07f76b1b32.gif"
                ]
            }

---

### 取得聊天室更新訊息 [POST]
https://command1264.xserver.tw/api/v1/getUserReceiveMessage

#### chatRoomName (String/JsonArray)
+ 輸入 (application/json)

    + Body

            {
                "token": "wIzBwZ9P/6EOdhMAKczQNMVvxTxfQMMN",
                "chatRoomName": {
                    "room_431c1373_2b76_4176_8962_b8419ffa6112": 0,
                    "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0": 1
                }
            }

+ 回傳 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": {
                    "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0": [
                        {
                            "id": 2,
                            "sender": "指令 Command1",
                            "senderId": "Command1",
                            "message": {
                                "message": "測試訊息1"
                            },
                            "type": "text",
                            "time": "2024-05-30 01:10:15",
                            "modify": false,
                            "deleted": false
                        },
                        {
                            "id": 9,
                            "sender": "台灣Ping霸主",
                            "senderId": "Taiwan_PingLord2",
                            "message": {
                                "message": "測試訊息1"
                            },
                            "type": "text",
                            "time": "2024-05-30 01:10:17",
                            "modify": false,
                            "deleted": false
                        },
                        {
                            "id": 10,
                            "sender": null,
                            "senderId": null,
                            "message": {
                                "message": "test1"
                            },
                            "type": "text",
                            "time": "2024-06-09 22:30:25",
                            "modify": false,
                            "deleted": false
                        }
                    ]
                }
            }
---

### 已讀聊天室訊息進度更新 [POST]
https://command1264.xserver.tw/api/v1/userReadMessage

#### chatRoomName
+ 輸入 (application/json)

    + Body

            {
                "token": "7nY6oJXSTbNm/OksIzXretRRRoTDOJa0",
                "chatRoomName": {
                    "room_431c1373_2b76_4176_8962_b8419ffa6112": 10,
                    "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0": 10
                }
            }
+ 回傳 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": {
                    "id": "1",
                    "chatRooms": {
                        "room_431c1373_2b76_4176_8962_b8419ffa6112": 10,
                        "room_6747db05_4d30_430c_bec6_1c49f942a7c3": 0,
                        "room_a84013c4_06a9_46f6_bdfa_272d408c9581": 0,
                        "room_e3e810ce_9660_43d7_93c0_f68489e72815": 0,
                        "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0": 10
                    },
                    "chatRoomsSerialize": "{\"room_431c1373_2b76_4176_8962_b8419ffa6112\":10,\"room_6747db05_4d30_430c_bec6_1c49f942a7c3\":0,\"room_a84013c4_06a9_46f6_bdfa_272d408c9581\":0,\"room_e3e810ce_9660_43d7_93c0_f68489e72815\":0,\"room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0\":10}"
                }
            }

---

#### [API文件](API%E6%96%87%E4%BB%B6.md)
#### [API文件-Account](API%E6%96%87%E4%BB%B6-Account.md)
#### [API文件-UserChatRoom](API%E6%96%87%E4%BB%B6-UserChatRoom.md)
#### [API文件-Message](API%E6%96%87%E4%BB%B6-Message.md)
#### [API文件-AI](API%E6%96%87%E4%BB%B6-AI.md)
#### [API文件-WebSocket(WIP)](API%E6%96%87%E4%BB%B6-WebSocket.md)