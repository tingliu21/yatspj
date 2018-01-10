var ioc = {
	dao : {
        type : "org.nutz.dao.impl.NutDao",
        args : [{refer:"dataSource"}]
    },
    dataSource : {
        type: "com.alibaba.druid.pool.DruidDataSource",
        events: {
            depose: 'close'
        },
        fields: {
            url: "jdbc:postgresql://localhost:5432/edudevelop",
            username: "postgres",
            password: "123456",
            maxWait: 15000, // 若不配置此项,如果数据库未启动,druid会一直等可用连接,卡住启动过程
            defaultAutoCommit: false // 提高fastInsert的性能
        }
    }
//    ,
//    tmpFilePool : {
//        type : 'org.nutz.filepool.NutFilePool',
//        // 临时文件最大个数为 1000 个
//        args : [ '~/tmp/files', 20 ]
//    },
//    uploadFileContext : {
//        type : 'org.nutz.mvc.upload.UploadingContext',
//        singleton : false,
//        args : [ {
//            refer : 'tmpFilePool'
//        } ],
//        fields : {
//            // 是否忽略空文件, 默认为 false
//            ignoreNull : true,
//            // 单个文件最大尺寸(大约的值，单位为字节，即 1048576 为 1M)
//            maxFileSize : 1048576,
//        // 正则表达式匹配可以支持的文件名
////            nameFilter : '^(.+[.])(xls)$'
//        }
//    },
//    myUpload : {
//        type : 'org.nutz.mvc.upload.UploadAdaptor',
//        singleton : false,
//        args : [ {
//            refer : 'uploadFileContext'
//        } ]
//    }
}