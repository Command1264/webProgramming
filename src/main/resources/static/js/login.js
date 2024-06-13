import{sv} from'./share.js';



//==========資料==========
const doms ={
    email:document.querySelector('#log_name'),
    email_text:document.querySelector('#log_name_text'),
    pw:document.querySelector('#log_pw'),
    pw_text:document.querySelector('#log_pw_text'),
    submit:document.querySelector('#submit'),
}



//==========輔助功能function==========

/**
 * 
 * @returns 帳密輸入框是否為空
 */
const checknull=()=>{
    let ok=true;
    if(doms.email.value===''){
        ok=false;
        doms.email_text.style.display='block';
    }else{
        doms.email_text.style.display='none';
    }
    if(doms.pw.value===''){
        ok=false;
        doms.pw_text.style.display='block';
    }else{
        doms.pw_text.style.display='none';
    }
    return ok
}

//==========網路連線function==========

/**
 * 帳密登入
 */
const login_submit=async ()=>{
    const sendBody={
        loginAccount:doms.email.value,
        loginPassword:doms.pw.value,
    }
    try {
        const response = await fetch(sv.ip+sv.loginAccount,{
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(sendBody)
        })
        
        // 檢查請求是否成功
        if (!response.ok) {
            throw new Error(`${response.status}`);
        }

        // 解析響應體
        const responseData = await response.json();

        // 標記響應體
        if(responseData.success){
            localStorage.setItem('token',responseData.data.token)
            window.location.href ='index.html';
        }else{
            window.alert(`${responseData.errorMessage}`);
        }

    } catch (error) {
        sv.urlToError(error);
    }
}



//==========事件監聽function==========
// 登入按鈕
doms.submit.addEventListener('click',()=>{
    if(checknull()){
        login_submit()
    }
})



//==========window畫面載入==========
window.addEventListener('DOMContentLoaded', () => {
    sv.pingsv();
});
