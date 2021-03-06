package com.reply.codemasters.challenge.studentclash2017.replycalls.graph.utils;

import com.reply.codemasters.challenge.studentclash2017.replycalls.model.Office;
import lombok.RequiredArgsConstructor;
import org.jgrapht.VertexFactory;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class BipartiteVertexFactory implements VertexFactory<Office> {

    private final VertexFactory<? extends Office> firstPartitionFactory;

    private final VertexFactory<? extends Office> secondPartitionFactory;

    private final int numberOfFirst;

    private final AtomicInteger generated = new AtomicInteger(0);

    @Override
    public Office createVertex() {
        return this.generated.getAndIncrement() < numberOfFirst
                ? firstPartitionFactory.createVertex()
                : secondPartitionFactory.createVertex();
    }
}
