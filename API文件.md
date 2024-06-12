<!-- FORMAT: 1A
HOST: https://polls.apiblueprint.org/ -->
<style>
    code {
        max-height:100px;
        background-color: red;
    }
</style>

# 指令Command1 
## 網路程式設計 API文件
##### version: 1.3.0
##### last edit time: 2024/06/12 10:35:50 PM


<!-- Polls is a simple API allowing consumers to view polls and vote in them. -->

<!-- ## Questions Collection [/questions] -->
---
### Ping [GET]
http://26.208.147.218:60922/ping

+ 回傳 200 (application/json)
    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": "alive"
            }
---
### 創建帳戶 [POST]
http://26.208.147.218:60922/api/v1/createAccount

+ 輸入 (application/json)

    + Body

            {
                "name": "我是誰我是誰我是誰",
                "loginAccount":"command126402@gmail.com",
                "loginPassword": "25d55ad283aa400af464c76d713c07ad"
            }

+ 回傳 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": ""
            }
        
        
> noName
+ 回傳 200 (application/json)

    + Body

            {
                "success": false,
                "errorMessage": "name沒有資料",
                "exception": "",
                "data": ""
            }
        
> noLoginAccount
+ 回傳 200 (application/json)
    + Body

            {
                "success": false,
                "errorMessage": "loginAccount沒有資料",
                "exception": "",
                "data": ""
            }
        
> noLoginPassword
+ 回傳 200 (application/json)
    + Body

            {
                "success": false,
                "errorMessage": "loginPassword沒有資料",
                "exception": "",
                "data": ""
            }

> loginAccountHasFound
+ 回傳 200 (application/json)
    + Body

            {
                "success": false,
                "errorMessage": "loginAccount已經有人使用",
                "exception": "",
                "data": ""
            }
---

### 登入帳戶 [POST]
http://26.208.147.218:60922/api/v1/loginAccount

#### onlyToken
+ 輸入 (application/json)
    + Body

            {
                "token": "wPtxDzgDP/u2Qs0StJ0r9y8yLCIIdFkU"
            }

+ 回傳 200 (application/json)
    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": {
                    "id": "1",
                    "userId": "Command1",
                    "name": "指令 Command1",
                    "createTime": "2004-01-06 20:07:09",
                    "photoStickerBase64": "",
                    "chatRooms": {
                        "room_431c1373_2b76_4176_8962_b8419ffa6112": 0,
                        "room_6747db05_4d30_430c_bec6_1c49f942a7c3": 0,
                        "room_a84013c4_06a9_46f6_bdfa_272d408c9581": 0,
                        "room_e3e810ce_9660_43d7_93c0_f68489e72815": 0,
                        "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0": 0
                    },
                    "chatRoomsSerialize": "{\"room_431c1373_2b76_4176_8962_b8419ffa6112\":0,\"room_6747db05_4d30_430c_bec6_1c49f942a7c3\":0,\"room_a84013c4_06a9_46f6_bdfa_272d408c9581\":0,\"room_e3e810ce_9660_43d7_93c0_f68489e72815\":0,\"room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0\":0}"
                }
            }

#### onlyLogin
+ 輸入 (application/json)
    + Body

            {
                "loginAccount":"command1264@gmail.com",
                "loginPassword": "25d55ad283aa400af464c76d713c07ad"
            }

+ 回傳 200 (application/json)
    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": {
                    "id": "1",
                    "token": "wPtxDzgDP/u2Qs0StJ0r9y8yLCIIdFkU",
                    "expiredTime": "2024-06-11 01:07:20",
                    "expiredTimeWithTime": "2024-06-11T01:07:20"
                }
            }

#### Both
+ 輸入 (application/json)
    + Body

            {
                "token": "mjO/xaqZxZEmZXYYCrib2FEhris6M71o",
                "loginAccount":"command1264@gmail.com",
                "loginPassword": "25d55ad283aa400af464c76d713c07ad"
            }

+ 回傳 200 (application/json)
    + Body

            同 onlyToken
---
### 更換Token [POST]
http://26.208.147.218:60922/api/v1/changeToken
+ 輸入 (application/json)
    + Body

            {
                "token": "wPtxDzgDP/u2Qs0StJ0r9y8yLCIIdFkU"
            }
+ 回傳 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": {
                    "id": "1",
                    "token": "EF8kw6n3/9BeBGZWGDqzEsgU+UaYjCwQ",
                    "expiredTime": "2024-06-11 01:55:51",
                    "expiredTimeWithTime": "2024-06-11T01:55:51"
                }
            }

---
### 創建聊天室 [POST]
http://26.208.147.218:60922/api/v1/createUserChatRoom

#### userIds
+ 輸入 (application/json)
    + Body

            {
                "token": "EGW7ZcsAFSdFBH2qTSG3QyIyiKH0LL+5",
                "userIds":[
                    "Command1",
                    "Taiwan_PingLord",
                    "5qXJ5yMjQW","Taiwan_PingLord2"
                ]
            }
<!-- #### ids (Number/String)
+ 輸入 (application/json)
    + Body

            {
                "ids" : [
                    1,
                    2, 
                    4
                ]
            } -->
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
### 取得聊天室訊息 [POST]
http://26.208.147.218:60922/api/v1/getUserChatRoomChats

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
                            "message": "",
                            "type": "space",
                            "time": "2024-05-30 01:10:15",
                            "modify": false,
                            "deleted": true
                        },
                        {
                            "id": 9,
                            "sender": "台灣Ping霸主",
                            "senderId": "Taiwan_PingLord2",
                            "message": "測試訊息1",
                            "type": "text",
                            "time": "2024-05-30 01:10:17",
                            "modify": false,
                            "deleted": false
                        },
                        {
                            "id": 10,
                            "sender": null,
                            "senderId": null,
                            "message": "test1",
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
http://26.208.147.218:60922/api/v1/userSendMessage

#### chatRoomName (String/JsonArray)
+ 輸入 (application/json)

    + Body

            {
                "token": "wIzBwZ9P/6EOdhMAKczQNMVvxTxfQMMN",
                "chatRoomName": "room_6747db05_4d30_430c_bec6_1c49f942a7c3",
                "message" : {
                    "sender" : "Taiwan_PingLord2",
                    "message" : "測試訊息1",
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
---

### 取得聊天室更新訊息 [POST]
http://26.208.147.218:60922/api/v1/getUserReceiveMessage

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
                            "message": "測試訊息1",
                            "type": "text",
                            "time": "2024-05-30 01:10:15",
                            "modify": false,
                            "deleted": false
                        },
                        {
                            "id": 9,
                            "sender": "台灣Ping霸主",
                            "senderId": "Taiwan_PingLord2",
                            "message": "測試訊息1",
                            "type": "text",
                            "time": "2024-05-30 01:10:17",
                            "modify": false,
                            "deleted": false
                        },
                        {
                            "id": 10,
                            "sender": null,
                            "senderId": null,
                            "message": "test1",
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
http://26.208.147.218:60922/api/v1/userReadMessage

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