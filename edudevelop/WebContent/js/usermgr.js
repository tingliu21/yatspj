$(document).ready(function() {  
        $('#dg').datagrid({  
            url : "user/query",  
            columns : [ [ {  
                field : 'resourceId',  
                title : '菜单id',  
                width : 50,  
                sortable : true  
            }, {  
                field : 'description',  
                title : '描述',  
                width : 50,  
                sortable : true  
            }, {  
                field : 'name',  
                title : '名称',  
                width : 50,  
                sortable : true  
            }, {  
                field : 'sort',  
                title : '分类',  
                width : 50,  
                sortable : true  
            }, {  
                field : 'type',  
                title : '类型',  
                width : 50,  
                sortable : true  
            }, {  
                field : 'value',  
                title : 'url值',  
                width : 50,  
                sortable : true  
            }, {  
                field : 'pid',  
                title : '父菜单',  
                width : 80,  
                formatter : function(value, row, index) {  
                    if (row.parent) {  
                        return row.parent.resourceId;  
                    } else {  
                        return value;  
                    }  
                }  
            } ] ]  
        });  
    });  