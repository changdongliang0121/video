package com.dahuaboke.signaling.util;

/**
 * author: Jiaojp
 * date: 2025/3/27
 */
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

public class NettyHttpClientUtil {
    private static final String HTTP_SCHEME = "http";
    private static final String HTTPS_SCHEME = "https";

    public static String sendGetRequest(String url) throws InterruptedException, URISyntaxException, SSLException {
        return sendRequest("GET", url, null);
    }

    public static String sendPostRequest(String url, String jsonBody) throws InterruptedException, URISyntaxException, SSLException {
        return sendRequest("POST", url, jsonBody);
    }

    private static String sendRequest(String method, String url, String jsonBody) throws InterruptedException, URISyntaxException, SSLException {
        URI uri = new URI(url);
        String scheme = uri.getScheme() == null? HTTP_SCHEME : uri.getScheme();
        String host = uri.getHost() == null? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if (HTTP_SCHEME.equalsIgnoreCase(scheme)) {
                port = 80;
            } else if (HTTPS_SCHEME.equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        final boolean ssl = HTTPS_SCHEME.equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        final CountDownLatch latch = new CountDownLatch(1);
        final StringBuilder responseContent = new StringBuilder();
        try {
            Bootstrap b = new Bootstrap();
            int finalPort = port;
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc(), host, finalPort));
                            }
                            p.addLast(new HttpClientCodec());
                            p.addLast(new HttpObjectAggregator(1048576));
                            p.addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) throws Exception {
                                    responseContent.append(response.content().toString(io.netty.util.CharsetUtil.UTF_8));
                                    latch.countDown();
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                    ctx.close();
                                    latch.countDown();
                                }
                            });
                        }
                    });

            ChannelFuture f = b.connect(host, port).sync();
            FullHttpRequest request;
            if ("GET".equalsIgnoreCase(method)) {
                request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
            } else if ("POST".equalsIgnoreCase(method)) {
                request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
                if (jsonBody != null && !jsonBody.isEmpty()) {
                    request.content().writeBytes(jsonBody.getBytes());
                    request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
                    request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
                }
            } else {
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            f.channel().writeAndFlush(request);
            latch.await();
            return responseContent.toString();
        } finally {
            group.shutdownGracefully();
        }
    }
}
