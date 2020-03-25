package com.qfeditor.interview;

/**
 * @ClassName AbstractFactory
 * @Description <p>
 *              抽象工厂模式
 *              提供一个创建一系列相关或相互依赖对象的接口,而无需指定他们具体的类
 *              <p/>
 * @Author yuxiang
 * @Date 2020/3/25 17:32
 **/
public class AbstractFactoryDemo {
    public static void main(String[] args) {
        IDataBaseUtils iDataBaseUtils = new MysqlDataBaseUtils();
        IConnection connection = iDataBaseUtils.getConnection();
        connection.connect();
        ICommand command = iDataBaseUtils.getCommand();
        command.command();
    }
}

interface IConnection{
    void connect();
}

interface ICommand{
    void command();
}

interface IDataBaseUtils{
    IConnection getConnection();
    ICommand getCommand();
}

class MysqlConnection implements IConnection{

    @Override
    public void connect() {
        System.out.println("mysql connected");
    }
}

class MysqlCommand implements ICommand{

    @Override
    public void command() {
        System.out.println("mysql command");
    }
}

class MysqlDataBaseUtils implements IDataBaseUtils{

    @Override
    public IConnection getConnection() {
        return new MysqlConnection();
    }

    @Override
    public ICommand getCommand() {
        return new MysqlCommand();
    }
}
