# 项目简介

### 1.起步
一个简短的IM应用，一步步实现从最基础到分布式版本

#### 1.1版本
采用Socket搭配ServerSocket进行网络编程，SpringBoot应用启动后，SingleExecutorService开启独立线程循环accept到Socket连接。
然后向线程池中提交具体任务，任务通过socket获取输入输出流与客户端进行交互。   

有了数据流之后，开始想着定义数据结构，消息以何种格式在客户端和服务端进行识别呢。本着方便简介的目的，
直接采用IO流进行编码，好理解点直接使用DataObjectStream和DataInputStream对InputStream和OutPutStream进行装饰，
这两个API能很方便的直接将流读取成String或者将String写入流，然后进行二进制传输。
同时封装了Msg类作为数据最终要转换成的格式。

#### 1.2版本
能与客户端进行交互后，开始想着设计用户体系，本着初版怎么轻量怎么来的原则，使用h2数据库。同时建立用户表。
有了用户之后，可以试着建立互相对话了。而不是服务器固定返回，服务器作一个中转，将数据转发给对应的用户。
同时，也不可能免登录就开始对话，所以相互对话前得先获取身份且登录。做个简单的逻辑即可，定义Msg的code 201为登录消息。服务器进行处理。
同时，Msg对象也需要调整，升级为EnhanceMsg（AbstractMsg+具体实现）。  

由于服务器从模拟返回固定数据模拟和用户对话转变成数据转发处理的角色，所以返回的数据格式也需要重新定义下，而非使用
和客户端相同的Object进行序列化。所以封装个Result作为服务器固定返回格式。  

#### 1.3版本
有了用户体系后，需要完成用户间对话，也就是两个socket间数据，所以需要服务端存储用户和socket的对应关系，socket虽然是一直
持有连接，但是用户登出后还是会断开连接。所以抽象出Session替换Connections，将用户和对应的socket存入session中。互相对话根据用户拿到对应的socket即可进行数据
传输。  
同时，服务端最基本的逻辑ok后，我不满足于只在测试类中模拟客户端，所以自己编写个客户端出来。但是客户端也相对麻烦，原因是只会js是不行的。
网页的前端因为基于浏览器的，他只能撰写应用层的http以及websocket协议的数据调用，所以还得学习下electron的用法。这样就能基于socket进行
编程了。当然基于java的swing或者javaFx也可以，但是论简单度来讲，我更倾向于electron，并且electron的强大远不止此，他能使用nodejs，这样我就能进行网络编程了。  

如我大概用electron写了个最简单的如下，整体流程为：使用nodejs连接socket，然后将页面上的数据收集起来采用electron技术
提交给node，然后进行基于socket的网络编程。

![small1](./sql/smallTalk1.png)

![small2](./sql/smallTalk2.png)

这个代码暂未上传，因为electron目前还不是特别熟
```javascript
document.getElementById('sendMsg').addEventListener('click', () => {
    let context = document.getElementById('context').value;
    let roomDom = document.getElementById('chatroom');

    let para = document.createElement("p");
    let node = document.createTextNode(context);
    para.appendChild(node);
    roomDom.appendChild(para);

    window.myApi.handleSendMsg(context);

})

const handleSendMsg = async (msg) => {
  ipcRenderer.invoke('sendMsg-event', msg);
}

contextBridge.exposeInMainWorld('myApi', {
  handleLogin,
  handleSendMsg
})


ipcMain.handle('login-event', (event, msg) => {
  console.log(msg);
  win.setSize(400, 500);
  win.center();
  socket = net.createConnection({
    host: 'localhost',
    port: 9999 // 你的服务器端口号
  }, () => {
    socket.write('Hello, server!');
  });
  return msg + "登录成功";
})

ipcMain.handle('sendMsg-event', (event, msg) => {
  socket.write(msg);
})

```


> 注意项

* 由于socket是阻塞io，所以单线程中只能一个个阻塞获取连接。单线程处理连接数会造成连接瓶颈。
* 获取到socket链接后，由于做的是IM应用，所以要一直持有这个socket，不能主动释放掉，所以是长连接，所以这里每获取到一个连接又要开启新的线程去处理具体的数据处理。因为线程资源宝贵
  ，所以采用线程池处理。但是线程池要注意，因为线程池是达到核心线程数后会将其放入到队列中，队列满了才会扩大到最大核心线程数，所以假如连接数到达了核心线程数的时候，后续再进来的连接请求就会
  被放入队列，这部分如果处理中的线程一直不被释放的话，会一直阻塞在队列中。所以需要提前知晓这里的坑，但是因为如果一直无限开线程的话肯定会造成OOM，所以需要控制好这个阈值。目前只设置了100个线程。
* 采用了Data Input/Output Stream进行数据写入和写出的话，最终是个String格式，但是我最终是要转成Msg对象，所以这里相当于构造对象，然后将对象转换成String，然后写入io流，然后对端又将io流读取出String，
最终按某个格式又将String转换成Msg对象，这中间对象和String的互相转换其实是可以避免的，直接采用Object Input/Output Stream将对象直接序列化到数据流。避免了中间商转换。所以这里又引出了一层序列化的概念，
要序列化的对象必须要实现Serializable接口。
* socket在进行read和write的时候，如果连接没有断且没有数据，会一直阻塞在对应API中。
* 虽然抽象出了session，并将其放入了ThreadLocalMap，但是还少了客户端主动断开或因网络原因无法与服务端通信的处理，因为网络环境是非常复杂的，无法仅仅通过SocketException去判断
客户端断连且将在线用户从内存中删除。因采取心跳方式进行处理。
* 有个要注意的点，由于我模拟客户端的测试写的是write和read在同一个线程中，客户端读取完数据后就阻塞再scanner.nextLine那里，所以一直无法执行后续的read操作。
所以这里客户端的读取数据要另开线程去操作，同样也是轮询操作。当read时，线程阻塞在read中。

  


