//雷达定位
$(function(){
    function getmatrix(a,b,c,d,e,f){
        var aa=Math.round(180*Math.asin(a)/ Math.PI);
        var bb=Math.round(180*Math.acos(b)/ Math.PI);
        var cc=Math.round(180*Math.asin(c)/ Math.PI);
        var dd=Math.round(180*Math.acos(d)/ Math.PI);
        var deg=0;
        if(aa==bb||-aa==bb){
            deg=dd;
        }else if(-aa+bb==180){
            deg=180+cc;
        }else if(aa+bb==180){
            deg=360-cc||360-dd;
        }
        return deg>=360?0:deg;

    }
    var obj=$('.shan');
    setInterval(function(){
        var deg=eval('get'+obj.css('transform'));//构造getmatrix函数,返回上次旋转度数
        var step=45;
        obj.css({'transform':'rotate('+(deg+step)%360+'deg)'});
        var dd = obj.css({'transform':'rotate('+(deg+step)%360+'deg)'});
        //上
        if(deg>300&&deg<360 ||deg>0 &&deg<40){
            $('.topcircle').addClass('active');
            $('.rightcircle').removeClass('active');
            $('.leftcircle').removeClass('active');
            // 右
        }else if(deg>50 && deg<80){
            $('.rightcircle').addClass('active');
            $('.leftcircle').removeClass('active');
            $('.topcircle').removeClass('active');
            // 左
        }else if(deg>180 && deg<240){
            $('.leftcircle').addClass('active');
            $('.rightcircle').removeClass('active');
            $('.topcircle').removeClass('active');
        }
    },100);
});
$(function(){
//        //区域内显隐左定位菜单
    $(window).scroll(function() {
        //console.log($(this).scrollTop());
        if($(this).scrollTop() >= 1160 && $(this).scrollTop() <= 3800 ){
            $('#loutinav').show();
        }else{
            $('#loutinav').hide();
        }
    })
})

/**
 *  锚点定位
 */
$(function(){
    if($(this).scrollTop() >= 1160 && $(this).scrollTop() <= 3800 ){
        $('#loutinav').show();
    }else{
        $('#loutinav').hide();
    }

    var scroll_ok = true
        ,$menu = $('#loutinav') //导航容器
        , $as = $menu.find('a') //锚点

    $as.each(function(index){
        var $a = $(this)
            , $content = $($a.attr('href'))
            , offset = $content.offset().top
        $content.attr('offset', offset).attr('index', index)
        //  console.log(offset);
    })

    //点击
    $("#fixedMenu .show_box a").on('click', function(){
        scroll_ok =false;
        var $id = $(this).attr('href');
        $("#fixedMenu .show_box a").parent().removeClass("active");
        $(this).parent().addClass('active');

        $('html,body').animate({scrollTop:$($id).attr('offset')-80} , 200);
        setTimeout(function(){
            scroll_ok = true;
        },500);
        return false;
    })

    //滚动
    $(window).scroll(function() { //true
        if(scroll_ok){
            anchor();
        }
    });

    //识别锚点位置
    function anchor(){
        var Anchor = $(".louti"),
            leftLi = $(".show_box li");
        for(var i=0; i<Anchor.length; i++){
            var scrollTop = $(document).scrollTop(),
                Anchor_top = Anchor.eq(i).offset().top;
            //console.log(Anchor_top);
            if(scrollTop > Anchor_top-369)
            {
                leftLi.removeClass("active");
                leftLi.eq(i).addClass("active");

            }
        }
    };

});