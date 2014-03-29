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
	function mapInit(){  
	    mapObj = new AMap.Map("iCenter",{  
	    center:new AMap.LngLat(116.397428,39.90923), //地图中心点  
	    level:13  //地图显示的缩放级别  
	    }); 
	    runningTaxi();  
	}
	
	function runningTaxi(){
	$("#Button1").click(function(){
              //var mName=encodeURI($("#mname").val());
              //var mPass=encodeURI($("#mpass").val());
              $.ajax(
              {
                  url:"ShowRequests.action",
                  dataType:"html",
                  //data:{mname:mName,mpass:mPass},
                  success:function(value)
                  {
                     var taxis=jQuery.parseJSON(value);
                     var arr = new Array();//经纬度坐标数组
                     for(var t in taxis)
                     {
						var marker = new AMap.Marker({ //自定义构造AMap.Marker对象                  
  						map:mapObj,                  
  						position: new AMap.LngLat(t.latitude,t.longitude),                  
  						offset: new AMap.Pixel(-10,-34),                  
  						icon: "http://webapi.amap.com/images/0.png"                  
						}); 
						marker=null;                               
                     }
                     /* for(var i=0;i<taxis.res.length;i++)
                     {                 
						arr.push(new AMap.LngLat(taxis.res[i].x, taxis.res[i].y));                               
                     }
                     var polyline = new AMap.Polyline({                    
  							map:mapObj,                  
  							path:arr,                    
  							strokeColor:"#F00",                    
  							strokeOpacity:0.4,                    
  							strokeWeight:3,                    
  							strokeStyle:"dashed",                    
  							strokeDasharray:[10,5]                    
						}); */
                     alert(taxis.size); 
                     //polyline=null;                              
                  }
              })
           })
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
		            <a href="http://localhost:8080/taxiSharing/ShowRequests.action"
		            class="list-group-item">
			        Start Application
		            </a>
		            <a href="#" class="list-group-item">
		            <input id="Button1" type="button" class="btn" value="running taxis"/> 
		            </a>
		        </div>
		        <div style="padding-bottom: 20px;">
				</div>
	        </div>

			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main"
				id="iCenter" scrolling="no"></div>
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
