import{sv} from'./share.js';



//==========資料==========
const doms ={
    /**使用者名稱輸入框*/
    username:document.querySelector("#cr_name"),
    /**使用者名稱錯誤訊息*/
    username_text:document.querySelector("#cr_name_text"),
    /**email輸入框*/
    email:document.querySelector("#cr_email"),
    /**email錯誤訊息*/
    email_text:document.querySelector("#cr_email_text"),
    /**密碼輸入框*/
    pw:document.querySelector("#cr_pw"),
    /**密碼錯誤訊息*/
    pw_text:document.querySelector("#cr_pw_text"),
    /**密碼2輸入框*/
    pw2:document.querySelector("#cr_pw2"),
    /**密碼2錯誤訊息*/
    pw2_text:document.querySelector("#cr_pw2_text"),
    /**送出按鈕*/
    submit:document.querySelector("#submit"),
}



//==========輔助功能function==========
/**
 * 檢測註冊輸入資料是否符合規則
 * @returns 符合規則
 */
const submitOK=()=>{
    let ok=true;
    if(doms.username.value===""){
        doms.username_text.style.display="block";
        ok=false;
    }else{
        doms.username_text.style.display="none";
    }
    if(doms.email.value===""){
        doms.email_text.style.display="block";
        ok=false;
    }else{
        doms.email_text.style.display="none";
    }
    if(doms.pw.value===""){
        doms.pw_text.style.display="block";
        ok=false;
    }else{
        doms.pw_text.style.display="none";
    }
    if(doms.pw2.value===""){
        doms.pw2_text.innerHTML="Retype password尚未輸入";
        doms.pw2_text.style.display="block";
        ok=false;
    }else if(doms.pw2.value!==doms.pw.value){
        doms.pw2_text.innerHTML="輸入密碼不同";
        doms.pw2_text.style.display="block";
        ok=false;
    }
    else{
        doms.pw2_text.style.display="none";
    }
    return ok;
}



//==========網路連線function==========
/**
 * 註冊帳號
 */
const create_submit=async ()=>{
    const sendBody={
        name:doms.username.value,
        loginAccount:doms.email.value,
        loginPassword:doms.pw.value,
    }
    // console.log(sendBody);
    try {
        const response = await fetch(sv.ip+sv.createAccount,{
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

        // 響應頭
        // response.headers.forEach((value, name) => {
        //     console.log(`${name}: ${value}`);
        // });

        // 解析響應體
        const responseData = await response.json();

        // 響應體處理
        if(responseData.success){
            const userConfirmed = confirm("註冊成功跳轉至登入");
            if (userConfirmed){
                window.location.href =`login.html`;
            }else{
                window.location.href =`create.html`;
            }
                
        }else{
            window.alert(`${responseData.errorMessage}`);
        }

    } catch (error) {
        sv.urlToError(error)
    }
}



//==========事件監聽function==========
// 註冊按鈕
doms.submit.addEventListener("click",()=>{
    if (submitOK()){
        create_submit();
    }
});
document.addEventListener('keydown',event=>{
    if(submitOK() && event.key==='Enter'){
        create_submit()
    }
});


//==========window畫面載入==========
window.addEventListener('DOMContentLoaded', () => {
    sv.pingsv();
});
