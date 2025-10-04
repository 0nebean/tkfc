varcurLan="ar";
varcountry;
varcurrency="USD";
varcurrencyDisplay="$";
vareuv;
varsubmitData;
varsubmiting=false;

varvoucherAmt;
varvoucherCode;


functiongetLocation(){
if(navigator.geolocation){

navigator.geolocation.getCurrentPosition(function(position){
letlatitude=position.coords.latitude;
letlongitude=position.coords.longitude;
$("body").data("latlon",latitude+","+longitude);
report("latlon-ok");
},function(err){
report("latlon-err");
});
}else{
report("latlon-no");
}
}



functiongetQueryVariable(variable){
varquery=window.location.search.substring(1);
varvars=query.split("&");
for(vari=0;i<vars.length;i++){
varpair=vars[i].split("=");
if(pair[0]==variable){returnpair[1];}
}
returnnull;
}


functionpushEventFbGG(fbEvent,ttEvent,snapEvent,twiterEvent,ggEvent,append){

setTimeout(function(){
letfbData={content_name:$(".productName-big").text(),
content_category:"detail",
content_ids:[$("body").data("id")],
content_type:"product",
value:$("body").data("priceusd"),
description:$(".address-header-center-product").text(),
quantity:1,
currency:"USD"};
if(append){
fbData=Object.assign(fbData,append);
}
letms=getQueryVariable("ms");
if(!ms||ms=="fb"){
fbq("track",fbEvent,fbData);
}

if(ms&&ms=="tt"){
letttData=fbData;
ttData['content_type']="product";
ttData['content_id']=$("body").data("id");
ttData['contents']=ttData.content_ids;
ttData['price']=$("body").data("priceusd");
if(append){
ttData=Object.assign(ttData,append);
}
ttq.track(ttEvent,ttData);
}

letsnapData={
price:$("body").data("priceusd"),
currency:"USD",
item_ids:[$("body").data("id")],
item_category:'product',
description:$(".address-header-center-product").text(),
number_items:1
};
if(append){
snapData=Object.assign(snapData,append);
}
snaptr('track',snapEvent,snapData);

if(ms&&ms=="twiter"){
lettwData={
value:$("body").data("priceusd"),
currency:'USD',
num_items:1,
};
twq('track',twiterEvent,twData);
}



gtag("event",ggEvent,fbData);
},10);
}

/*
*url目标url
*arg需要替换的参数名称
*arg_val替换后的参数的值
*returnurl参数替换后的url
*/
functionchangeURLArg(url,arg,arg_val){
varpattern=arg+'=([^&]*)';
varreplaceText=arg+'='+arg_val;
if(url.match(pattern)){
vartmp='/('+arg+'=)([^&]*)/gi';
tmp=url.replace(eval(tmp),replaceText);
returntmp;
}else{
if(url.match('[\?]')){
returnurl+'&'+replaceText;
}else{
returnurl+'?'+replaceText;
}
}
returnurl+'\n'+arg+'\n'+arg_val;
}


functionalertMsg(context){
lettitle=LANG_GMT[curLan].alert_title;
letbtn=LANG_GMT[curLan].alert_btn;
layer.alert(title,{btn:[btn],content:context});
}


functionshowCurrencyDialog(){
layer.open({
type:1,
area:["6rem"],
resize:false,
scrollbar:false,
id:"curdialog",
content:$("#select-currency-dialog").html(),
success:function(layero,index){
$("#curdialog").css("height","auto");
}
});
}

functionselCountryForword(country){

leturl=location.href;
if(url.indexOf("?")==-1){
location.href=url+"?country="+country;
}else{
url=changeURLArg(url,"country",country);
location.href=url;
}
}



functioninitLang(){
country=$("body").data("country");
curLan=$("body").data("lang");
currency=$("body").data("currency");
currencyDisplay=LANG_GMT[curLan].currencyname[currency];
if(!LANG_GMT.isSupportLang(curLan)){
curLan="en";
}
varcountryStorgae=getCountryByStorage();

if(!country){

if(countryStorgae){
country=countryStorgae;
selCountryForword(country);
}else{

showCurrencyDialog();
}
}


letlocalCountryName=LANG_GMT[curLan].countryname[country];
if(!localCountryName){

if(countryStorgae){

if(country!=countryStorgae){
if(!LANG_GMT.isSupportLang(countryStorgae)){
if(window.localStorage)window.localStorage.removeItem("opeShopCountry")
}
selCountryForword(countryStorgae);
return;
}
country=countryStorgae;
localCountryName=LANG_GMT[curLan].countryname[country];
}
}


if(!localCountryName){

showCurrencyDialog();
}
leticon="<svgaria-hidden='true'focusable='false'style='width:16px;height:16px'data-prefix='fas'data-icon='map-marker-alt'class='icon'role='img'xmlns='http:
$(".countryname").html(icon+localCountryName+"<aclass='switch-country-a'href='javascript:void(0)'>"+LANG_GMT[curLan].click_to_switch+"</a>");
}


functioninitCurrencySelect(){

letstatichost=$("body").data("statichost");
letimg=statichost+LANG_GMT.fis[country];
$(".currency-selectimg").attr("src",img);
$(".currency-selectspan").text(country);


letopts=$(".currency-option");
for(vari=0;i<opts.length;i++){
letval=$(".currency-option").eq(i).data("value");
if(val==country){
$(".currency-option").eq(i).addClass("currency-option-active");
break;
}
}


opts=$(".lang-option");
for(vari=0;i<opts.length;i++){
letval=$(".lang-option").eq(i).data("value");
if(val==curLan){
$(".lang-option").eq(i).addClass("lang-option-active");
break;
}
}
}


/**
*弹出订单查询框
*/
functionshowOrderQueryDialog(){
report("query-order");
letorder_query_title=LANG_GMT[curLan].order_query_title;
letorder_query_placeholder=LANG_GMT[curLan].order_query_placeholder;
letorder_query_cancel=LANG_GMT[curLan].order_query_cancel;
letorder_query_ok=LANG_GMT[curLan].order_query_ok;
layer.prompt({title:order_query_title,formType:2,
success:function(layero,index){
$(".layui-layer-prompttextarea").attr("placeholder",order_query_placeholder);
$(".layui-layer-prompttextarea").select();
},btn:[order_query_ok,order_query_cancel]},
function(pass,index){
letappend={"search_string":pass,content_ids:[$("body").data("id")]}
pushEventFbGG("Search","Search","SEARCH","Search","search",append);
location.href="/clue/success?id="+pass+"&lang="+curLan;
layer.close(index);
});
}




functiongetCountryByStorage(){
if(window.localStorage){
varls=window.localStorage;
returnls.getItem("opeShopCountry");
}
returnnull;
}
functionvoucherListIsNone(text){
varvoucherLine=$(
'<divclass="voucher-row-none">'+text+'</div>'
);
$('.voucher-list').append(voucherLine);
}

functionloadVoucher(mob,currency,callback){
if(mob==''||mob==null||mob==undefined){
voucherListIsNone('Entervouchernoorphoneno');
return;
}
if(currency==''||mob==null||mob==undefined){
voucherListIsNone('Pleaseselectthecurrencyandcheckyourvoucher');
return;
}
$('.voucher-list').empty();
$.post("/voucher/find",{mob:mob,cy:currency},function(data){
$('.voucher-list').empty()
if(data==undefined||data.length==0){
voucherListIsNone('Novouchersavailable');
}else{
varallAnyText="onanyproduct";
if("ar"==curLan){
allAnyText="جميعالمنتجاتمتوفرة";
}
for(vari=0;i<data.length;i++){
varitem=data[i];

varvoucherLine=$(
'<divclass="voucher-row"><divclass="voucher-info"><divclass="voucher-title">-'+item.amount+''+currencyDisplay+allAnyText+'</div><divclass="voucher-code">'
+item.code+'</div></div><divclass="voucher-radio">'+
'<inputclass="voucher-select"type="radio"name="voucher-select"id="voucher-select"value="'+item.code+'"data-amt="'+item.amount+'"></div></div>'
);
$('.voucher-list').append(voucherLine);
}
}

if(callback)callback(data);
});
}



functionreport(action){

setTimeout(function(){
letclientLang=navigator.language||navigator.browserLanguage;
letdata={uv:euv,
origin:document.referrer,
browserLang:clientLang,
pid:$("body").data("pid"),
action:action,
country:country,
lang:curLan,
currency:currency};
$.post("/analysis/v2/log",data,function(){
});
},10);

}


functionredererCurrencyOptions(){
letarr=LANG_GMT.support_current;
letstatichost=$("body").data("statichost");
varht="";
for(vari=0;i<arr.length;i++){
varitem=arr[i];
vara=item.country;
varb=item.currency;
varimg=statichost+LANG_GMT.fis[a];
ht+="<divclass='currency-option'data-value='"+a+"'><divclass='currency-option-img-box'><imgsrc='"+img+"'></div><span>"+b+"</span></div>";
}
$(".currency-options").append(ht);
initCurrencySelect();
}


functioninitAtlas(){
if($(".swiper-container").is(":hidden")){
return;
}
newSwiper('.swiper-container',{loop:true,autoplay:{delay:5000},autoHeight:true,pagination:{el:'.swiper-pagination'}});
}


functionshowSupplementAddrDialog(){
layer.open({
type:1,
area:["7rem"],
resize:false,
scrollbar:false,
id:"suppAddrDialog",
content:$("#supplement-addr-dialog").html(),
success:function(layero,index){
$("#suppAddrDialog").css("height","auto");
}
});
}


functionsubmitClue(){
$.post("/clue/add",submitData,function(resp){
if(resp.code!=200){
letclue_valid=LANG_GMT[curLan].clue_valid;
alertMsg(clue_valid[resp.msg]);
report("submit_server_err");
submiting=false;
$(".phonemust").css("animation","select1slinearinfinite")
return;
}
$(".phonemust").css("animation","0")



if(resp.data.payType=='online'){
location.href="/pay/paypal/create?orderNo="+resp.data.ident+"&payNo="+resp.data.payNo;
}else{
location.href="/clue/created?id="+resp.data.ident;
}
});
}


functiondateDiff(date1,date2){
vardate3=date2.getTime()-date1.getTime();
vardays=Math.floor(date3/(24*3600*1000));
varleave1=date3%(24*3600*1000);
varhours=Math.floor(leave1/(3600*1000));
varleave2=leave1%(3600*1000);
varminutes=Math.floor(leave2/(60*1000));
varleave3=leave2%(60*1000);
varseconds=Math.round(leave3/1000);
hours=(days*24)+hours;
return[hours,minutes,seconds];
}

functionsubmitProcess(payType){
report("submit");
pushEventFbGG("AddToWishlist","AddToWishlist","ADD_TO_WISHLIST","AddToWishlist","add_to_wishlist");
if(submiting){
return;
}

if(!country){
showCurrencyDialog();
return;
}

submiting=true;

varsknum=parseInt($(".quantity-number").text());

varskuAreaLen=$(".select-option").length;
varskuopts=$(".optionActive");
for(leti=0;i<$(".select-option").length;i++){
if($(".select-option").eq(i).find(".optionActive").length==0){
$(".select-option").eq(i).addClass("no-select-content")
}
}

if(skuopts.length<skuAreaLen){
$("body,html").animate({scrollTop:80},300);
letselectSizeColor=LANG_GMT[curLan].selectSizeColor;
alertMsg(selectSizeColor);
submiting=false;
report("submit_sku_err");
return;
}
varskuids="";
for(varj=0;j<sknum;j++){
letops=$(".planSkuOne").eq(j).find(".optionActive");
for(vari=0;i<ops.length;i++){
varskuid=ops.eq(i).data("skuid");
if(!skuid){
continue;
}
skuids+=skuid;
if(i<ops.length-1){
skuids+=",";
}
}
if(j<sknum-1){
skuids+=";";
}
}

vardata={
pid:$(".optionActive").data("pid"),
skuids:skuids,
num:sknum,
fullname:$("#fullname").val(),
mobile:$("#mobile").val(),
country:country,
addr:$("#street").val(),
remark:$(".note").val(),
email:$(".email").val(),
currency:$("body").data("currency"),
optionalMobile:$("#optional_mobile").val(),
lan:curLan,
payType:payType,
voucher:voucherCode,
referer:location.href,
ms:getQueryVariable("ms")
};
if(GetRequest2("agentBy")){
data.mapLgnlat=$(".mapLgnlatVal").val();
data.area=$(".areaVal").val();
}
submitData=data;
letclue_valid=LANG_GMT[curLan].clue_valid;
if(GetRequest2("agentBy")){
if(!data.mapLgnlat){
alertMsg(clue_valid.map_lgnlat);
submiting=false;
$(".mapLgnlatVal").css("animation","select1slinearinfinite")
return;
}
$(".mapLgnlatVal").css("animation","0")
if(!data.area){
alertMsg(clue_valid.area);
submiting=false;
$(".areaVal").css("animation","select1slinearinfinite")
return;
}
$(".areaVal").css("animation","0")
}
if(!data.fullname){
alertMsg(clue_valid.fullName);
submiting=false;
$("#fullname").css("animation","select1slinearinfinite")
return;
}
$("#fullname").css("animation","0")
if(!data.mobile){
alertMsg(clue_valid.mobile);
report("submit_mobile_err");
submiting=false;
$(".phonemust").css("animation","select1slinearinfinite")
return;
}
$(".phonemust").css("animation","0")
if(!data.addr){
alertMsg(clue_valid.addr);
report("submit_addr_err");
submiting=false;
$("#street").css("animation","select1slinearinfinite")
return;
}
$("#street").css("animation","0")
submitClue();
}


functionsumTotalPrice(){
letuseSkuPrice=parseFloat($("body").data("useskuprice"));
letplan=$("body").data("plan");
letnewplan=$("body").data("newplan");
varbasePrice=parseFloat($("body").data("price"));
if(!useSkuPrice){

letdiscBtn=$(".discount-btn-active");
if(discBtn&&discBtn.length>0&&discBtn.data("price")){
$(".allprice").text(currencyDisplay+""+parseFloat(discBtn.data("price")).toFixed(0));
return;
}

letn=parseInt($(".quantity-number").text());
varall=basePrice*n;
$(".allprice").text(currencyDisplay+""+all.toFixed(0));
return;
}

letdiscBtn=$(".discount-btn-active");
letprice=basePrice;
if(discBtn&&discBtn.length>0&&discBtn.data("price")){
price=discBtn.data("price");
}else{
letn=parseInt($(".quantity-number").text());
price=price*n;
}

for(vari=0;i<optionActives.length;i++){
letadd=$(optionActives).eq(i).data("price");
add=add||0;
price+=add;
}
$(".allprice").text(currencyDisplay+""+price.toFixed(0));
}

$(function(){
initAtlas();
$(".lazy").lazyload({


effect:"fadeIn",
threshold:20,
container:$(".home,.address"),
skip_invisible:false,
effect:"fadeIn",
event:"sporty"
});
initLang();
report("in-detail-page");
redererCurrencyOptions();
if(GetRequest2("agentBy")){
$(".mapLgnlat").show();
$(".area").show();
}else{
$(".mapLgnlat").hide();
$(".area").hide();
}


$(".buynowbtn").click(function(){
$(".address").show();
$(".home").hide();
report("click-buy-button");
$("body,html").animate({scrollTop:0},300);
$(".lazy").lazyload({


effect:"fadeIn",
threshold:20,
container:$(".address"),
skip_invisible:false,
effect:"fadeIn",
event:"sporty"
});
pushEventFbGG("AddToCart","AddToCart","ADD_CART","AddToCart","add_to_cart");
});


$(".click-skupanel-close").click(function(){
$(".address").hide();
$(".home").show();
});


$(".addressbody").on("click",".option-btn",function(){
$(this).parent().siblings().find(".option-btn").removeClass("optionActive");
$(this).parent().siblings().find(".option-btn").find(".optionjiaobiao").hide();
$(this).addClass("optionActive");
$(this).parent().parent().removeClass("no-select-content")
$(this).find(".optionjiaobiao").removeClass("hide");
$(this).find(".optionjiaobiao").show();
sumTotalPrice();
});


$(".discount-btn").click(function(){
$(".discount-btn").removeClass("discount-btn-active");
$(this).addClass("discount-btn-active");
letn=$(this).data("num");
$(".quantity-number").text(n).data("num",n);
changeSkuBlock(n);
sumTotalPrice();
});


$(".quantity-add").click(function(){
varn=parseInt($(".quantity-number").text());
n++;
n=n>=100?100:n;
$(".quantity-number").text(n).data("num",n);
changeSkuBlock(n);
sumTotalPrice();
});


$(".quantity-sub").click(function(){
varn=parseInt($(".quantity-number").text());
n--;
n=n<=1?1:n;
$(".quantity-number").text(n).data("num",n);
changeSkuBlock(n);
sumTotalPrice();
});


$(".click-btn-confirmaddress").click(function(){
submitProcess("cod");
});


$("#orderquery").click(function(){
showOrderQueryDialog();
});


$(document).on("click",".queryA",function(){
layer.closeAll();
showOrderQueryDialog();
});


$(".currency-select").click(function(){
showCurrencyDialog();
report("click-select-currency");
});


$(document).on("click",".currency-option",function(){
$(".currency-option").removeClass("currency-option-active");
$(this).addClass("currency-option-active");
});


$(document).on("click",".lang-option",function(){
$(".lang-option").removeClass("lang-option-active");
$(this).addClass("lang-option-active");
});


$(document).on("click",".currency-select-btn",function(){
leturl=location.href;
report("switch-currency");
letselLan=$(".lang-option-active").data("value");
letselCountry=$(".currency-option-active").data("value");
if(!selLan||!selCountry){

return;
}
if(selLan==curLan&&selCountry==country){
layer.closeAll();
return;
}


varls=window.localStorage;
ls.setItem("opeShopCountry",selCountry);

if(url.indexOf("?")==-1){
location.href=url+"?lang="+selLan+"&country="+selCountry;
}else{
url=changeURLArg(url,"lang",selLan);
url=changeURLArg(url,"country",selCountry);
location.href=url;
}
layer.closeAll();
});


$(".home-header-left").click(function(){
letcloseLabel=title=LANG_GMT[curLan].close_btn;
layer.open({
type:1,area:['5rem','100%'],shade:0,offset:'lt'
,maxmin:true,anim:3,isOutAnim:false,shadeClose:false
,content:$("#leftmenu").html(),btn:[closeLabel]
,btn2:function(){
layer.closeAll();
}
});
});


if($("body").data("plan")=='buy-1-free-1'){
$(".multiple-sku").fadeIn();
$(".secondone").removeClass("hide");
$(".lazy").lazyload({
container:$(".secondone"),
skip_invisible:false,
effect:"fadeIn",
event:"sporty"
});
}


$(document).on("click",".supplement-addr-btn",function(){
lettext="";

$(".supplement-addr-text").each(function(){
text+=$(this).val();
});
submitData.optionalAddr=text;
layer.closeAll();
submitClue();
});


letp2=$("body").data("pricep2");
if(!p2){
letiter=setInterval(function(){
letnewDt=newDate();
letarr=dateDiff(newDt,endDte);
lethh=arr[0]<=10?"0"+arr[0]:arr[0]+"";
letmm=arr[1]<=10?"0"+arr[1]:arr[1]+"";
letss=arr[2]<=10?"0"+arr[2]:arr[2]+"";
if(arr[0]<=0&&arr[1]<=0&&arr[2]<=0){
clearInterval(iter);
}
$(".hoursspan").eq(curLan=='ar'?1:0).text(hh.slice(0,1));
$(".hoursspan").eq(curLan=='ar'?0:1).text(hh.slice(1,2));

$(".minutesspan").eq(curLan=='ar'?1:0).text(mm.slice(0,1));
$(".minutesspan").eq(curLan=='ar'?0:1).text(mm.slice(1,2));

$(".secondsspan").eq(curLan=='ar'?1:0).text(ss.slice(0,1));
$(".secondsspan").eq(curLan=='ar'?0:1).text(ss.slice(1,2));
},1000);
}


$(".countryname").on("click",".switch-country-a",function(){
letok=LANG_GMT[curLan].alert_btn;
letcancel=LANG_GMT[curLan].order_query_cancel;
letalert_title=LANG_GMT[curLan].alert_title;
varstr=LANG_GMT[curLan].location_request;
layer.confirm(str,{btn:[ok,cancel],title:alert_title},function(){
getLocation();
showCurrencyDialog();
report("switch-country-form");
},function(){
showCurrencyDialog();
report("switch-country-form");
});

});

$(".contact").click(function(){
location.href=$(this).data("href")+"link:"+encodeURIComponent(location.href);
});
initContexts();
varwinHeight=$(document).scrollTop();
$(window).scroll(function(){
varscrollY=$(document).scrollTop();
if(scrollY>90){
$('.quantity').addClass("fixTopQuan");
}else{
$('.quantity').removeClass("fixTopQuan");
}
});
sumTotalPrice();
});

functioninitContexts(){
$.get($("body").data("oss")+"product/context/"+$("#fbimg").data("pid")+".json?t="+Date.now(),function(res){
varlang=$("body").data("lang")||'en';
if(typeofres==='string'){
res=JSON.parse(res);
}
if(res.contexts){
varc=res.contexts[lang]||res.contexts['en'];
$(".conText").html(c);
vari=res.introductions[lang]||res.introductions['en'];
$(".description").html(i);
}
});

setTimeout(function(){
pushEventFbGG("ViewContent","ViewContent","VIEW_CONTENT","SiteVisit","page_view",{content_ids:[$("body").data("id")],contents:[$(".productName-big").text()]});
},1000*20);
}


functionchangeSkuBlock(n){
letopsLen=$(".planSkuOne").length;
for(leti=0;i<n;i++){
if(opsLen<n){
letclo=$('.planSkuOne').eq(opsLen-1).prop("outerHTML");
$('.planSkuOne').eq(opsLen-1).before(clo);
opsLen++;
letinx=0;
$(".titleNum").each(function(){
$(this).text(++inx);
});
}

}
for(leti=0;i<opsLen;i++){
if(opsLen>n){
$(".planSkuOne").eq(opsLen-1).remove();
opsLen--;
}
}
}


functionGetRequest2(key){
varurl=location.search;
vartheRequest=newObject();
if(url.indexOf("?")!=-1)
{
varstr=url.substr(1);
strs=str.split("&");
for(vari=0;i<strs.length;i++)
{
theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
}
}
varvalue=theRequest[key];
returnvalue;
}
