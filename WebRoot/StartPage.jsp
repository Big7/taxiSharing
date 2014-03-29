<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="css/bootstrap.min.css" rel="stylesheet">
<title>Taxi Sharing</title>
<link href="css/gaode.demo.Default.css" rel="stylesheet" 	/>
<link href="css/dashboard.css" rel="stylesheet">
<link href="css/blog.css" rel="stylesheet">
<style>
	div#scroll-able {
	    height:400px;
	    overflow:scroll;
	}
</style>
<script language="javascript"
	src="http://webapi.amap.com/maps?v=1.2&key=4c610eb884b00b883f012ca642f6d8ab"></script>
<script src="JavaScript/jquery-2.1.0.js"></script>
<script language="javascript">
	var mapObj;
	var route_text, steps;  
	var polyline; 
	var DrivingOption;
	//初始化地图对象，加载地图
	function mapInit() {
		mapObj = new AMap.Map("iCenter", {
			center : new AMap.LngLat(116.397428, 39.90923), //å°å¾ä¸­å¿ç¹
			continuousZoomEnable:false,  
		    level:10  //å°å¾æ¾ç¤ºçç¼©æ¾çº§å«  
		    });   
		    //地图图块加载完成后触发事件complete
		    AMap.event.addListener(mapObj,"complete",driving_route);
		    mapObj.plugin(["AMap.Driving"], function() {  
	        DrivingOption = {  
	            //驾车策略，包括 LEAST_TIME，LEAST_FEE, LEAST_DISTANCE,REAL_TRAFFIC  
	            policy: AMap.DrivingPolicy.LEAST_TIME   
	        }; 
	        
	        
	    }); 
	}
  	var start_xy ;//= new AMap.LngLat(116.379018,39.865026);  
 	var end_xy; //= new AMap.LngLat(116.321139, 39.896028); 
	//改写completeEventHandler，小车跑路线
	function car_move(){  
	    alert(start_xy);  
	    
		var sicon = new AMap.Icon({  
	        image: "http://api.amap.com/Public/images/js/poi.png",  
	        size:new AMap.Size(44,44),  
	        imageOffset: new AMap.Pixel(-334, -180)  
	    });  
	    var startmarker = new AMap.Marker({  
	        icon : sicon, //复杂图标  
	        visible : true,   
	        position : start_xy,  
	        map:mapObj,  
	        offset : {  
	            x : -16,  
	            y : -40  
	        }  
	    });  
	    var eicon = new AMap.Icon({  
	        image: "http://api.amap.com/Public/images/js/poi.png",  
	        size:new AMap.Size(44,44),  
	        imageOffset: new AMap.Pixel(-334, -134)  
	    });  
	    var endmarker = new AMap.Marker({  
	        icon : eicon, //复杂图标  
	        visible : true,   
	        position : end_xy,  
	        map:mapObj,  
	        offset : {  
	            x : -16,  
	            y : -40  
	        }  
	    });  
	    
	        marker = new AMap.Marker({  
	            map:mapObj,  
	            //draggable:true, //æ¯å¦å¯æå¨  
	            position:start_xy,//åºç¹ä½ç½®  
	            icon:"http://code.mapabc.com/images/car_03.png", //markerå¾æ ï¼ç´æ¥ä¼ éå°åurl  
	            offset:new AMap.Pixel(-26,-13), //ç¸å¯¹äºåºç¹çä½ç½®  
	            autoRotation:true  
	        });  
	          
	        car_route = new Array();  
	        car_route.push(start_xy);
		    for(var s=0; s<steps.length; s++){  
		    	var path_xy = steps[s].path;
		    	//car_route.push(path_xy[(path_xy.length-1)]);
		    	//alert(path_xy.length);
		    	for(var ss=0; ss<path_xy.length; ss++){
		    	car_route.push(path_xy[ss]);
		    	//addMarker(path_xy[ss]);
		        
		        }
		          
		    }  
		    var polyline = new AMap.Polyline({  
		            map: mapObj,  
		            path: car_route,  
		            strokeColor:"#00A",//çº¿é¢è²  
		            strokeOpacity:1,//çº¿éæåº¦  
		            strokeWeight:3,//çº¿å®½  
		            strokeStyle:"solid",
		        });
		    marker.moveAlong(car_route,900);  
		    mapObj.setFitView();
	  }  
	  
	//驾车导航  
	function driving_route() {
	    $(document).click(function(e){
		    	var place=$(e.target).text();
			    place=place.replace(")->(",",");
			    place=place.replace("(","");
			    place=place.replace(")","");
			    var array=place.split(",");
			    
				start_xy = new AMap.LngLat(array[1],array[0]); 
				alert(start_xy.getLng());
				end_xy = new AMap.LngLat(array[3],array[2]); 
			    alert(end_xy.getLat());
			    var MDrive;         
		        MDrive = new AMap.Driving(DrivingOption); //构造驾车导航类   
		        //当查询（search）成功时触发complete事件
		        AMap.event.addListener(MDrive, "complete", driving_routeCallBack); //返回导航查询结果  
			    MDrive.search(start_xy, end_xy);
			    setTimeout(function(){car_move();}
			    			,3000);
							    
		    }); 
	} 
	 //导航结果展示，输出路线  
	function driving_routeCallBack(data) { 
	    console.log(data);  
	    alert("aaaaaaaa");
	    var routeS = data.routes;  
	    if (routeS.length <= 0) {  
	        document.getElementById("result").innerHTML = "未查找到任何结果!<br />建议：<br />1.请确保所有字词拼写正确。<br />2.尝试不同的关键字。<br />3.尝试更宽泛的关键字。";  
	    }   
	    else{   
	        route_text="";  
	        for(var v =0; v< routeS.length;v++){  
	            //驾车步骤数  
	            steps = routeS[v].steps;
	            var route_count = steps.length;  
	            //行车距离（米）  
	            var distance = routeS[v].distance;  
	            //拼接输出html  
	            for(var i=0 ;i< steps.length;i++){  
	                route_text += "<tr><td align=\"left\" onMouseover=\"driveDrawFoldline('" + i + "')\">" + i +"." +steps[i].instruction  + "</td></tr>";  
	            }  
	        }  
	        //输出行车路线指示  
	        route_text = "<table cellspacing=\"5px\"><tr><td style=\"background:#e1e1e1;\">路线</td></tr><tr><td><img src=\"http://code.mapabc.com/images/start.gif\" />&nbsp;&nbsp;北京南站</td></tr>" + route_text + "<tr><td><img src=\"http://code.mapabc.com/images/end.gif\" />&nbsp;&nbsp;北京西站</td></tr></table>";  
	        document.getElementById("result").innerHTML = route_text;  
	        //drivingDrawLine();  
	        
	    }     
	}  
	//绘制驾车导航路线  
	function drivingDrawLine(s) {  
		console.log(s);  
	    //起点、终点图标  
	    var sicon = new AMap.Icon({  
	        image: "http://api.amap.com/Public/images/js/poi.png",  
	        size:new AMap.Size(44,44),  
	        imageOffset: new AMap.Pixel(-334, -180)  
	    });  
	    var startmarker = new AMap.Marker({  
	        icon : sicon, //复杂图标  
	        visible : true,   
	        position : start_xy,  
	        map:mapObj,  
	        offset : {  
	            x : -16,  
	            y : -40  
	        }  
	    });  
	    var eicon = new AMap.Icon({  
	        image: "http://api.amap.com/Public/images/js/poi.png",  
	        size:new AMap.Size(44,44),  
	        imageOffset: new AMap.Pixel(-334, -134)  
	    });  
	    var endmarker = new AMap.Marker({  
	        icon : eicon, //复杂图标  
	        visible : true,   
	        position : end_xy,  
	        map:mapObj,  
	        offset : {  
	            x : -16,  
	            y : -40  
	        }  
	    });  
	    //起点到路线的起点   
	    var extra_path1 = new Array();  
	    extra_path1.push(start_xy);  
	    extra_path1.push(steps[0].path[0]);  
	    var extra_line1 = new AMap.Polyline({  
	        map: mapObj,  
	        path: extra_path1,  
	        strokeColor: "#FFFFFF",  
	        strokeOpacity: 0.7,  
	        strokeWeight: 4,  
	        strokeStyle: "dashed",  
	        strokeDasharray: [10, 5]  
	    });  
	    //路线的终点到终点
	    var extra_path2 = new Array();  
	    var path_xy = steps[(steps.length-1)].path;  
	    extra_path2.push(end_xy);  
	    extra_path2.push(path_xy[(path_xy.length-1)]);  
	    var extra_line2 = new AMap.Polyline({  
	        map: mapObj,  
	        path: extra_path2,  
	        strokeColor: "#000000",  
	        strokeOpacity: 0.7,  
	        strokeWeight: 4,  
	        strokeStyle: "dashed",  
	        strokeDasharray: [10, 5]  
	    });  
	    //绘制无道路部分 
	    var drawpath = new Array();  
	    for(var s=0; s<steps.length; s++){  
	        drawpath = steps[s].path;  
	        var polyline = new AMap.Polyline({  
	            map: mapObj,  
	            path: drawpath,  
	            strokeColor: "#333333",  
	            strokeOpacity: 0.7,  
	            strokeWeight: 4,  
	            strokeDasharray: [10, 5]  
	        });  
	        
	        
	    }  
	    
	    mapObj.setFitView();  
	    
	}  
	//放大某段路线 
	function driveDrawFoldline(num){  
	    var drawpath1 = new Array();  
	    drawpath1 = steps[num].path;  
	    if(polyline != null){  
	        polyline.setMap(null);  
	    }  
	    polyline = new AMap.Polyline({  
	            map: mapObj,  
	            path: drawpath1,  
	            strokeColor: "#FF3030",  
	            strokeOpacity: 0.9,  
	            strokeWeight: 4,  
	            strokeDasharray: [10, 5]  
	        });  
	  
	    mapObj.setFitView(polyline);  
	} 
	//æ¸é¤ææ¬
	function clearText(field){

    	if (field.defaultValue == field.value) field.value = '';
    	else if (field.value == '') field.value = field.defaultValue;
	}
	function addMarker(p){  
	    marker=new AMap.Marker({                    
	    icon:"http://webapi.amap.com/images/marker_sprite.png",  
	    position: p,
	    });  
	    marker.setMap(mapObj);  //在地图上添加点  
	}  
	//地图图块加载完毕后执行函数  
	function completeEventHandler(){    
	        marker = new AMap.Marker({  
	            map:mapObj,  
	            //draggable:true, //æ¯å¦å¯æå¨  
	            position:new AMap.LngLat(116.273881,39.807409),//åºç¹ä½ç½®  
	            icon:"http://code.mapabc.com/images/car_03.png", //markerå¾æ ï¼ç´æ¥ä¼ éå°åurl  
	            offset:new AMap.Pixel(-26,-13), //ç¸å¯¹äºåºç¹çä½ç½®  
	            autoRotation:true  
	        });  
	          
	        var lngX = 116.273881;  
	        var latY = 39.807409;         
	        lineArr = new Array();   
	        lineArr.push(new AMap.LngLat(lngX,latY));   
	        for (var i = 1; i <30; i++){   
	            lngX=lngX+Math.random()*0.05;   
	            if(i%2){   
	                latY = latY+Math.random()*0.0001;   
	            }else{   
	                latY = latY+Math.random()*0.06;   
	            }   
	            lineArr.push(new AMap.LngLat(lngX,latY));   
	        }  
	        //ç»å¶è½¨è¿¹  
	        var polyline=new AMap.Polyline({  
	            map:mapObj,  
	            path:lineArr,  
	            strokeColor:"#00A",//çº¿é¢è²  
	            strokeOpacity:1,//çº¿éæåº¦  
	            strokeWeight:3,//çº¿å®½  
	            strokeStyle:"solid",//çº¿æ ·å¼  
	        });  
	  }  
	  function startAnimation()  
	  {     
	    marker.moveAlong(lineArr,900);  //speed
	  }  
	  
	  function stopAnimation()  
	  {     
	    marker.stopMove();  
	  } 
	  
	
	
</script>
</head>
<body onLoad="mapInit()">
<div class="navbar navbar-default navbar-fixed-top" role="navigation">
      <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
              <span class="sr-only">Toggle navigation</span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Taxi Sharing</a>
          </div>
          <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
              <li class="active"><a href="#">Home</a></li>
              <li><a href="#about">About</a></li>
              <li><a href="#contact">Contact</a></li>
              <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <b class="caret"></b></a>
                <ul class="dropdown-menu">
                  <li><a href="#">Action</a></li>
                  <li><a href="#">Another action</a></li>
                  <li><a href="#">Something else here</a></li>
                  <li class="divider"></li>
                  <li class="dropdown-header">Nav header</li>
                  <li><a href="#">Separated link</a></li>
                  <li><a href="#">One more separated link</a></li>
                </ul>
              </li>
            </ul>
          </div>
      </div>
    </div>
<div class="container-fluid">
      <div class="row">
			<div class="col-sm-3 col-md-2 sidebar" >
				<div id="scroll-able" class="list-group" >
		            <a href="#" class="list-group-item active">
		              Query
		            </a>
			            <s:iterator value="requests">
			             <a id="riders" class="list-group-item">
			            	(<s:property value="OriginLat"/>,
			            	<s:property value="OriginLng"/>)->(
			            	<s:property value="DestinationLat"/>,
			            	<s:property value="DestinationLng"/>)
			            </a>
			            </s:iterator>
		        </div>
		        <div id="scroll-able" class="list-group" >
		            <a href="#" class="list-group-item active">
		              Schedule
		            </a>
			            <s:iterator value="requests"  status="st">
			             <a id="riders" class="list-group-item">
			            	<s:property value="#st.getIndex()+1"/>
			            </a>
			            </s:iterator>
		        </div>
		        <div style="padding-bottom: 20px;">
				</div>
	        </div>

			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main"
				id="iCenter" >
			</div>
			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2">  
		        <input type="button" value="navigation" onclick="driving_route()"/> 
		        <input type="button" value="car move" onclick="car_move()"/>
		        <input id="Button1" type="button" class="btn" value="running taxis"/> 
		        <input id="Button2" type="button" class="btn" value="show requests"/> 
		        <div id="r_title"><b>query result:</b></div>  
       			<div id="result"> </div>
		    </div>
		</div>
	</div>
	
<div class="blog-footer">
      <p>Copyright 2014<a href="http://database.ecnu.edu.cn"> Taxi-Sharing </a> by <a href="https://twitter.com/mdo">@C^3BD ECNU</a>.</p>
      <p>
        <a href="#">Back to top</a>
      </p>
    </div>
</body>
</html>
