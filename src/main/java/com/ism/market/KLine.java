package com.ism.market;

import java.io.Serializable;

@SuppressWarnings(value = {"serial"})
public class KLine implements Serializable {

    public float ADJUST;//复权因子
    public float MA;
    public float CLOSE;
    public float DIF;
    public float DEA;
    public float EMA12;
    public float EMA26;
    public int ts;
    public KLine(){

    }

    @Override
    public String toString() {
        return "{" +
            "ADJUST=" + ADJUST +
            ", MA=" + MA +
            ", CLOSE=" + CLOSE +
            ", DIF=" + DIF +
            ", DEA=" + DEA +
            ", EMA12=" + EMA12 +
            ", EMA26=" + EMA26 +
            ", ts=" + ts +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KLine kLine = (KLine) o;

        if ((kLine.ADJUST - ADJUST) > 0.01f) return false;
        if ((kLine.MA- MA) > 0.001) return false;
        if ((kLine.CLOSE - CLOSE) > 0.001f) return false;
        if ((kLine.DIF - DIF) > 0.0005f) return false;
        if ((kLine.DEA - DEA) > 0.0005f) return false;
        if ((kLine.EMA12 - EMA12) >0.0005f) return false;
        return (kLine.EMA26 - EMA26) <0.0005f;

    }

    @Override
    public int hashCode() {
      return Float.hashCode(CLOSE);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        KLine o = new KLine();
        o.ADJUST = ADJUST;
        o. MA = MA;
        o. CLOSE = CLOSE;
        o. DIF =DIF;
        o. DEA =DEA;
        o. EMA12 =EMA12;
        o. EMA26 =EMA26;
        return o;
    }
}
