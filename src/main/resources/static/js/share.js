export const sv={
    ip:'http://26.208.147.218:60922',
    loginAccount:'/api/v1/loginAccount',
    createAccount:'/api/v1/createAccount',
    createUserChatRoom:'/api/v1/createUserChatRoom',
    userSendMessage:'/api/v1/userSendMessage',
    getUserReceiveMessage:'/api/v1/getUserReceiveMessage',
    getUserChatRoom:'/api/v1/getUserChatRoom',
    getUserChatRoomChats:'/api/v1/getUserChatRoomChats',
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
        // window.location.href =`error.html?continue_url=${window.location.pathname.substring(1)}&error=${errorStr}`;
        throw new Error(`${errorStr}`);
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
        }
    },
    async verify(){
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
            window.location.href =`error.html?continue_url=${window.location.pathname.substring(1)}&error=${error}`
        }
    }
}

