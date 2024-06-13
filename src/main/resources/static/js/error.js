//==========資料==========
const error_text = document.querySelector('#error_n')
const ct = document.querySelector('#ct')



//==========window畫面載入==========
window.onload = () => {
    const params = new URLSearchParams(window.location.search);
    const error_n = params.get('error');
    const error_ms = params.get('errorms');
    const continue_url = params.get('continue');

    if(error_n!==null){
        error_text.innerText = `${error_n}`;
        if(error_ms!==null){
            error_text.innerText+=`\n${error_ms}`;
        }
    }else{
        error_text.innerText = `沒錯誤你來這幹嘛?0.0`;
    }

    if(continue_url!==null){
        ct.innerHTML=`<a href="${continue_url}">回到原頁面</a>`;
    }else{
        ct.innerHTML=`<a href="login.html">回到原頁面</a>`;
    }
    
};