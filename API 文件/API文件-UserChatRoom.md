# 指令Command1 
## 網路程式設計 API文件
##### version: 24w25a
##### last edit time: 2024/06/17 10:39:30 PM

#### [API文件](https://github.com/Command1264/webProgramming/blob/master/API%20%E6%96%87%E4%BB%B6/API%E6%96%87%E4%BB%B6.md)
#### [API文件-Account](https://github.com/Command1264/webProgramming/blob/master/API%20%E6%96%87%E4%BB%B6/API%E6%96%87%E4%BB%B6-Account.md)
#### [API文件-Message](https://github.com/Command1264/webProgramming/blob/master/API%20%E6%96%87%E4%BB%B6/API%E6%96%87%E4%BB%B6-Message.md)
#### [API文件-UserChatRoom](https://github.com/Command1264/webProgramming/blob/master/API%20%E6%96%87%E4%BB%B6/API%E6%96%87%E4%BB%B6-UserChatRoom.md)
---
### 創建聊天室 [POST]
http://26.208.147.218:60922/api/v1/createUserChatRoom

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
http://26.208.147.218:60922/api/v1/getUserChatRoom

#### chatRoomName (String/JsonArray)
+ 輸入 (application/json)
    + Body

            {
                "token": "EF8kw6n3/9BeBGZWGDqzEsgU+UaYjCwQ",
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
                    "room_431c1373_2b76_4176_8962_b8419ffa6112": {
                        "uuid": "431c1373-2b76-4176-8962-b8419ffa6112",
                        "name": "431c1373-2b76-4176-8962-b8419ffa6112",
                        "users": "[\"1\"]",
                        "lastModify": "2024-06-04 16:41:17",
                        "userList": [
                            "1"
                        ],
                        "lastModifyWithTime": "2024-06-04T16:41:17"
                    },
                    "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0": {
                        "uuid": "e9ac0054-fd27-44a8-b2ca-eb704aebf8c0",
                        "name": "e9ac0054-fd27-44a8-b2ca-eb704aebf8c0",
                        "users": "[\"1\",\"2\",\"3\"]",
                        "lastModify": "2024-06-04 16:28:30",
                        "userList": [
                            "1",
                            "2",
                            "3"
                        ],
                        "lastModifyWithTime": "2024-06-04T16:28:30"
                    },
                    "room_a84013c4_06a9_46f6_bdfa_272d408c9581": {
                        "uuid": "a84013c4-06a9-46f6-bdfa-272d408c9581",
                        "name": "",
                        "users": "[\"1\",\"2\"]",
                        "lastModify": "2024-06-04 16:29:22",
                        "userList": [
                            "1",
                            "2"
                        ],
                        "lastModifyWithTime": "2024-06-04T16:29:22"
                    }
                }
            }

---
#### [API文件](https://github.com/Command1264/webProgramming/blob/master/API%20%E6%96%87%E4%BB%B6/API%E6%96%87%E4%BB%B6.md)
#### [API文件-Account](https://github.com/Command1264/webProgramming/blob/master/API%20%E6%96%87%E4%BB%B6/API%E6%96%87%E4%BB%B6-Account.md)
#### [API文件-Message](https://github.com/Command1264/webProgramming/blob/master/API%20%E6%96%87%E4%BB%B6/API%E6%96%87%E4%BB%B6-Message.md)
#### [API文件-UserChatRoom](https://github.com/Command1264/webProgramming/blob/master/API%20%E6%96%87%E4%BB%B6/API%E6%96%87%E4%BB%B6-UserChatRoom.md)