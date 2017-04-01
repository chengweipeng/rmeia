package com.ism.rmeia.server.ctrl;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class PriceInfo {
    public PriceInfo() {

    }

    public PriceInfo(String date, float openPrice, float closePrice) {
        this.date = date;
        this.open = openPrice;
        this.close = closePrice;
    }

    String date;
    float open;
    float close;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public byte[] toBytes() {
        ByteBuffer bb = ByteBuffer.allocate(64);
        // date must be yyyyMMdd
        bb.putInt(Integer.parseInt(date));
        bb.putFloat(open);
        bb.putFloat(close);
        return Arrays.copyOf(bb.array(), bb.position());
    }

    public void fromBytes(byte[] buf, int off, int len) {
        ByteBuffer bb = ByteBuffer.wrap(buf, off, len);
        date = String.valueOf(bb.getInt());
        open = bb.getFloat();
        close = bb.getFloat();
    }
}
