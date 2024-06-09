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
##### version: 1.1.2
##### edit time: 2024/06/10 02:07:30 AM


<!-- Polls is a simple API allowing consumers to view polls and vote in them. -->

<!-- ## Questions Collection [/questions] -->
---
### Ping [GET]
http://26.208.147.218:60922/ping

+ Response 200 (application/json)
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

+ Request (application/json)

    + Body

            {
                "name": "我是誰我是誰我是誰",
                "loginAccount":"command126402@gmail.com",
                "loginPassword": "25d55ad283aa400af464c76d713c07ad"
            }

+ Response 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": ""
            }
        
        
> noName
+ Response 200 (application/json)

    + Body

            {
                "success": false,
                "errorMessage": "name沒有資料",
                "exception": "",
                "data": ""
            }
        
> noLoginAccount
+ Response 200 (application/json)
    + Body

            {
                "success": false,
                "errorMessage": "loginAccount沒有資料",
                "exception": "",
                "data": ""
            }
        
> noLoginPassword
+ Response 200 (application/json)
    + Body

            {
                "success": false,
                "errorMessage": "loginPassword沒有資料",
                "exception": "",
                "data": ""
            }

> loginAccountHasFound
+ Response 200 (application/json)
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
+ Request (application/json)
    + Body

            {
                "token": "wPtxDzgDP/u2Qs0StJ0r9y8yLCIIdFkU"
            }

+ Response 200 (application/json)
    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": {
                    "id": "1",
                    "userId": "Command1",
                    "name": "指令 Command1",
                    "createTime": "2004-01-06 20:07:09.2200",
                    "photoStickerBase64": "",
                    "chatRooms": [
                        {
                            "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0": 0
                        },
                        {
                            "room_a84013c4_06a9_46f6_bdfa_272d408c9581": 0
                        },
                        {
                            "room_431c1373_2b76_4176_8962_b8419ffa6112": 0
                        },
                        {
                            "room_25f77d46_bbc8_4ba0_bcdd_035226a5296f": 0
                        },
                        {
                            "room_1fded8c8_b18e_4fcc_84f6_562ee4976015": 0
                        },
                        {
                            "room_0bf9cc76_c30e_44a1_83df_7c9dbbb00ddf": 0
                        },
                        {
                            "room_0bf9cc76_c30e_44a1_83df_7c9dbbb00ddf": 0
                        },
                        {
                            "room_6747db05_4d30_430c_bec6_1c49f942a7c3": 0
                        },
                        {
                            "room_e3e810ce_9660_43d7_93c0_f68489e72815": 0
                        }
                    ],
                    "chatRoomsSerialize": "[{\"room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0\":0},{\"room_a84013c4_06a9_46f6_bdfa_272d408c9581\":0},{\"room_431c1373_2b76_4176_8962_b8419ffa6112\":0},{\"room_25f77d46_bbc8_4ba0_bcdd_035226a5296f\":0},{\"room_1fded8c8_b18e_4fcc_84f6_562ee4976015\":0},{\"room_0bf9cc76_c30e_44a1_83df_7c9dbbb00ddf\":0},{\"room_0bf9cc76_c30e_44a1_83df_7c9dbbb00ddf\":0},{\"room_6747db05_4d30_430c_bec6_1c49f942a7c3\":0},{\"room_e3e810ce_9660_43d7_93c0_f68489e72815\":0}]"
                }
            }

#### onlyLogin
+ Request (application/json)
    + Body

            {
                "loginAccount":"command1264@gmail.com",
                "loginPassword": "25d55ad283aa400af464c76d713c07ad"
            }

+ Response 200 (application/json)
    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": {
                    "id": "1",
                    "token": "wPtxDzgDP/u2Qs0StJ0r9y8yLCIIdFkU",
                    "expiredTime": "2024-06-11 01:07:20.1052",
                    "expiredTimeWithTime": "2024-06-11T01:07:20.1052"
                }
            }

#### Both
+ Request (application/json)
    + Body

            {
                "token": "mjO/xaqZxZEmZXYYCrib2FEhris6M71o",
                "loginAccount":"command1264@gmail.com",
                "loginPassword": "25d55ad283aa400af464c76d713c07ad"
            }

+ Response 200 (application/json)
    + Body

            同 onlyToken
---
### 更換Token [POST]
http://26.208.147.218:60922/api/v1/changeToken
+ Request (application/json)
    + Body

            {
                "token": "wPtxDzgDP/u2Qs0StJ0r9y8yLCIIdFkU"
            }
+ Response 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": {
                    "id": "1",
                    "token": "EF8kw6n3/9BeBGZWGDqzEsgU+UaYjCwQ",
                    "expiredTime": "2024-06-11 01:55:51.9979",
                    "expiredTimeWithTime": "2024-06-11T01:55:51.9979"
                }
            }

---
### 創建聊天室 [POST]
http://26.208.147.218:60922/api/v1/createUserChatRoom

#### userIds
+ Request (application/json)
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
+ Request (application/json)
    + Body

            {
                "ids" : [
                    1,
                    2, 
                    4
                ]
            } -->
+ Response 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": "room_e3e810ce_9660_43d7_93c0_f68489e72815"
            }

---
### 取得聊天室資料 [GET]
http://26.208.147.218:60922/api/v1/getUsersChatRoomChats

#### chatRoomName (String/JsonArray)
+ Request (application/json)

    + Body

            {
                "token": "wIzBwZ9P/6EOdhMAKczQNMVvxTxfQMMN",
                "chatRoomName": [
                    "room_431c1373_2b76_4176_8962_b8419ffa6112",
                    "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0"
                ]
            }

+ Response 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": {
                    "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0": [
                        {
                            "id": 8,
                            "sender": "台灣Ping霸主",
                            "message": "測試訊息1",
                            "type": "text",
                            "time": "2024-05-30 01:10:17",
                            "modify": false,
                            "deleted": false
                        },
                        {
                            "id": 9,
                            "sender": "台灣Ping霸主",
                            "message": "測試訊息1",
                            "type": "text",
                            "time": "2024-05-30 01:10:17",
                            "modify": false,
                            "deleted": false
                        },
                        {
                            "id": 10,
                            "sender": null,
                            "message": "test1",
                            "type": "text",
                            "time": "2024-06-09 22:30:25.4586",
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
+ Request (application/json)

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

+ Response 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": ""
            }

### 取得聊天室更新訊息 [GET]
http://26.208.147.218:60922/api/v1/getUserReceiveMessage

#### chatRoomName (String/JsonArray)
+ Request (application/json)

    + Body

            {
                "token": "wIzBwZ9P/6EOdhMAKczQNMVvxTxfQMMN",
                "chatRoomName": {
                    "room_431c1373_2b76_4176_8962_b8419ffa6112": 0,
                    "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0": 1
                }
            }

+ Response 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": {
                    "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0": [
                        {
                            "id": 8,
                            "sender": "台灣Ping霸主",
                            "message": "測試訊息1",
                            "type": "text",
                            "time": "2024-05-30 01:10:17",
                            "modify": false,
                            "deleted": false
                        },
                        {
                            "id": 9,
                            "sender": "台灣Ping霸主",
                            "message": "測試訊息1",
                            "type": "text",
                            "time": "2024-05-30 01:10:17",
                            "modify": false,
                            "deleted": false
                        },
                        {
                            "id": 10,
                            "sender": null,
                            "message": "test1",
                            "type": "text",
                            "time": "2024-06-09 22:30:25.4586",
                            "modify": false,
                            "deleted": false
                        }
                    ]
                }
            }