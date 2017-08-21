# cordova-plugin-alipay-v2（更新至20170725）
### 前言：
支付宝的cordova插件其实在github上已经有很多了，但是都已经是以前的版本了。在2016年11月的时候支付宝进行了一次更新，支付宝的SDK升级到2.0版本。以前在app中使用支付宝进行支付叫做移动支付，11月之后更名为APP支付。
###### 本插件仅支持《APP支付》，不支持移动支付
***
### 功能说明
1. 根据支付宝的说明文档的建议，为保证安全，签名都放到后端去做，前端只需要接收后台传入签名字符串，使用该插件调用支付宝SDK完成支付
2. APP_ID：对应开放平台中应用的APPID，主要用于iOS平台xcode构建URL Schemes

***
### 支持平台
1. android （alipaySdk-2070725.jar）
2. iOS

***
### 安装
###### 在线安装
    cordova plugin add cordova-plugin-alipay-v2 --variable APP_ID=[your AppId]

    cordova plugin add https://github.com/hhjjj1010/cordova-plugin-alipay-v2.git --variable APP_ID=[your AppId]

###### 本地安装
下载插件到本地

    cordova plugin add /your/local/path --variable APP_ID=[your AppId]
    
***
### 使用 API

    // ionic3上使用时需早import结束后添加 declare let cordova;
    
    // 第一步：订单在服务端签名生成订单信息，具体请参考官网进行签名处理
    var payInfo  = "xxxx";

    // 第二步：调用支付插件            
    cordova.plugins.alipay.payment(payInfo,function success(e){},function error(e){});

     //e.resultStatus  状态代码  e.result  本次操作返回的结果数据 e.memo 提示信息
     //e.resultStatus  9000  订单支付成功 ;8000 正在处理中  调用function success
     //e.resultStatus  4000  订单支付失败 ;6001  用户中途取消 ;6002 网络连接出错  调用function error
     //当e.resultStatus为9000时，请去服务端验证支付结果
                /**
                 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                 * docType=1) 建议商户依赖异步通知
                 */
                
                
#### TIPS
##### 1. iOS上支付成功之后无法回调
xcode的URL Types上alipay的URL Schemes正确格式应为ali2xxxxxxxxxxxxxxx。2开头的这串数字是你的APP_ID，英文字母与数字之间没有任何符号！！！

##### 2. 沙箱环境
在我个人的开发过程中确实是没有使用到沙箱环境，都是直接真实支付1分钱来做测试。
如要使用沙箱环境，请自行参考官方文档https://docs.open.alipay.com/200/105311/
