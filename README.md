# Rocker - Android app to control raspberry pi car

![](https://img.shields.io/github/release/sikuquanshu123/rocker.svg)  ![](https://img.shields.io/badge/language-java-orange.svg)    

基于socket，手机作为客户端发送指令，树莓派作为服务器接受到指令后，通过GPIO接口向电机发送电信号来驱动电机运转。    

[Home Page](http://qiangge.me/articles/2017/06/19/raspberry-pi-car.html) |    
## Code  
- socket server  
```  
import socket
import socketserver
import gpio

class ThreadedTCPRequestHandler(socketserver.BaseRequestHandler):
    def handle(self):
        while True:
            data = (self.request.recv(1024).decode('utf-8'))
            # print("raw:" + str(data))
            if not data:
                break
            data = data.replace('\n', '').replace(' ', '')
            response = gpio.ctrl_id(data) + "\n"
            print("post:" + str(data))
            print("response:" + str(response))
            self.request.sendall(response.encode('utf-8'))

if __name__ == "__main__":
    # Port 0 means to select an arbitrary unused port
    HOST, PORT = "0.0.0.0", 20000
    timeOut = 6  # 设置超时时间变量
    server = socketserver.TCPServer((HOST, PORT), ThreadedTCPRequestHandler)
    server.serve_forever()

```  
- Android client  
```  
// 获取 Client 端的输出/输入流
PrintWriter out = null;
try {
    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
} catch (IOException e) {
    Log.i("CtrlSocketClient", "outexcept:" + e);
    e.printStackTrace();
}
// 填充信息
assert out != null;
out.println(info);
Log.i("CtrlSocketClient", "send :" + info);
BufferedReader br;
try {
    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    msg = br.readLine();
    //Log.i("CtrlSocketClient", "recv :" + msg);


} catch (IOException e) {
    e.printStackTrace();
}
 ```  
 ## Screenshots
 ![]()  
 ## ChangeLog
 [ChangeLog](https://github.com/sikuquanshu123/rocker/releases)
