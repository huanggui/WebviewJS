jsWebView
=========

webview 要能够调用js方法，则需要使能其相应js方法。
webview通过loadurl加载一个html页面，然后html中嵌有javascript的处理接口，这样当触发html中处理流程后就会调用js相应的方法。其中调用js方法有2种方式：
一、直接用setWebChromeClient设置js方法代理，让代理去实现js对应的处理流程，如alert等。
二、先从html去js文件中调用对应的js方法，然后在js文件中再调用android java中的处理接口，
    如javascript:window.myjs.chooseDate(dateChoose.value,dateChoose.id);其中myjs是在java中处理js方法的对象,是通过
    addJavascriptInterface(new MyJavaScriptInterface(), "myjs")添加到webview中的。
