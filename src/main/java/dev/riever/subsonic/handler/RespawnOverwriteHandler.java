package dev.riever.subsonic.handler;

import dev.riever.subsonic.protocol.VelocityInternals;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class RespawnOverwriteHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf buf) {
            try {
                tryOverwriteAndPass(ctx, buf, promise);
            } finally {
                buf.release();
            }
        } else {
            ctx.write(msg, promise);
        }
    }

    private void tryOverwriteAndPass(ChannelHandlerContext ctx, ByteBuf buf, ChannelPromise promise) {
        if (VelocityInternals.isRespawnPacket(ctx.channel(), buf) && buf.readableBytes() >= 1 + Integer.BYTES) {
            Integer dimId = ctx.channel().attr(JoinGameAppendHandler.GTNH_DIM_ID).getAndSet(null);
            if (dimId != null) {
                buf.setInt(buf.readerIndex() + 1, dimId);
            }
        }
        ctx.write(buf.retain(), promise);
    }
}
