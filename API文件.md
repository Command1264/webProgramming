<!-- FORMAT: 1A
HOST: https://polls.apiblueprint.org/ -->

# 指令Command1 
## 網路程式設計 API文件
##### version: 1.0.0
##### edit time: 2024/06/07 02:04:30 AM


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
        
        
+ Response 200 (application/json)
noName
    + Body

            {
                "success": false,
                "errorMessage": "name沒有資料",
                "exception": "",
                "data": ""
            }
        
+ Response 200 (application/json)
noLoginAccount
    + Body

            {
                "success": false,
                "errorMessage": "loginAccount沒有資料",
                "exception": "",
                "data": ""
            }
        
+ Response 200 (application/json)
noLoginPassword
    + Body

            {
                "success": false,
                "errorMessage": "loginPassword沒有資料",
                "exception": "",
                "data": ""
            }

+ Response 200 (application/json)
loginAccountHasFound
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

+ Request (application/json)
onlyToken
    + Body

            {
                "token": "mjO/xaqZxZEmZXYYCrib2FEhris6M71o"
            }

+ Request (application/json)
onlyLogin
    + Body

            {
                "loginAccount":"command1264@gmail.com",
                "loginPassword": "25d55ad283aa400af464c76d713c07ad"
            }

+ Request (application/json)
Both
    + Body

            {
                "token": "mjO/xaqZxZEmZXYYCrib2FEhris6M71o",
                "loginAccount":"command1264@gmail.com",
                "loginPassword": "25d55ad283aa400af464c76d713c07ad"
            }

+ Response 200 (application/json)
onlyToken
    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": "{\"loginAccount\":\"command1264@gmail.com\",\"loginPassword\":\"25d55ad283aa400af464c76d713c07ad\",\"id\":\"1\",\"name\":\"指令 Command1\",\"createTime\":\"2004-01-06 20:07:09.2200\",\"chatRooms\":[{\"room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0\":0},{\"room_a84013c4_06a9_46f6_bdfa_272d408c9581\":0},{\"room_431c1373_2b76_4176_8962_b8419ffa6112\":0}],\"photoStickerBase64\":\"\"}"
            }

+ Response 200 (application/json)
onlyLogin
    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": "{\"account\":\"{\\\"loginAccount\\\":\\\"command1264@gmail.com\\\",\\\"loginPassword\\\":\\\"25d55ad283aa400af464c76d713c07ad\\\",\\\"id\\\":\\\"1\\\",\\\"name\\\":\\\"指令 Command1\\\",\\\"createTime\\\":\\\"2004-01-06 20:07:09.2200\\\",\\\"chatRooms\\\":[{\\\"room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0\\\":0},{\\\"room_a84013c4_06a9_46f6_bdfa_272d408c9581\\\":0},{\\\"room_431c1373_2b76_4176_8962_b8419ffa6112\\\":0}],\\\"photoStickerBase64\\\":\\\"\\\"}\",\"token\":\"{\\\"id\\\":\\\"1\\\",\\\"token\\\":\\\"mjO/xaqZxZEmZXYYCrib2FEhris6M71o\\\",\\\"expiredTime\\\":\\\"2024-06-07 02:39:11.7806\\\"}\"}"
            }

+ Response 200 (application/json)
Both
    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": "{\"loginAccount\":\"command1264@gmail.com\",\"loginPassword\":\"25d55ad283aa400af464c76d713c07ad\",\"id\":\"1\",\"name\":\"指令 Command1\",\"createTime\":\"2004-01-06 20:07:09.2200\",\"chatRooms\":[{\"room_e9ac0054_fd27_44a8_b2ca_eb704aebf8c0\":0},{\"room_a84013c4_06a9_46f6_bdfa_272d408c9581\":0},{\"room_431c1373_2b76_4176_8962_b8419ffa6112\":0}],\"photoStickerBase64\":\"\"}"
            }
---
### 創建聊天室 [POST]
http://26.208.147.218:60922/api/v1/createUserChatRoom

+ Request (application/json)
userIds
    + Body

            {
                "userIds" : [
                    "Command1",
                    "T9NSiwItgj",
                    "5qXJ5yMjQW"
                ]
            }

+ Request (application/json)
ids (Number/String)
    + Body

            {
                "ids" : [
                    1,
                    2, 
                    4
                ]
            }
+ Response 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": "room_6747db05_4d30_430c_bec6_1c49f942a7c3"
            }

---
### 取得聊天室資料 [GET]
http://26.208.147.218:60922/api/v1/getUsersChatRoomChat

+ Request (application/json)
userIds
    + Body

            {
                "userIds" : [
                    "Command1",
                    "T9NSiwItgj",
                    "5qXJ5yMjQW"
                ]
            }

+ Request (application/json)
ids (Number/String)
    + Body

            {
                "ids" : [
                    1,
                    2, 
                    4
                ]
            }
+ Response 200 (application/json)

    + Body

            {
                "success": true,
                "errorMessage": "",
                "exception": "",
                "data": "[{\"id\":1,\"sender\":\"1\",\"message\":\"測試訊息1\",\"type\":\"2024-05-30 01:10:15\",\"time\":\"\",\"modify\":false},{\"id\":2,\"sender\":\"1\",\"message\":\"測試訊息1\",\"type\":\"2024-05-30 01:10:15\",\"time\":\"\",\"modify\":false},{\"id\":3,\"sender\":\"1\",\"message\":\"測試訊息1\",\"type\":\"2024-05-30 01:10:15\",\"time\":\"\",\"modify\":false}]"
            }
