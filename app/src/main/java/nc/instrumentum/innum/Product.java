package nc.instrumentum.innum;

public class Product {

    private int id;
    private String obj;

    private int num;

    Product(){
    }

    Product(int z, String a, int b) {
        this.id = z;
        this.obj = a;
        this.num = b;
    }

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return this.obj + "   x" + this.num;
    }
}
