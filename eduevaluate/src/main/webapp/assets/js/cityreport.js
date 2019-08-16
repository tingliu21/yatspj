function showScoreByCity(year,xzqhdm,xzqhmc,chartbar) 
{  
	var name = xzqhmc+'各县（市、区）评估分';
	
    $.get('monitor/getTotalScore?year='+year+'&xzqhdm='+xzqhdm).done(function (data){
    	console.log(data);
    	var cname=[],mvalue=[];
    	for(var i = 0; i < data.length; i++){
    		cname.push(data[i].name);
    		mvalue.push(data[i].value);
    	}
    	var option = {
    			title: {
           	        text: name+'排名'
           	    },
    		   
    		    legend: {
    		        data:[name],	    		       
    		    },
    		   
    		    xAxis: [{
    		        data: cname,
    		          
		        }],
    		    yAxis: [{
		            max:  function(value) {
		                return Math.ceil(value.max/10)*10;
		            },
		            interval: 10,
    		           
		        }],
    		    series: [{
    		            name:name,
    		            data:mvalue
		        }]
    		};	
    	 // 为echarts对象加载数据 
    	chartbar.setOption(option);     
    });
}
function showScore1ByCity(year,xzqhdm,xzqhmc,chartbar) 
{  
	data0=[];
	data1=[];
	data2=[];
	data3=[];
	data4=[];
	name0=[];
	name1=[];
	name2=[];
	name3=[];
	name4=[];
	var name = xzqhmc+'一级指标得分比较';
	
    $.get('monitor/getScore1?year='+year+'&xzqhdm='+xzqhdm).done(function (data){
        var mvalue=[];
        
        
        for (var i = 0; i < data.length; i++) {
      		
    		mvalue=data[i].value;        		
 
    		var d0 ={
       			name: data[i].name,
        		    value:mvalue[0]
        	};
    		var d1 ={
       			name: data[i].name,
        		    value:mvalue[1]
        	};
    		var d2 ={
       			name: data[i].name,
        		    value:mvalue[2]
        	};
    		var d3 ={
       			name: data[i].name,
        		    value:mvalue[3]
        	};
    		var d4 ={
       			name: data[i].name,
        		    value:mvalue[4]
        	};
    		data0.push(d0);
	   		data1.push(d1);
	   		data2.push(d2);
	   		data3.push(d3);
	   		data4.push(d4); 		
    	}
        data0.sort(NumDescSort);
        data1.sort(NumDescSort);
        data2.sort(NumDescSort);
        data3.sort(NumDescSort);
        data4.sort(NumDescSort);
        
       
       
       for (var i = 0; i < data.length; i++) {
      	name0.push(data0[i].name);
      	name1.push(data1[i].name);
      	name2.push(data2[i].name);
      	name3.push(data3[i].name);
      	name4.push(data4[i].name);
      	
       }
        
   		var option = {
			title: {
		 		left:'center',
       	        text: name,
       	        top:0
       	    },
		    tooltip: {
		        trigger: 'axis',
		        axisPointer: {
		            type: 'cross',
		            crossStyle: {
		                color: '#999'
		            }
		        }
		    },
		    
		    legend: {
		    	
		    	top:30,
		        data:['优先发展','育人为本','促进公平','教育质量','社会认可'],
		        selectedMode: 'single',
		        
		    },
   		    xAxis: [
 		        {
 		            type: 'category',
 		            data:name0,
 		            axisLabel:{  
 		                interval: 0,
 		                rotate: 30
 		            },
 		            axisPointer: {
 		                type: 'shadow'
 		            }
 		        }
 		    ],
   		    yAxis: [
 		        {
 		            type: 'value',
 		            name: '得分',
 		            min: 0,
 		            max:  function(value) {
 		                return Math.ceil(value.max/5)*5;
 		            },
 		            interval: 5,
 		            axisLabel: {
 		                formatter: '{value}'
 		            }
 		        }
 		    ],
   		    series: [
   		        {
   		            name:'优先发展',
   		            type:'bar',
   		            label: {
   			            normal: {
   			                position: 'top',
   			                show: true
   			            }
   			        },
   			        barMaxWidth: 60,
   		            data:data0
   		        }, {
   		            name:'育人为本',
   		            type:'bar',
   		            label: {
   			            normal: {
   			                position: 'top',
   			                show: true
   			            }
   			        },
   			        barMaxWidth: 60,
   		            data:data1
   		        }, {
   		            name:'促进公平',
   		            type:'bar',
   		            label: {
   			            normal: {
   			                position: 'top',
   			                show: true
   			            }
   			        },
   			        barMaxWidth: 60,
   		            data:data2
   		        }, {
   		            name:'教育质量',
   		            type:'bar',
   		            label: {
   			            normal: {
   			                position: 'top',
   			                show: true
   			            }
   			        },
   			        barMaxWidth: 60,
   		            data:data3
   		        }, {
   		            name:'社会认可',
   		            type:'bar',
   		            label: {
   			            normal: {
   			                position: 'top',
   			                show: true
   			            }
   			        },
   			        barMaxWidth: 60,
   		            data:data4
   		        }
   		    ]
   		};

   	 // 为echarts对象加载数据 
   		chartbar.setOption(option);  
    });
}
function NumDescSort(a,b){
    return a.value-b.value;
}