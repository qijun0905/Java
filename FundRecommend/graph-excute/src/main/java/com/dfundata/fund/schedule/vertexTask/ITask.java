package com.dfundata.fund.schedule.vertexTask;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
public interface ITask {

    @Async
    ListenableFuture<DirectedAcyclicGraph<String, DefaultEdge>> execute(String taskId
            , Integer initDate
            , String procedure
            , DirectedAcyclicGraph<String, DefaultEdge> graph);
}
