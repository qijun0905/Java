package com.dfundata.fund.schedule.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.HashSet;
import java.util.Set;

/**
 * 2020/10/28 9:28 上午
 *
 * @author Seldom
 */
public class ReadGraphUtils {

    public DirectedAcyclicGraph<String, DefaultEdge> readGraph(String filePath){
        String file =
                FileUtils.readFile(filePath);
        JSONObject obj = JSONObject.parseObject(file);
        // 初始化graph
        DirectedAcyclicGraph<String, DefaultEdge>  graph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        // 添加顶点
        JSONArray vertexs = obj.getJSONArray("vertexs");
        for (Object vertex : vertexs) {
            String vertexStr = (String)vertex;
            graph.addVertex(vertexStr);
        }
        // 添加边的关系
        JSONArray edges = obj.getJSONArray("edges");
        for (Object edge : edges) {
            String edgeStr = (String)edge;
            String[] itemEdges = edgeStr.split("->");
            graph.addEdge(itemEdges[0],itemEdges[1]);
        }
        return graph;
    }



}
