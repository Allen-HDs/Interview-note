package com.qfeditor.interview;

/**
 * @ClassName FactoryMethod
 * @Description <p>
 *              定义一个用于创建对象的接口, 让子类决定实现哪一个类.Factory Method 使得一个类的实例化延迟到子类
 *              应用场景:
 *              1.当你不知道该使用对象的确切类型的时候
 *              2.当你希望为库或框架提供扩展其内部组件的方法时
 *              主要优点:
 *              1.将具体产品和创建者解耦
 *              2.符合单一指责原则
 *              3.符合开闭原则
 *              <p/>
 *
 * @Author yuxiang
 * @Date 2020/3/25 15:06
 **/
public class FactoryMethod {
    public static void main(String[] args) {
        SimplePhoneFactory simplePhoneFactory = new SimplePhoneFactory();
        Phone iPhone = simplePhoneFactory.makePhone("IPhone");
        iPhone.make();

        AbstractFactory miFactory = new XiaoMiFactory();
        Phone phone = miFactory.makePhone();
        phone.make();


    }
}

interface Phone {
    void make();
}

class MiPhone implements Phone {

    @Override
    public void make() {
        System.out.println("make MiPhone...");
    }
}

class IPhone implements Phone {

    @Override
    public void make() {
        System.out.println("make IPhone...");
    }
}

/**
 * 简单工厂模式
 */
class SimplePhoneFactory{
    public Phone makePhone(String phoneType) {
        if(phoneType.equalsIgnoreCase("MiPhone")){
            return new MiPhone();
        }
        else if(phoneType.equalsIgnoreCase("iPhone")) {
            return new IPhone();
        }
        return null;
    }
}

/**
 * 工厂方法模式
 */
interface AbstractFactory{
    Phone makePhone();
}

class XiaoMiFactory implements AbstractFactory{
    @Override
    public Phone makePhone() {
        return new MiPhone();
    }
}

class AppleFactory implements AbstractFactory{
    @Override
    public Phone makePhone() {
        return new IPhone();
    }
}


abstract class PhoneFactory {
    abstract Phone createPhone();

    Phone getObject() {
        Phone product = createPhone();
        return product;
    }
}


class CreateMiPhone extends PhoneFactory {

    @Override
    Phone createPhone() {
        //详细业务
        return new MiPhone();
    }
}

class CreateIPhone extends PhoneFactory {

    @Override
    Phone createPhone() {
        //详细业务
        return new IPhone();
    }
}