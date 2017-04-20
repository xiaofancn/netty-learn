
package com.fansxnet.netty.memcached;

import java.util.Random;
import java.util.concurrent.ExecutionException;

import io.netty.handler.codec.memcache.binary.FullBinaryMemcacheResponse;
import io.netty.util.CharsetUtil;

public class App {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		NioSession session = new NioSession("127.0.0.1", 11211);
		while (true) {
			FullBinaryMemcacheResponse res = null;
			String keyString = "user_id:" + new Random().nextInt(10000);
			session.send(keyString, keyString);
			res = (FullBinaryMemcacheResponse) session.get(keyString);
			System.out.println(keyString + " = " + res.content().toString(CharsetUtil.UTF_8));
		}
	}
}
