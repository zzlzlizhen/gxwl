function turnPage(nowPage) {
    var urls = window.location.href;
    var site = urls.indexOf("?");
    if(site > 0){
        urls = urls.substring(0,site)
    }
    urls = urls + "?page.nowPage=" + nowPage;
    window.location.href = urls;
}