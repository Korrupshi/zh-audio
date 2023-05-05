package com.zhtools.generateAudio.extract;

public class Tuple<A, B, C> {
    public A start;
    public B end;
    public C sentence;

    public Tuple(A start, B end, C sentence) {
        this.start = start;
        this.end = end;
        this.sentence = sentence;
    }
}
