window.app = window.app || {};
// 實際上線的app位址
window.app.host = 'http://192.168.2.111:8080/FuBang/';
// FB分享的網站連結
window.app.indexhtml = 'www.yahoo.com.tw';
// FB的應用程式ID
window.app.fbappid = '280619272145943';
// FB的channel. 很像不需要設定, 對程式沒影響
window.app.fbchannel = 'http://localhost:9000/test/';
// 圖片上傳自動縮放大小
window.app.imageSize = {x:470, y:299};
// 是否忽略FB的流呈. 測試用
window.app.ignoreFB = false;
// 不需要調整
window.app.sqlType = 'mysql';
window.app.local = false;

// 程式訊息
window.app.info = {};
window.app.info.pleaseUploadImage = '請您上傳一張圖片';
window.app.info.dataUploadError = '資料傳輸失敗';
window.app.info.imageUploadError = '圖片上傳失敗，請再試一次';
window.app.info.pleaseCheckNeeded = '請同意活動辦法';
window.app.info.noName = '請輸入名字';
window.app.info.noGender = '請勾選性別';
window.app.info.noPhonenumber = '請輸入電話號碼';
window.app.info.noEmail = '請輸入正確信箱';
window.app.info.searchFBInputName = 'FB名稱搜尋';
window.app.info.pleaseInputSearchWord = '請輸入搜尋名稱';
window.app.info.alreadyVote = '你已經投票了';
window.app.info.voteOk = '投票成功';
window.app.info.pleaseWaitForFacebook = '請等待網頁回應';
window.app.info.shareFBSuccess = '分享FB成功';
window.app.info.waitForImageUpload = '請等待圖片上傳';
window.app.info.waitForVote = '投票中，請等待';
window.app.info.over300Word = '超過三百個字了哦';
window.app.info.belowIE10 = '請使用IE10以上的版本上傳圖片';
window.app.info.appCrash = '資料錯誤，請重新整理網頁';
window.app.info.defaultName = '同身份證';

//FB分享文案
window.app.info.shareFB = {
	caption : '永保安康保富邦My Car, My Life !',
	description : '參加愛車照片上傳活動，除了可以集氣拿卡西歐自拍神器，還有威秀電影票可以抽唷!!!',
	link : window.app.host, 
	picture : window.app.host + 'web/images/100x100.jpg'
};
	