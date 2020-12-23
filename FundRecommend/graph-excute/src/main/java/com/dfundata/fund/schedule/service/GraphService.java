package com.dfundata.fund.schedule.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dfundata.fund.schedule.entity.GraphParams;
import com.dfundata.fund.schedule.utils.FileUtils;
import com.dfundata.fund.schedule.utils.H2Utils;
import com.dfundata.fund.schedule.utils.HiveDBUtils;
import com.dfundata.fund.schedule.vertexTask.ITask;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.*;

/**
 * 2020/10/15 10:10 上午
 *
 * @author Seldom
 */
@Service
public class GraphService {


    private Logger logger = LoggerFactory.getLogger(GraphService.class);

    @Value("${graph.json}")
    private String filePath;


    @Value("${schema.header}")
    private String header;

    DirectedAcyclicGraph<String, DefaultEdge> graph;

    JSONObject obj;

    Map<String,Object> map;
    // 顶点集合
    List<String> vList;
    // task任务的集合
    Map<String,JSONObject> taskMap;

    // 子节点的所有父节点的集合
    Map<String, Set<String>> vMap;

    private Set executing;

    @Autowired
    private HiveDBUtils hiveDBUtils;

    @Autowired
    private ApplicationContext applicationContext;

    public void executeCheck() throws Exception{
        // 1.先检查当前图是否合法
        boolean b = checkGraphEdge();
        if(!b){
            throw new RuntimeException("节点条件不满足，请先核查");
        }
        //
        if(vList!=null){
            H2Utils.executeSql("DROP TABLE IF EXISTS POT_STATUS;");
            H2Utils.executeSql("CREATE TABLE  POT_STATUS (task_id VARCHAR(10),status VARCHAR(4),err_code VARCHAR(10)," +
                    "err_msg VARCHAR(200) ,init_date int(10) )");

            for (String vertex:vList) {
                JSONObject o = (JSONObject)taskMap.get(vertex);
                String taskId = o.getString("taskId");
                String status = o.getString("status");
                if(StringUtils.isNotBlank(status) && "0".equals(status)){
                    String sql =  "insert into POT_STATUS (task_id,status,init_date) values ( '"+taskId+"' , "+
                            status+" , 20201001 )";
                    H2Utils.executeSql(sql);
                }
            }
        }
        executing = new HashSet<String>();
        scheduleTask(20201001,graph);

    }

    private void scheduleTask(Integer initDate, DirectedAcyclicGraph<String, DefaultEdge> graph){
        if (graph.vertexSet().size() == 0) {
            return;
        }
        Iterator iter = new TopologicalOrderIterator(graph);
        while (iter.hasNext()) {
            String task = (String) iter.next();
            if (!executing.contains(task) && graph.incomingEdgesOf(task).size() == 0) {
                executing.add(task);
                JSONObject taskObj = taskMap.get(task);
                String executeClass = taskObj.getString("executeClass");
//                JSONArray shellParam = taskObj.getJSONArray("shellParam");
                ITask iTask = (ITask) applicationContext.getBean(executeClass);
                String procedure = taskObj.getString("procedure");
                ListenableFuture<DirectedAcyclicGraph<String, DefaultEdge>> future =
                        iTask.execute(task, initDate, procedure, graph);
                future.addCallback(new ListenableFutureCallback<DirectedAcyclicGraph<String, DefaultEdge>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        logger.info("this time has failed");
                    }
                    @Override
                    public void onSuccess(DirectedAcyclicGraph<String, DefaultEdge> graph1) {
//                        scheduleTask(initDate,graph1);
                    }
                });
            }
        }
    }

    /**
     * 校验当前图是否合法
     * @return
     */
    public boolean checkGraphEdge(){
        vList = new ArrayList<>();
        vMap = new HashMap<>();
        String file =
                FileUtils.readFile(filePath);
        obj = JSONObject.parseObject(file);
        // 初始化graph
        graph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        // 添加顶点
        JSONArray vertexs = obj.getJSONArray("vertexs");
        for (Object vertex : vertexs) {
            String vertexStr = (String)vertex;
            graph.addVertex(vertexStr);
            vList.add(vertexStr);
        }
        // 添加边的关系
        JSONArray edges = obj.getJSONArray("edges");
        for (Object edge : edges) {
            String edgeStr = (String)edge;
            String[] itemEdges = edgeStr.split("->");
            graph.addEdge(itemEdges[0],itemEdges[1]);
            Set<String> strings = vMap.get(itemEdges[1]);
            if(strings == null){
                strings = new HashSet<String>();
            }
            strings.add(itemEdges[0]);
            vMap.put(itemEdges[1],strings);
        }
        // 获取每个节点的in-out
        JSONArray taskInfos = obj.getJSONArray("tasks");
        map = new HashMap<>();
        taskMap = new HashMap<>();
        for (Object task: taskInfos) {
            GraphParams params = new GraphParams();
            JSONObject jsonObject = (JSONObject) task;
            String taskId = jsonObject.getString("taskId");
            String input = jsonObject.getString("input").replaceAll("schema",header);
            String output = jsonObject.getString("output").replaceAll("schema",header);
            String status = jsonObject.getString("status");
            String sql = jsonObject.getString("sql");
            params.setInput(input);
            params.setOutput(output);
            params.setStatus(status);
            map.put(taskId,params);
            taskMap.put(taskId,jsonObject);
        }
        // 这里定义一个临时set，方便取出 差集
        Set<String> tempSet = new HashSet<>();
        // 对于map put元素之后，判断关系是否满足
        for (Object vertex : vertexs) {
            // 根据节点获取此节点的所有父节点
            Set<String> strings = vMap.get(vertex);
            if(strings != null){
                // 获取当前节点的所有父节点的输出并集
                Set<String> iSet = new HashSet<>();
                Set<String> oSet = new HashSet<>();
                StringBuffer stringBuffer = new StringBuffer();
                for (String s:strings
                ) {
                    stringBuffer.append(s).append(" ");
                    GraphParams o = (GraphParams) map.get(s);
                    GraphParams orz = (GraphParams) map.get(vertex);
                    String input = orz.getInput().replace("{","").replace("}","").replaceAll("schema",header);
                    String output = o.getOutput().replace("{","").replace("}","");
                    String[] spt = input.split(",");
                    String[] split = output.split(",");
                    for(String m: spt){
                        iSet.add(m);
                    }
                    // 将所有父节点的输出取并集
                    for (String k:split
                    ) {
                        oSet.add(k);
                    }
                }
                // 这里需要知道具体是哪一个父节点出现了 错误
                if(oSet.containsAll(iSet) && iSet.size() > 0){
                    try{
                        for(String keys : iSet){
                            String[] split = keys.split(".", 1);
                            String sql = "select count("+split[1]+") from " + split[0];
                            Long result = hiveDBUtils.getResult(sql);
                            if(result <= 0){
                                tempSet.clear();
                                tempSet.addAll(iSet);
                                tempSet.removeAll(oSet);
                                logger.error("节点"+vertex+"error;"+"前置条件字段缺失数据"+tempSet+";前置节点为"+stringBuffer);
                            }
                        }
                    }catch (Exception e){
                        logger.error(e.getMessage());
                    }
                    logger.info("节点"+vertex+"验证成功");
                }else{
                    tempSet.clear();
                    tempSet.addAll(iSet);
                    tempSet.removeAll(oSet);
                    logger.info("节点"+vertex+"error;"+"缺少前置条件"+tempSet+";前置节点为"+stringBuffer);
                    return false;
                }
            }else{
                logger.info(vertex+"是一个无前置条件的节点");
            }
        }
        return true;
    }

    private String[] jsonArrayToArray(JSONArray jsonArray){
        if(jsonArray == null){
            return null;
        }
        int size = jsonArray.size();
        String[] params = new String[size];
        for (int i = 0; i < jsonArray.size(); i++) {
            String param = jsonArray.getString(i);
            params[i] = param;
        }
        return params;
    }
}
