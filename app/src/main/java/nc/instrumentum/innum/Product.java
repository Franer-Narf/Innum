package nc.instrumentum.innum;

public class Product {

    private int id;
    private int idList;

    private String obj;

    private int num;

    Product(){
    }

    Product(int z, String a, int b, int y) {
        this.id = z;
        this.obj = a;
        this.num = b;
        this.idList = y;
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

    public int getIdList() {
        return this.idList;
    }

    public void setIdList(int idList) {
        this.idList = idList;
    }

    @Override
    public String toString(){
        return this.obj + "   x" + this.num;
    }
}
