package com.weisong.test.comm.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import com.weisong.test.comm.CHub;
import com.weisong.test.comm.codec.CCodec;
import com.weisong.test.comm.codec.CCodecFactory;
import com.weisong.test.comm.exception.CException;
import com.weisong.test.comm.pdu.CPdu;

abstract public class CBaseWebSocketProxy extends CBaseProxy {

	static public class WebSocketEndpointRemote implements EndpointRemote {
		static protected CCodec codec = CCodecFactory.getOrCreate();
		protected String address;
		protected ChannelHandlerContext ctx;
		protected WebSocketEndpointRemote(String address, ChannelHandlerContext ctx) {
			this.address = address;
			this.ctx = ctx;
		}
		@Override
		public String getAddress() {
			return address;
		}
		@Override
		public void publish(CPdu pdu) throws CException {
			ByteBuf bytes = Unpooled.wrappedBuffer(codec.encode(pdu));
			BinaryWebSocketFrame frame = new BinaryWebSocketFrame(bytes);
			ctx.channel().writeAndFlush(frame);
		}
	}
	
	public CBaseWebSocketProxy(CHub hub) throws Exception {
		super(hub);
	}
}
