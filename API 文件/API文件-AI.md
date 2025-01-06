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
### AI預測補全對話 [POST]
https://command1264.xserver.tw/api/v1/messageReplayAi

> All
+ 輸入 (application/json)

  + Body

        {
            "token": "m7Y2Nu6stueV9/7mg+xgrRpekf5tMCMh",
            "chatRoomName": "room_a84013c4_06a9_46f6_bdfa_272d408c9581",
            "messageCount": 10,
            "inputEntryText": "te"
        }


+ 回傳 200 (application/json)

    + Body

          {
              "success": true,
              "errorMessage": "",
              "exception": "",
              "data": {
                  "completionMessage": "st",
                  "possibleReply": [
                      "st",
                      "stest",
                      "s"
                  ]
              }
          }

> 必須(must)
+ 輸入 (application/json)

    + Body

          {
              "token": "m7Y2Nu6stueV9/7mg+xgrRpekf5tMCMh",
              "chatRoomName": "room_a84013c4_06a9_46f6_bdfa_272d408c9581"
          }


+ 回傳 200 (application/json)

    + Body

          {
              "success": true,
              "errorMessage": "",
              "exception": "",
              "data": {
                  "completionMessage": null,
                  "possibleReply": [
                      "你好！",
                      "什麼事?",
                      "好的，等一下"
                  ]
              }
          }
        
        
> noToken
+ 回傳 200 (application/json)
    + Body

            {
                "success": false,
                "errorMessage": "找不到Token",
                "exception": "",
                "data": ""
            }
        
> noChatRoomName
+ 回傳 200 (application/json)
    + Body

            {
                "success": false,
                "errorMessage": "找不到聊天室名稱",
                "exception": "",
                "data": ""
            }

> ollama cant return data (ollama 不能回傳資料)
+ 回傳 200 (application/json)
    + Body

            {
                "success": false,
                "errorMessage": "e.getMessage()",
                "exception": "",
                "data": ""
            }
---

#### [API文件](API%E6%96%87%E4%BB%B6.md)
#### [API文件-Account](API%E6%96%87%E4%BB%B6-Account.md)
#### [API文件-UserChatRoom](API%E6%96%87%E4%BB%B6-UserChatRoom.md)
#### [API文件-Message](API%E6%96%87%E4%BB%B6-Message.md)
#### [API文件-AI](API%E6%96%87%E4%BB%B6-AI.md)
#### [API文件-WebSocket(WIP)](API%E6%96%87%E4%BB%B6-WebSocket.md)