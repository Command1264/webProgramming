export const sv={
    /**server IP*/
    ip:'http://26.208.147.218:60922',
    /**登入 API*/
    loginAccount:'/api/v1/loginAccount',
    /**創建帳號 API*/
    createAccount:'/api/v1/createAccount',
    /**創建聊天室 API*/
    createUserChatRoom:'/api/v1/createUserChatRoom',
    /**傳送訊息 API*/
    userSendMessage:'/api/v1/userSendMessage',
    /**取得更新訊息 API*/
    getUserReceiveMessage:'/api/v1/getUserReceiveMessage',
    /**取得聯天室資訊 API*/
    getUserChatRoom:'/api/v1/getUserChatRoom',
    /**取得聯天室內容 API*/
    getUserChatRoomChats:'/api/v1/getUserChatRoomChats',
    /**更換token API*/
    changeToken:'/api/v1/changeToken',
    /**搜尋使用者 API*/
    getContainsUser:'/api/v1/getContainsUser',
    
    /**ping API*/
    ping:'/ping',
    /**
     * 跳轉Login
     */
    urlToLogin(){
        window.location.href ='login.html';
        // throw new Error(`跳轉login`);
    },
    /**
     * 跳轉error
     * @param {string} errorStr 
     */
    urlToError(errorStr){
        window.location.href =`error.html?continue_url=${window.location.pathname.substring(1)}&error=${errorStr}`;
        // throw new Error(`${errorStr}`);
    },
    /**
     * ping server
     */
    async pingsv(){
        try {
            const response = await fetch(this.ip+this.ping,{
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            })
            
            // 檢查請求是否成功
            if (!response.ok) {
                throw new Error(`${response.status}`);
            }
            // 解析響應體
            // const responseData = await response.json();
        } catch (error) {
            this.urlToError(error);
            // this.urlToError(`server未連接\n${error}`);
        }
    },
}

