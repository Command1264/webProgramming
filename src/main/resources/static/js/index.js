import{sv} from'./share.js';


//==========資料==========
const doms = {
    /**整個左半部使用者列表+功能*/
    obj_list:document.querySelector('#obj_list'),
    /**整個右半部聊天室*/
    obj_content:document.querySelector('#obj_content'),
    /**聊天室訊息列表*/
    msg:document.querySelector('#msg'),
    /**聊天文字輸入(全部)*/
    textin:document.querySelector('#textin'),
    /**聊天文字輸入(輸入框)*/
    msg_text:document.querySelector('#msg_text'),
    /**返回按鈕 退出聊天室*/
    back_btn:document.querySelector('#back_btn'),
    /**聊天室選擇列表*/
    obj_list_ct:document.querySelector('#obj_list_ct'),
    /**聊天室顯示名稱*/
    chatRoom_name:document.querySelector('#chatRoom_name'),
    /**房間名稱*/
    add_room_rn:document.querySelector('#add_room_rn'),
    /**功能列表(左上)*/
    menu:document.querySelector('#menu'),
    /**menu彈窗*/
    setting:document.querySelector('#setting'),
    /**menu內使用者名稱*/
    user_name:document.querySelector('#user_name'),
    /**新增聊天室彈窗*/
    add_room:document.querySelector('#add_room'),
    /**找尋使用者(輸入框)*/
    add_room_userSearch:document.querySelector('#add_room_userSearch'),
    /**使用者查詢結果列表*/
    add_room_users:document.querySelector('#add_room_users'),
    /**創建聊天室按鈕*/
    add_room_establish:document.querySelector('#add_room_establish'),
    /**取消聊天室按鈕*/
    add_room_cancel:document.querySelector('#add_room_cancel'),

}


//==========輔助功能function==========
/**
 * 
 * @param {number} insize 轉換用Rem
 * @returns pxSize
 */
const getRemSize = insize=>{
    // 元素計算樣式
    const htmlElement = document.documentElement;
    const fontSize = window.getComputedStyle(htmlElement).fontSize;
    const remSize = parseFloat(fontSize);
    return(remSize*insize);
}
/**
 * 
 * @returns 現在時間 
 */
const datatime_str = {
    /**
     * 
     * @param {object} Date()
     * @returns 秒
     */
    seconds:(now)=>
        String(now.getSeconds()).padStart(2, '0'),
    /**
     * 
     * @param {object} Date()
     * @returns 分
     */
    minutes:(now)=>
        String(now.getMinutes()).padStart(2, '0'),
    /**
     * 
     * @param {object} Date()
     * @returns 時
     */
    hours:(now)=>
        String(now.getHours()).padStart(2, '0'),
    /**
     * 
     * @param {object} Date()
     * @returns 日
     */
    date:(now)=>
        String(now.getDate()).padStart(2, '0'),
    /**
     * 
     * @param {object} Date()
     * @returns 月
     */
    mobth:(now)=>
        String(now.getMonth()+1).padStart(2, '0'),
    /**
     * 
     * @param {object} now 
     * @returns 年
     */
    year:(now)=>
        String(now.getFullYear()).padStart(4, '0'),
}
/**
 * 取得該畫面最新訊息id
 * @returns 訊息id
 */
const newMsgId=()=>{
    if(doms.msg.children.length!==0)
        return doms.msg.children[doms.msg.children.length-1].id.substring(4);
    else
        return '0';

}


//==========畫面function==========
/**
 * addRoom還原至初始值
*/
const addRoomReset = ()=>{
    doms.add_room_rn.value='';
    doms.add_room_userSearch.value='';
    doms.add_room_users.innerHTML='<p>查無結果<p/>'
}
/**
 * 視窗刷新
 */
const winRefresh = ()=>{
    if(window.innerWidth>getRemSize(50)){
        if(window.location.hash!==''){
            doms.obj_list.style.display='block';
            doms.obj_content.style.display='flex';
            doms.obj_content.style.visibility='visible';
        }else{
            doms.obj_list.style.display='block';
            doms.obj_content.style.display='flex';
            doms.obj_content.style.visibility='hidden';

        }
    }
    if(window.innerWidth<=getRemSize(50)){
        if(window.location.hash!==''){
            doms.obj_list.style.display='none';
            doms.obj_content.style.display='flex';
        }else{
            doms.obj_list.style.display='block';
            doms.obj_content.style.display='none';
        }
        doms.obj_content.style.visibility='visible';
    }
}
/**
 * 畫面訊息顯示
 * @param {string} my 自己名稱
 * @param {string} sender 發送者名稱
 * @param {string} cont 發送內容
 * @param {string} time 發送時間
 * @param {string} id 訊息id
 * @param {string} state 訊息狀態
 */
const addMsg = (my,sender,cont,time,id,state='')=>{
    const msgBubble = document.createElement('div');
    const msgCt = document.createElement('span');
    const msgSt = document.createElement('span');
    msgCt.innerText = cont;
    msgSt.innerText = `${state} ${time}`
    msgSt.classList.add(sender===my?'my_msg_state':'you_msg_state');
    msgBubble.classList.add(sender===my?'my_mes':'you_mes');
    msgBubble.id=`msg-${id}`;
    msgBubble.appendChild(msgCt);
    msgBubble.appendChild(msgSt);
    doms.msg.appendChild(msgBubble);
    to_bottom();
    
}
/**
 * 聊天室移動至最下面
 */
const to_bottom = ()=>{
    doms.msg.scroll({
        top:doms.msg.scrollHeight,
        behavior: 'smooth'
    })//跳轉至最底部
}


//==========網路連線function==========

/**
 * 聊天室更新訊息
 * @param {object} chat_room
 * @param {string} chat_room.room_name
 * @param {number} chat_room.chatnum 
 */
const refreshMsg = async(...chat_room)=>{
    const sendBody={
        token:localStorage.getItem('token'),
        chatRoomName : {},
    }
    for (let i of [...chat_room]){
        if(i.room_name!="")
            sendBody.chatRoomName[i.room_name]=i.chatnum;
        
    }
    
    
    try {
        const response = await fetch(sv.ip+sv.getUserReceiveMessage,{ 
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(sendBody),
        });
        
        // 檢查請求是否成功
        if (!response.ok) {
            sv.urlToError(`${response.status}`);
        }

        // 解析響應體
        const responseData = await response.json();

        // 標記響應體
        if(responseData.success){
            return responseData.data
        }

    } catch{
    // } catch (error){
        // sv.urlToError(error);
        setTimeout(()=>{
            refreshMsg(...chat_room);
        },100)
        
    }
}
/**
 * 創建聊天室
 * @param {string} 聊天室名稱
 * @param  {...string} ...使用者名稱
 */
const crChatroom = async (room_name,...user_name)=>{
    const sendBody={
        token:localStorage.getItem('token'),
        name:room_name,
        userIds : [
            localStorage.getItem('userId'),
            ...user_name
        ]
    }
    try {
        const response = await fetch(sv.ip+sv.createUserChatRoom,{
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(sendBody),
        });
        
        // 檢查請求是否成功
        if (!response.ok) {
            sv.urlToError(`${response.status}`);
        }

        // 解析響應體
        const responseData = await response.json();

        // 標記響應體
        if(responseData.success){
            window.location.href =`#${responseData.data}`;
        }else{
            window.alert(`創建失敗請重試\n${responseData.errorMessage}`);
        }

    } catch (error) {
        sv.urlToError(error);
    }
    doms.add_room.querySelector('#add_room_rn').value='';
    doms.add_room.querySelector('#add_room_userSearch').value='';
    // ============
}

/**
 * token登入刷新
 */
const tokenChange = async ()=>{
    // 避免正在載入時token被換掉
    setTimeout(async()=>{
        const sendBody={
            token:localStorage.getItem('token'),
        }
        try {
            const response = await fetch(sv.ip+sv.changeToken,{
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(sendBody),
            });
            // 檢查請求是否成功
            if (!response.ok) {
                sv.urlToError(`${response.status}`);
            }
            // 解析響應體
            const responseData = await response.json();
            // 標記響應體
            if(responseData.success){
                localStorage.setItem('token',responseData.data.token);
            }
        } catch (error) {
            sv.urlToError(error);
        }
    },100);
}
/**
 * token登入
 */
const tokenlogin = async ()=>{
    const sendBody={
        token:localStorage.getItem('token'),
    }
    try {
        const response = await fetch(sv.ip+sv.loginAccount,{
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(sendBody),
        });
        
        // 檢查請求是否成功
        if (!response.ok) {
            sv.urlToError(`${response.status}`);
        }

        // 解析響應體
        const responseData = await response.json();

        // 標記響應體
        if(responseData.success){
            localStorage.setItem('userId',responseData.data.userId);
            // localStorage.setItem('name',responseData.data.name);
            // localStorage.setItem('createTime',responseData.data.createTime);
            // localStorage.setItem('photo',responseData.data.photoStickerBase64);
            // localStorage.setItem('chatRooms',JSON.stringify(responseData.data.chatRooms.map(obj => Object.keys(obj)[0])));
            // console.log(Object.keys(responseData.data.chatRooms));
            return {
                userId:responseData.data.userId,
                name:responseData.data.name,
                createTime:responseData.data.createTime,
                photo:responseData.data.photoStickerBase64,
                // chatRooms:responseData.data.chatRooms.map(obj => Object.keys(obj)),
                chatRooms:Object.keys(responseData.data.chatRooms),
            }

            
        }else{
            sv.urlToLogin();
        }
    } catch (error) {
        sv.urlToError(error);
    }
}
/**
 * 
 * 畫面載入聊天室訊息
 */
const lodMsg = async ()=>{
    if(window.location.hash){
        doms.msg.innerHTML='';
        
        //載入聊天室名稱
        doms.chatRoom_name.innerText=doms.obj_list_ct.querySelector(`#room_${window.location.hash.substring(6).replace(/_/g,'-')}`).innerText;
        
        const sendBody={
            token:localStorage.getItem('token'),
            chatRoomName:window.location.hash.substring(1),
        }
        try {
            const response = await fetch(sv.ip+sv.getUserChatRoomChats,{
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(sendBody),
            });
            
            // 檢查請求是否成功
            if (!response.ok) {
                sv.urlToError(`${response.status}`);
            }
            // 解析響應體
            const responseData = await response.json();
            // 標記響應體
            if(responseData.success){
                // 載入聊天內容
                responseData.data[window.location.hash.substring(1)].forEach(item=>{
                    
                    if(item.deleted){
                        addMsg(localStorage.getItem('userId'),item.senderId,item.message,item.time,String(item.id),'已移除');
                    }else if(item.type==='text'){
                        if(item.modify){
                            addMsg(localStorage.getItem('userId'),item.senderId,item.message,item.time,String(item.id),'已編輯');
                        }else{
                            addMsg(localStorage.getItem('userId'),item.senderId,item.message,item.time,String(item.id),'');
                        }
                    }
                });
            }else{
                // console.log('login');
                // console.log(responseData.errorMessage);
                // console.log(sendBody.chatRoomName);
                if(responseData.errorMessage==='沒有權限'){
                    // window.alert(`沒有存取 ${sendBody.chatRoomName} 的權限`);
                    window.location.hash='';
                }
                // sv.urlToLogin();
            }
    
        } catch (error) {
            sv.urlToError(error);
        }
    }
}
/**
 * 取得聊天室資訊
 * @param  {string array} RoomName 聊天室名稱
 * @returns 聊天室資訊物件
 */
const chatRoom_ct = async (RoomName)=>{
    const sendBody={
        token:localStorage.getItem('token'),
        chatRoomName:RoomName,
    }
    try {
        const response = await fetch(sv.ip+sv.getUserChatRoom,{
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(sendBody),
        });
        
        // 檢查請求是否成功
        if (!response.ok) {
            sv.urlToError(`${response.status}`);
        }
        // 解析響應體
        const responseData = await response.json();
        // 標記響應體
        if(responseData.success){
            const rearr =responseData.data;
            // for(let item of Object.keys(responseData.data)){
            //     rearr.push(responseData.data[item]);
                
            // }
            return rearr;
        }
    } catch (error) {
        sv.urlToError(error);
    }
}
/**
 * 
 * 訊息從訊息框送出
 */
const sendMsg = async ()=>{
    if(doms.msg_text.value!=''){
        const now = new Date();
        const fu_now_time=`${datatime_str.year(now)}-${datatime_str.mobth(now)}-${datatime_str.date(now)} ${datatime_str.hours(now)}:${datatime_str.minutes(now)}:${datatime_str.seconds(now)}`;
        const sendMsgid=`${parseInt(newMsgId())+1}`;
        addMsg(localStorage.getItem('userId'),localStorage.getItem('userId'),doms.msg_text.value,fu_now_time,sendMsgid,'傳送中...');
        const sendBody={
        token:localStorage.getItem('token'),
        chatRoomName:window.location.hash.substring(1),
        message:{
            sender : localStorage.getItem('userId'),
            message : doms.msg_text.value,
            type : "text",
            time : fu_now_time
        }
        }
        try {
            const response = await fetch(sv.ip+sv.userSendMessage,{
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(sendBody),
            });
            
            // 檢查請求是否成功
            if (!response.ok) {
                sv.urlToError(`${response.status}`);
            }
            // 解析響應體
            const responseData = await response.json();
            // 標記響應體
            if(responseData.success){
                doms.msg.querySelector(`#msg-${sendMsgid}`).children[1].innerText=` ${fu_now_time}`;
            }else{
                sv.urlToLogin();
            }
        } catch (error) {
            sv.urlToError(error);
        }
        doms.msg_text.value='';
    }
}
/**
 * 查尋使用者
 * @param {String} searchStr 搜尋內容(使用者)
 * @returns 使用者資訊物件
 */
const searchAllUser = async (searchStr)=>{
    const sendBody={
        token:localStorage.getItem('token'),
        name:searchStr,
    }
    try {
        const response = await fetch(sv.ip+sv.getContainsUser,{
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(sendBody),
        });
        // 檢查請求是否成功
        if (!response.ok) {
            sv.urlToError(`${response.status}`);
        }
        // 解析響應體
        const responseData = await response.json();
        // 標記響應體
        if(responseData.success){
            return responseData.data;
        }
    } catch (error) {
        sv.urlToError(error);
    }
}



//==========事件監聽function==========

// 顯示'創建聊天室'彈窗
doms.menu.children[1].addEventListener('click',()=>{
    addRoomReset();
    doms.add_room.showModal();
});
// '創建聊天室'取消按紐
doms.add_room_cancel.addEventListener('click',()=>{
    addRoomReset();
    doms.add_room.close();
});
// 按下'設定'按鈕
doms.menu.children[0].addEventListener('click',()=>{
    doms.setting.showModal();
});
// 按下'創建聊天室'創建按紐
doms.add_room_establish.addEventListener('click',()=>{
    const textbox_value=doms.add_room_rn.value;
    const userls=[];
    if(textbox_value!==''){
        doms.add_room_users.querySelectorAll(`.choose`).forEach(item=>{
            userls.push(item.children[1].innerText);
        });
        crChatroom(doms.add_room_rn.value,...userls);
        addRoomReset();
        doms.add_room.close();
    }else{
        window.alert('尚未輸入房間名稱')
    }
});
// '設定'按下彈窗外面關閉
doms.setting.addEventListener('click',event=>{
    const rect = event.target.getBoundingClientRect();
    const isInDialog = (event.clientX >= rect.left && event.clientX <= rect.right && event.clientY >= rect.top && event.clientY <= rect.bottom);

    if (!isInDialog) {
        doms.setting.close();
    }
});
// '創建聊天室'按下彈窗外面關閉
doms.add_room.addEventListener('click',event=>{
    const rect = event.target.getBoundingClientRect();
    const isInDialog = (event.clientX >= rect.left && event.clientX <= rect.right && event.clientY >= rect.top && event.clientY <= rect.bottom);

    if (!isInDialog) {
        addRoomReset();
        doms.add_room.close();
    }
});
// '創建聊天室'找尋使用者
doms.add_room_userSearch.addEventListener('input',async ()=>{
    const showUsers = await searchAllUser(doms.add_room_userSearch.value);
    const serls = document.createElement('div');
    serls.innerHTML='';
    doms.add_room_users.querySelectorAll('.choose').forEach(item=>{
        serls.appendChild(item);
    })
    showUsers.forEach(item=>{
        if(!serls.querySelector(`#add_room_serli_${item.userId}`)){
            const serUserObj = document.createElement('div');
            const serUserObj_name = document.createElement('span');
            const serUserObj_id = document.createElement('span');
            serUserObj_name.innerText=`${item.name}`;
            serUserObj_id.innerText=`${item.userId}`;
            serUserObj.appendChild(serUserObj_name);
            serUserObj.appendChild(serUserObj_id);
            serUserObj.id=`add_room_serli_${item.userId}`
            // document.querySelector(`#add_room_serli_${item.userId}`).addEventListener('click',()=>{
            
            serls.appendChild(serUserObj);
        }
    });
    if(serls.innerHTML!==doms.add_room_users.innerHTML){
        doms.add_room_users.innerHTML=serls.innerHTML;
        if(doms.add_room_users.innerHTML===''){
            doms.add_room_users.innerHTML='<p>查無結果<p/>';
        }else{
            for (let key=0;key<doms.add_room_users.children.length;key++) {
                // if()
                doms.add_room_users.children[key].addEventListener('click',()=>{
                    if(doms.add_room_users.children[key].classList.contains('choose')){
                        doms.add_room_users.children[key].classList.remove('choose');
                    }else{
                        doms.add_room_users.children[key].classList.add('choose');
                    }
                    // console.log(doms.add_room_users.children[key].classList.contains);
                });
                // doms.add_room_users.children[key];
                // console.log(key);
            }
        }
    }

});

// 訊息傳送(按鈕)
doms.textin.querySelector('input[type="button"]').addEventListener('click',()=>{
    sendMsg();
});
// 訊息傳送(Enter)
doms.textin.addEventListener('keydown',event=>{
    if(event.key=== 'Enter'){
        sendMsg();
    }
});
// 返回BTN
doms.back_btn.addEventListener('click',()=>{
    window.location.hash='';
});
// Esc返回
document.addEventListener('keydown', event=>{
    if (event.key === 'Escape') {
        window.location.hash='';
    }
});
// 點選聊天室切換並刷新畫面
doms.obj_list_ct.addEventListener('click',()=>{
    winRefresh();
});
// 聊天室切換(hash切換)
window.addEventListener('hashchange', ()=>{
    lodMsg();
    winRefresh();
});
//視窗size變更
window.addEventListener('resize', winRefresh);

//==========遞迴型function==========

/**
 * 畫面重複刷新至最新訊息
 */
const refreshMsg_ct = async()=>{ 
    const newmsg_li = await refreshMsg({
        room_name:window.location.hash.substring(1),
        chatnum:Number(newMsgId()),
    });
    if(newmsg_li){
        Object.keys(newmsg_li).forEach(newmsg_id=>{
            newmsg_li[newmsg_id].forEach(item=>{
                if(!doms.msg.querySelector(`#msg-${item.id}`)){
                    if(item.deleted)
                        addMsg(localStorage.getItem('userId'),item.senderId,item.message,item.time,String(item.id),'已移除');
                    else if(item.modify)
                        addMsg(localStorage.getItem('userId'),item.senderId,item.message,item.time,String(item.id),'已編輯');
                    else
                        addMsg(localStorage.getItem('userId'),item.senderId,item.message,item.time,String(item.id),'');
                }
                    
            });
        });
    }
    winRefresh();
    // console.log(newmsg_li);
    setTimeout(()=>{
        refreshMsg_ct();
    },100)
    
}
/**
 * 畫面重複刷新至最新聊天室順序
 */
const refreshChatroom = async()=>{
    const temp_obj_list_ct=document.createElement('div');
    temp_obj_list_ct.innerHTML='';
    tokenlogin().then(async userDt=>{
        const chat_room_object=await chatRoom_ct(userDt.chatRooms);
        if(chat_room_object){
            Object.entries(chat_room_object).forEach(async ([key,item])=>{
                const ctName = document.createElement('a');
                ctName.href=`#${key}`;
                ctName.id=`room_${item.uuid}`;
                ctName.innerText=item.name;
                temp_obj_list_ct.appendChild(ctName);
            });
        }
    }).then(()=>{
        if(temp_obj_list_ct.innerHTML!==doms.obj_list_ct.innerHTML){
            doms.obj_list_ct.innerHTML=temp_obj_list_ct.innerHTML;
        }
        setTimeout(()=>{
            refreshChatroom();
        },200)
    });
    
    
}
/**
 * 載入聊天室名稱
 */
const lodchatRoomName = ()=>{
    try {
        doms.chatRoom_name.innerText=doms.obj_list_ct.querySelector(`#room_${window.location.hash.substring(6).replace(/_/g,'-')}`).innerText;
    } catch {
        setTimeout(()=>{
            lodchatRoomName();
        },200)
    }
        
}
//==========window畫面載入==========
window.addEventListener('DOMContentLoaded',() => {
    tokenlogin().then(async userDt=>{
        const setneme = document.createElement('li');
        doms.setting.innerHTML='';
        setneme.id='user_neme';
        setneme.innerText=`Name:${userDt.name}`;
        doms.setting.appendChild(setneme);
        const setid = document.createElement('li');
        setid.innerText=`ID:${userDt.userId}`;
        doms.setting.appendChild(setid);
        const setCrtime = document.createElement('li');
        setCrtime.innerText=`創建時間:${userDt.createTime}`;
        doms.setting.appendChild(setCrtime);
    });
    refreshMsg_ct();
    refreshChatroom();
    lodchatRoomName();
    
    tokenChange();
    
});

