package com.fansxnet.netty.memcached;


import java.util.concurrent.ExecutionException;

import io.netty.handler.codec.memcache.binary.BinaryMemcacheRequest;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheResponse;

public class Cmd {
	private BinaryMemcacheRequest requ;
	private final FutureImpl<BinaryMemcacheResponse> fut = new FutureImpl<BinaryMemcacheResponse>();
	
	 

	public BinaryMemcacheRequest getRequ() {
		return requ;
	}

	public BinaryMemcacheResponse getResp() throws InterruptedException, ExecutionException {
		return fut.get();

	}

	public void setRequ(BinaryMemcacheRequest requ) {
		this.requ = requ;
	}

	public void setResp(BinaryMemcacheResponse resp) {
		fut.setResult(resp);
	}

}
