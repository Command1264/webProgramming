# 指令Command1
## 網路程式設計 API文件
##### version: 24w26d
##### last edit time: 2024/06/27 01:19:45 AM

#### [API文件](API%E6%96%87%E4%BB%B6.md)
#### [API文件-Account](API%E6%96%87%E4%BB%B6-Account.md)
#### [API文件-UserChatRoom](API%E6%96%87%E4%BB%B6-UserChatRoom.md)
#### [API文件-Message](API%E6%96%87%E4%BB%B6-Message.md)
#### [API文件-WebSocket](API%E6%96%87%E4%BB%B6-WebSocket.md)
---
### 創建帳戶 [POST]
https://command1264.xserver.tw/api/v1/createAccount

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
https://command1264.xserver.tw/api/v1/loginAccount

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
https://command1264.xserver.tw/api/v1/changeToken
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
### 搜尋使用者(Contains) [POST]
https://command1264.xserver.tw/api/v1/getContainsUser

+ 輸入 (application/json)

    + Body

            {
                "token": "iBm13ZuwQAysdlo7lTWCFqzI5RORaqvr",
                "name": "P"
            }

+ 回傳 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": [
                    {
                        "id": "",
                        "userId": "Taiwan_PingLord",
                        "name": "台灣Ping霸主",
                        "createTime": "2007-09-22 20:04:01",
                        "photoStickerBase64": ""
                    },
                    {
                        "id": "",
                        "userId": "Taiwan_PingLord2",
                        "name": "台灣Ping霸主",
                        "createTime": "2024-06-04 01:18:15",
                        "photoStickerBase64": ""
                    },
                    {
                        "id": "",
                        "userId": "Taiwan_PingLord3",
                        "name": "台灣Ping霸主",
                        "createTime": "2024-06-04 01:54:14",
                        "photoStickerBase64": ""
                    },
                    {
                        "id": "",
                        "userId": "CnPQH7yNpT",
                        "name": "xiang",
                        "createTime": "2024-06-07 16:04:31",
                        "photoStickerBase64": ""
                    },
                    {
                        "id": "",
                        "userId": "RiVnhxq1Ep",
                        "name": "wow5",
                        "createTime": "2024-06-09 16:03:10",
                        "photoStickerBase64": ""
                    },
                    {
                        "id": "",
                        "userId": "rzgKWFPkFQ",
                        "name": "我是誰我是誰我是誰",
                        "createTime": "2024-06-10 01:49:10",
                        "photoStickerBase64": ""
                    }
                ]
            }

---
### 刪除使用者 [POST]
https://command1264.xserver.tw/api/v1/deleteAccount
+ 輸入 (application/json)
    + Body

            {
                "token" : "lFv/2CQUHuZXLkoOZt9iw2Yy6DgkUjH9",
                "userId" : "2g5vT5miYn"
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
#### [API文件](API%E6%96%87%E4%BB%B6.md)
#### [API文件-Account](API%E6%96%87%E4%BB%B6-Account.md)
#### [API文件-UserChatRoom](API%E6%96%87%E4%BB%B6-UserChatRoom.md)
#### [API文件-Message](API%E6%96%87%E4%BB%B6-Message.md)
#### [API文件-WebSocket](API%E6%96%87%E4%BB%B6-WebSocket.md)