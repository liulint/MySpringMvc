package com.testSpringMvc;

import com.spring.MyApplicationContext;
import com.spring.annotation.RequestMapping;
import com.spring.annotation.RequestMethod;
import com.spring.bean.BeanDefinition;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: ly
 * @Package: com.springmvc.annotation
 * @Project: mySpring
 * @name: MyDispatcherServlet
 * @Date:2024/1/27 22:48
 */
public class MyDispatcherServlet extends HttpServlet {

    private Map<String, Method> handlerMapping = new HashMap<>();

    private Map<String, RequestMethod[]> requestMethodsMap = new HashMap<>();
    private Map<String, Object> controllerMap  =new HashMap<>();
    MyApplicationContext myApplicationContext;
    @Override
    public void init() throws ServletException {
        myApplicationContext = new MyApplicationContext(SpringConfig.class);
        //初始化HandlerMapping(将url和method对应上)
        initHandlerMapping();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        try {
            //处理请求
            String s = doDispatch(req, resp);
            resp.getWriter().write(s);
        } catch (Exception e) {
            resp.getWriter().write("500!! Server Exception");
        }

    }




    private String doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if(handlerMapping.isEmpty()){
            return "";
        }
        String reqMethod = req.getMethod();
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();

        url=url.replace(contextPath, "").replaceAll("/+", "/");

        if(!this.handlerMapping.containsKey(url)){
            return "404 NOT FOUND!";
        }

        RequestMethod[] requestMethods = requestMethodsMap.get(url);
        Optional<RequestMethod> reqMethodResult = Arrays.stream(requestMethods).filter(requestMethod -> requestMethod.toString().equals(reqMethod)).findFirst();
        if(!reqMethodResult.isPresent()){
            return "Request method not supported";
        }

        Method method =this.handlerMapping.get(url);

        //获取方法的参数列表
        Class<?>[] parameterTypes = method.getParameterTypes();

        //获取请求的参数
        Map<String, String[]> parameterMap = req.getParameterMap();

        //保存参数值
        Object [] paramValues= new Object[parameterTypes.length];

        //方法的参数列表
        for (int i = 0; i<parameterTypes.length; i++){
            //根据参数名称，做某些处理
            String requestParam = parameterTypes[i].getSimpleName();


            if (requestParam.equals("HttpServletRequest")){
                //参数类型已明确，这边强转类型
                paramValues[i]=req;
                continue;
            }
            if (requestParam.equals("HttpServletResponse")){
                paramValues[i]=resp;
                continue;
            }
//            if(requestParam.equals("String")){
//                for (Entry<String, String[]> param : parameterMap.entrySet()) {
//                    String value =Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
//                    paramValues[i]=value;
//                }
//            }
        }
        //利用反射机制来调用
        Object invoke = null;
        try {
            invoke = method.invoke(this.controllerMap.get(url), paramValues);//第一个参数是method所对应的实例 在ioc容器中
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoke.toString();
    }

    private void initHandlerMapping(){
        Map<String, BeanDefinition> controllerBeanDefinitionMap = myApplicationContext.controllerBeanDefinitionMap;
        if(controllerBeanDefinitionMap.isEmpty()){
            return;
        }
        try {
            for (Map.Entry<String, BeanDefinition> entry: controllerBeanDefinitionMap.entrySet()) {
                BeanDefinition beanDefinition = entry.getValue();
                Class<? extends Object> clazz = beanDefinition.getType();
                //拼url时,是controller头的url拼上方法上的url
                String baseUrl ="";
                if(clazz.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);
                    baseUrl=annotation.value();
                }
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if(!method.isAnnotationPresent(RequestMapping.class)){
                        continue;
                    }
                    RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                    String url = annotation.value();
                    RequestMethod[] requestMethods = annotation.method();
                    url =(baseUrl+"/"+url).replaceAll("/+", "/");
                    handlerMapping.put(url,method);
                    Object instance = myApplicationContext.createBean(beanDefinition, entry.getKey());
                    controllerMap.put(url,instance);
                    requestMethodsMap.put(url,requestMethods);
                    System.out.println(url+","+method);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
