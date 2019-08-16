function showScoreByCity1(year, xzqhdm,chartBar) 
{  
	// 基于准备好的dom，初始化echarts图表		        
	//var aname = xzqhmc+'一级指标得分比较';
    //$.get('monitor/getCountyScore1?year='+year+'&xzqhdm='+xzqhdm).done(function (data){
    $.get('${base!}/assets/data.json').done(function (data){
    	console.log('一级指标得分返回值')
    	console.log(data);//前端调试控制台输出数据，看看是否符合要求
    	chartBar.setOption({					
			legend:{
				data:[ '得分', '目标值','省均值' ]
			},
			series:[{
				
				data:data
			}]				
		});			    	
        window.onresize = chartRadar1.resize;
      });
}
function showScoreByCity2(year, xzqhdm,chartBar)
{			       
    //var aname = xzqhmc+'二级指标得分比较';
    $.get('monitor/getCountyScore2?year='+year+'&xzqhdm='+xzqhdm).done(function (data){
    	console.log('二级指标得分返回值')
		console.log(data);
    	
    	chartBar.setOption({					
			legend:{
				data:['得分','省均值','目标值']
			},
			yAxis:[{
				interval:5,
				max:function(value){
					return Math.ceil(value.max/5)*5;
				}
			}],
			series:[{
				name:'得分',
				data:data[0].value
			},{
				name:'目标值',
				data:data[1].value
			},{
				name:'省均值',
				data:data[2].value
			}]				
		});
    	
        window.onresize = chartBar2.resize;
    
    });
}
function showScoreByCity3(year, xzqhdm,chartBar) 
{  
	// 基于准备好的dom，初始化echarts图表
    
	//var aname = xzqhmc+'五十四个指标得分比较';
    $.get('monitor/getCountyIndex?year='+year+'&xzqhdm='+xzqhdm).done(function (data){
		console.log('监测点得分返回值')
		console.log(data);
		var monitorname=[];
		for(var i=0;i<46;i++){
			var name =i+1;
			monitorname.push(name);
     	}   
		chartBar.setOption({					
			legend:{
				data:['得分','目标值','省均值']
			},
			
			yAxis:[{
				data:monitorname
			}],
			series:[{
				name:'得分',
				data:data[0].value
			},{
				name:'目标值',
				data:data[1].value
			},{
				name:'省均值',
					data:data[2].value
				}]				
			});				    
    });
}