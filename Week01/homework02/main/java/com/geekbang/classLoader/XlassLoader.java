package com.geekbang.classLoader;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class XlassLoader extends ClassLoader{

    public static void main(String[] args) throws Exception {
        //相关参数
        final String className = "Hello";
        final String methodName = "hello";
        //创建类加载器
        ClassLoader classLoader = new XlassLoader();
        //加载类，loadClass加载类会通过findClass查找是否存在该类
        Class<?> clazz = classLoader.loadClass(className);
        //创建对象
        Object obj = clazz.newInstance();
        //调用方法
        Method method = clazz.getMethod(methodName);
        method.invoke(obj);
    }

    /**
     * //重载findClass方法，loadClass加载类会通过findClass查找是否存在该类
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            //读取数据
            byte[] bytes = streamToBytes(name);
            //转换
            byte[] classBytes = decode(bytes);
            //通过底层定义此类
            return defineClass(name, classBytes, 0, classBytes.length);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    /**
     * 将resource文件转字节数组
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    private byte[] streamToBytes(String name) throws ClassNotFoundException {
        //若name中带有支持包名，则进行路径转化
        String resourcePath = name.replace(".", "/");
        //文件后缀
        final String suffix = ".xlass";
        //获取输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath + suffix);
        try {
            //读取数据
            int length = inputStream.available();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            return bytes;
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        } finally {
            close(inputStream);
        }
    }


    /**
     * 转码
     * @param bytes
     * @return
     */
    private byte[] decode(byte[] bytes) {
        byte[] targetArray = new byte[bytes.length];
        for (int i = 0; i < targetArray.length; i++){
            targetArray[i] = (byte) (255 - bytes[i]);
        }
        return targetArray;
    }

    /**
     * 关闭输入流
     * @param res
     */
    private static void close(Closeable res){
        if (null != res){
            try {
                res.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


