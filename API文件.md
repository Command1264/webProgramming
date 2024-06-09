<!-- FORMAT: 1A
HOST: https://polls.apiblueprint.org/ -->

# 指令Command1 
## 網路程式設計 API文件
##### version: 1.1.1
##### edit time: 2024/06/09 10:04:30 PM


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
                "token": "mjO/xaqZxZEmZXYYCrib2FEhris6M71o"
            }

+ Response 200 (application/json)
    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": "{\"chatRooms\":[],\"id\":\"11\",\"userId\":\"EKrqgg9BHQ\",\"name\":\"我是誰我是誰我是誰\",\"createTime\":\"2024-06-08 16:11:52.2539\",\"photoStickerBase64\":\"\"}"
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
            "data": "{\"userAndRooms\":\"{\\\"chatRooms\\\":[],\\\"id\\\":\\\"11\\\",\\\"userId\\\":\\\"EKrqgg9BHQ\\\",\\\"name\\\":\\\"我是誰我是誰我是誰\\\",\\\"createTime\\\":\\\"2024-06-08 16:11:52.2539\\\",\\\"photoStickerBase64\\\":\\\"\\\"}\",\"token\":\"{\\\"id\\\":\\\"11\\\",\\\"token\\\":\\\"nCKO50yfvdvJ+jol8DNFHhfolf2qsx+5\\\",\\\"expiredTime\\\":\\\"2024-06-09 16:19:29.9721\\\"}\"}"
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

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": "{\"chatRooms\":[],\"id\":\"11\",\"userId\":\"EKrqgg9BHQ\",\"name\":\"我是誰我是誰我是誰\",\"createTime\":\"2024-06-08 16:11:52.2539\",\"photoStickerBase64\":\"\"}"
            }
---
### 更換Token [POST]
http://26.208.147.218:60922/api/v1/changeToken
+ Request (application/json)
    + Body

            {
                "token": "XZNhgf7WR1x5RPYvz6+o8M9xidcGW10r"
            }
+ Response 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": "{\"id\":\"1\",\"token\":\"EGW7ZcsAFSdFBH2qTSG3QyIyiKH0LL+5\",\"expiredTime\":\"2024-06-10 17:39:33.0571\"}"
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
                "token": "EGW7ZcsAFSdFBH2qTSG3QyIyiKH0LL+5",
                "chatRoomName": [
                    "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0",
                    "room_6747db05_4d30_430c_bec6_1c49f942a7c3"
                ]
            }

+ Response 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": "{\"room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0\":[{\"id\":1,\"sender\":\"指令 Command1\",\"message\":\"\",\"type\":\"space\",\"time\":\"\",\"modify\":false,\"deleted\":true},{\"id\":2,\"sender\":\"指令 Command1\",\"message\":\"測試訊息1\",\"type\":\"2024-05-30 01:10:15\",\"time\":\"\",\"modify\":false,\"deleted\":false},{\"id\":3,\"sender\":\"指令 Command1\",\"message\":\"測試訊息1\",\"type\":\"2024-05-30 01:10:15\",\"time\":\"\",\"modify\":false,\"deleted\":false},{\"id\":4,\"sender\":\"指令 Command1\",\"message\":\"測試訊息1\",\"type\":\"2024-05-30 01:10:15\",\"time\":\"\",\"modify\":false,\"deleted\":false},{\"id\":5,\"sender\":\"指令 Command1\",\"message\":\"測試訊息1\",\"type\":\"2024-05-30 01:10:17\",\"time\":\"\",\"modify\":false,\"deleted\":false}],\"room_6747db05_4d30_430c_bec6_1c49f942a7c3\":[]}"
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
                "token": "EGW7ZcsAFSdFBH2qTSG3QyIyiKH0LL+5",
                "chatRoomName": {
                    "room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0": 0,
                    "room_6747db05_4d30_430c_bec6_1c49f942a7c3": 1
                }
            }

+ Response 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": "{\"room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0\":\"[{\\\"id\\\":1,\\\"sender\\\":\\\"指令 Command1\\\",\\\"message\\\":\\\"\\\",\\\"type\\\":\\\"space\\\",\\\"time\\\":\\\"\\\",\\\"modify\\\":false,\\\"deleted\\\":true},{\\\"id\\\":2,\\\"sender\\\":\\\"指令 Command1\\\",\\\"message\\\":\\\"測試訊息1\\\",\\\"type\\\":\\\"2024-05-30 01:10:15\\\",\\\"time\\\":\\\"\\\",\\\"modify\\\":false,\\\"deleted\\\":false},{\\\"id\\\":3,\\\"sender\\\":\\\"指令 Command1\\\",\\\"message\\\":\\\"測試訊息1\\\",\\\"type\\\":\\\"2024-05-30 01:10:15\\\",\\\"time\\\":\\\"\\\",\\\"modify\\\":false,\\\"deleted\\\":false},{\\\"id\\\":4,\\\"sender\\\":\\\"指令 Command1\\\",\\\"message\\\":\\\"測試訊息1\\\",\\\"type\\\":\\\"2024-05-30 01:10:15\\\",\\\"time\\\":\\\"\\\",\\\"modify\\\":false,\\\"deleted\\\":false},{\\\"id\\\":5,\\\"sender\\\":\\\"指令 Command1\\\",\\\"message\\\":\\\"測試訊息1\\\",\\\"type\\\":\\\"2024-05-30 01:10:17\\\",\\\"time\\\":\\\"\\\",\\\"modify\\\":false,\\\"deleted\\\":false}]\",\"room_6747db05_4d30_430c_bec6_1c49f942a7c3\":\"[]\"}"
            }