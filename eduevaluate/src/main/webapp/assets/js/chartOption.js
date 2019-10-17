var mapOption={
    title: {
        text: "浙江省教育现代化综合得分",
        left: 'center'
    },
    tooltip: {
        trigger: 'item'
    },
    visualMap: {
        min: 50,
        max: 100,
        left: 'left',
        top: 'bottom',
        text: ['高', '低'], // 文本，默认为数值文本
        color: ['#c05050','#e5cf0d','#5ab1ef'],
        calculable: true
    },
    grid: {
        right: 40,
        top: 50,
        bottom: 40,
        width: '30%'
    },
    xAxis: [{
        position: 'top',
        type: 'value',
        boundaryGap: false,
        splitLine: {
            show: false
        },
        axisLine: {
            show: false
        },
        axisTick: {
            show: false
        },
    }],
    yAxis: [{
        type: 'category',
        // data: titledata,
        axisTick: {
            alignWithLabel: true
        }
    }]

};
var radarOption= {
	title : {
		text : '一级指标达成度',
		left : 'center',
		top : 0
	},
	tooltip : {},
	legend : {
		top : 20,
		//data : [ '浙江省', '目标值' ],
		left : 'center'
	},
	radar : {
		// shape: 'circle',
		name : {
			textStyle : {
				color : '#fff',
				backgroundColor : '#999',
				borderRadius : 2,
				padding : [ 3, 4 ]
			}
		},
		toolbox : {
			feature : {
				dataView : {
					show : true,
					readOnly : false
				},
				magicType : {
					show : true,
					type : [ 'line', 'bar' ]
				},
				restore : {
					show : true
				},
				saveAsImage : {
					show : true
				}
			}
		},
		indicator : [ {
			name : '优先发展(35)',
			max : 35
		}, {
			name : '育人为本(15)',
			max : 15
		}, {
			name : '促进公平(25)',
			max : 25
		}, {
			name : '教育质量(15)',
			max : 15
		}, {
			name : '社会认可(10)',
			max : 10
		} ]
	},
	series : [ {
		//name : '平均值 vs 目标值',
		type : 'radar',

		//data : data,
		itemStyle : {
			normal : {
				label : {
					show : true,
					position : 'top',

					formatter : function(a) {
						
						if (a.dataIndex == 1)
							return '';
						else
							return a.Number;

					}
				}
		
            
			}
		}
	} ]
};
var barOption_v={
	title : {
		left : 'center',
		top : 0,
		//text : '11个二級指标实现程度比较'
	},
	tooltip : {
		trigger : 'axis',
		axisPointer : {
			type : 'cross',
			crossStyle : {
				color : '#999'
			}
		}
	},
	/*  toolbox: {
	     feature: {
	         dataView: {show: true, readOnly: false},
	         magicType: {show: true, type: ['line', 'bar']},
	         restore: {show: true},
	         saveAsImage: {show: true}
	     }
	 }, */
	legend : {
		top : 25,
		//data : [ '省均值', '目标值' ],
		left : 'center'
		
	},
	/*  grid:{
	     x:100,
	     y:100,
	     x2:100,
	     y2:100
	 }, */
	xAxis : [ {
		type : 'category',
		data : [ '经费保障', '教师保障', '资源保障', '素质教育',
				'国际交流与合作',  '义务教育资源均衡',
				'教育协调发展', '教育发展水平','学生发展', '教育满意' ],
		axisLabel : {
			interval : 0,
			rotate : 30
		},
		axisPointer : {
			type : 'shadow'
		}
	} ],
	yAxis : [ {
		type : 'value',
		name : '得分',
		interval : 5,
		min : 0,
		max : function(value) {
			return Math.ceil(value.max/5)*5;
		},
		
		axisLabel : {
			formatter : '{value}'
		}
	} ],
	series : [ {
		//name : '省均值',
		type : 'bar',
		z : 3,
		itemStyle: {
            normal: {
                color: '#EF4947'
            }
        },
		label : {
			normal : {
				position : 'insideTop',
				show : true
			}
		},
		barMaxWidth: 60,
		//data : data[0].value
	},

	{
		//name : '目标值',
		type : 'bar',
		z : 0,
		barGap : '-100%', // Make series be overlap
		silent : true,
		itemStyle : {
			normal : {
				color : '#F1EEE8'
			}
		},
		label : {
			normal : {
				position : 'top',
				show : true,
				color : '#999'
			}
		},
		//data : data[1].value
	},
	{        
        type:'bar',
        z :2,
        barGap: '-100%', // Make series be overlap
        silent: true,
        itemStyle: {
            normal: {
                color: '#FEBF74'
            }
        },
        label: {
            normal: {
                position: 'top',
                show: true,
                color:'#634739'
            }
        }
        
    }]
};
var barOption_h={

	tooltip : {},
	title : {
		left : 'center',
		top : 0,
		text : '45个监测点实现程度比较',
	},
	legend : {
		top : 25,
		//data : [ '省均值', '目标值' ]
	},
	grid: {
		containLabel: true
	},
	xAxis : [ {
		type : 'value',
		max : 4,
		splitLine : {
			show : false
		}
	} ],
	yAxis : [ {
		type : 'category',
		//data : monitorname,
		axisLabel : {
			interval : 0
			//rotate : 30
		},
		splitLine : {
			show : false
		}
	} ],
	series : [ {
		type : 'bar',
		//name : '省均值',
		z : 3,
		itemStyle: {
            normal: {
                color: '#EF4947'
            }
        },
		label : {
			normal : {
				position : 'insideRight',
				center:'left',
				verticalAlign:'middle',
				//distance:10,
				show : true,
				//formatter : '{c}'//{b}表示类别，如1-54，{c}表示柱子的取值
				formatter: function(param){
					return param.value;
					
				}
			}
		},
        //barMaxWidth: 50
		//data : data[0].value

	}, {
		type : 'bar',
		name : '目标值',
		z : 0,
		barGap : '-100%', // Make series be overlap
		silent : true,
		label : {
			normal : {
				position : 'right',
				show : true,
				color : '#999'
			}
		},
		itemStyle : {
			normal : {
				color : '#F1EEE8'
			}
		}
		//data : data[1].value

	},{
        
        type:'bar',
        z : 2,
        barGap: '-100%', // Make series be overlap
        silent: true,
        itemStyle: {
            normal: {
                color: '#FEBF74'
            }
        },
        label: {
            normal: {
                position: 'right',
                show: true,
                color:'#634739'
            }
        },
        //data:data[2].value
    } ]
};
