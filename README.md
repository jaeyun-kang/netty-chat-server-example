# netty-chat-server-example
Netty 연습 - 기초적인 채팅 프로그램 구현

## 관련 개념 정리
* bootstrap.bind()를 할 때, 설정에 따라 서버를 개설
* ChannelInitializer를 통해 SocketChannel을 초기화
* workerEventLoopGroup에 들어있는 eventloop들은 각각의 socketChannel을 관리하고, 이벤트 발생 시 연결된 pipeline의 첫 번째 핸들러에 넘겨줌.
* SocketChannel에 들어온 데이터는 ChannelInboundHandler를 통해 적절하게 처리되어 넘어옴
* 이후 write() 함수를 통해 ChannelOutboundHandler를 거쳐 적절하게 처리되어 SocketChannel을 통해 전송함

## Server
* Master(boss)와 worker EventLoopGroup을 각각 생성, bootstrap을 통해 그룹을 묶어줌
* channel은 소켓의 입출력모드를 설정하며, NIOServerSocketChannel을 사용
* channelGroup을 통해 연결된 client group들을 관리하며, 채팅이 들어올 때마다 이 안에 속한 모든 채널에 채팅을 전송함
* 소켓채널이 active될 때 channelGroup에 채널 추가
* 입력받은 채팅에 따라 channelGroup에서 채널 삭제

## Client
* 서버의 주소와 port를 통해 소켓채널을 연결
* 서버로부터 소켓을 통해 들어오는 데이터(채팅내용)를 그대로 write
* Scanner를 통해 입력받은 데이터(채팅내용)을 outboundhandler를 거쳐 sockerchannel를 통해 서버로 전송함
