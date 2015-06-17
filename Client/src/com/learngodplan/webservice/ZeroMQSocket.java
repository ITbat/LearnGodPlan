package com.learngodplan.webservice;

import org.zeromq.ZMQ;

import android.util.Log;

public class ZeroMQSocket {
	private String request;
	private ZMQ.Socket socket;
	
	public ZeroMQSocket(String rq){
		request = rq;
	}
	
	public String SendRequest(){
		//创建一个I/O线程的上下文
		ZMQ.Context context = ZMQ.context(1); 
		//创建一个request类型的socket，这里可以将其简单的理解为客户端，用于向response端发送数据
		ZMQ.Socket socket = context.socket(ZMQ.REQ); 
		
		//与response端建立连接
		socket.connect("tcp://172.18.35.75:8001");
		socket.connect("tcp://172.18.35.75:8002");
		socket.connect("tcp://172.18.35.75:8003");
		
		socket.send(request.getBytes());   //向reponse端发送数据
		byte[] response = socket.recv();   //接收response发送回来的数据 ,正在request/response模型中，send之后必须要recv之后才能继续send
		
		Log.d("send mood response", new String(response));
        
		String buffer =  new String(response);
        socket.close(); 
		
		return buffer;
	}

	public ZMQ.Socket getSocket(){
		return socket;
	}
	
	

}
