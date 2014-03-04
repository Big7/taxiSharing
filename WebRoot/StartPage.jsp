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
<script language="javascript"
	src="http://webapi.amap.com/maps?v=1.2&key=4c610eb884b00b883f012ca642f6d8ab"></script>
<script language="javascript">
	var mapObj;
	//初始化地图对象，加载地图
	function mapInit() {
		mapObj = new AMap.Map("iCenter", {
			center : new AMap.LngLat(116.397428, 39.90923), //地图中心点
			continuousZoomEnable:false,  
		    level:10  //地图显示的缩放级别  
		    });   
		    AMap.event.addListener(mapObj,"complete",completeEventHandler); 
	}
	
	//清除文本
	function clearText(field){

    	if (field.defaultValue == field.value) field.value = '';
    	else if (field.value == '') field.value = field.defaultValue;
	}
	//地图图块加载完毕后执行函数  
	function completeEventHandler(){    
	        marker = new AMap.Marker({  
	            map:mapObj,  
	            //draggable:true, //是否可拖动  
	            position:new AMap.LngLat(116.273881,39.807409),//基点位置  
	            icon:"http://code.mapabc.com/images/car_03.png", //marker图标，直接传递地址url  
	            offset:new AMap.Pixel(-26,-13), //相对于基点的位置  
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
	        //绘制轨迹  
	        var polyline=new AMap.Polyline({  
	            map:mapObj,  
	            path:lineArr,  
	            strokeColor:"#00A",//线颜色  
	            strokeOpacity:1,//线透明度  
	            strokeWeight:3,//线宽  
	            strokeStyle:"solid",//线样式  
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
	
	//关键词搜索
	function placeSearch() {
        
	    var keyword=document.getElementById("keyword").value.toString();
	    var MSearch;
	    mapObj.plugin(["AMap.PlaceSearch"], function() {        
	        MSearch = new AMap.PlaceSearch({ //构造地点查询类
	            city:"北京" //城市
	        }); 
	        AMap.event.addListener(MSearch, "complete", keywordSearch_CallBack);//返回地点查询结果
	        MSearch.search(keyword); //关键字查询
	    });
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
				<div class="list-group">
		            <a href="#" class="list-group-item active">
		              Query
		            </a>
		            <a href="#" class="list-group-item" onclick="startAnimation()">
			            <s:iterator value="Requests">
			            	<s:property value="Origin"/>->
			            	<s:property value="Destination"/>
			            </s:iterator>
			        <input type="button" value="start" onclick="startAnimation()"/>
		            </a>
		            <a href="#" class="list-group-item">
		            <input type="button" value="stop" onclick="stopAnimation()"/>
		            </a>
		            
		            <a href="#" class="list-group-item">1.aaaa->bbbb</a>
		            <a href="#" class="list-group-item">2.bbbb->cccc</a>
		            <a href="#" class="list-group-item">3.cccc->dddd</a>
		            <a href="#" class="list-group-item">4.dddd->eeee</a>
		        </div>
		        <div style="padding-bottom: 20px;">
				</div>
	        </div>

			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main"
				id="iCenter" scrolling="no"></div>
			<div style="padding:2px 0px 0px 5px;font-size:12px">  
		        <input type="button" value="start" onclick="startAnimation()"/>  
		        <input type="button" value="stop" onclick="stopAnimation()"/>  
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
