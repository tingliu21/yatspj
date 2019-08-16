//无中职高中地区排名
function showScoreByNoHigh(year,chartBar){
	
	$.get('monitor/getTotalScoreByNoHigh?year='+year).done(function (data){	    
        var cname=[],mvalue=[];	
    	for(var i = 0; i < data.length; i++){
    		cname.push((data.length-i)+'.'+data[i].name);
    		mvalue.push(data[i].value);
    	}
        var ctitle="特殊地区教育现代化水平排名";
       
    	var option = {
			 	title: {
			 		text: ctitle,			 		
	       	    },   		          
    		    yAxis: [{    		           
    		            data: cname,  		         
    		        }],
    		    xAxis: [{	           
		            max:  function(value) {
		                return Math.ceil(value.max/10)*10;
		            },
		            interval: 10,		            
		           }],
    		    series: [{
    		            name:'得分',
    		            data:mvalue
		        }]
    		};
    	// 为echarts对象加载数据 
        chartBar.setOption(option); 
        window.onresize = chartBar.resize;
    });
}
//重点扶贫县排名
function showScoreByKey(year,chartBar){
	
	$.get('monitor/getTotalScoreByKey?year='+year).done(function (data){	    
        var cname=[],mvalue=[];	
    	for(var i = 0; i < data.length; i++){
    		cname.push((data.length-i)+'.'+data[i].name);
    		mvalue.push(data[i].value);
    	}
        var ctitle="重点帮扶县教育现代化水平排名";
       
    	var option = {
			 	title: {
			 		text: ctitle,			 		
	       	    },   		          
    		    yAxis: [{    		           
    		            data: cname,  		         
    		        }],
    		    xAxis: [{	           
		            max:  function(value) {
		                return Math.ceil(value.max/10)*10;
		            },
		            interval: 10,		            
		           }],
    		    series: [{
    		            name:'得分',
    		            data:mvalue
		        }]
    		};
    	// 为echarts对象加载数据 
        chartBar.setOption(option); 
        window.onresize = chartBar.resize;
    });
	
	
}