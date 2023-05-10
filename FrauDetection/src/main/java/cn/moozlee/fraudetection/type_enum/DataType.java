package cn.moozlee.fraudetection.type_enum;

/**
 * @author moozlee
 * @create 2023-05-08 15:47
 */
public enum DataType {
    RAW(0, "原始数据"),
    RATE(1, "财务比率");


    private int id;
    private String des;

    DataType(int id, String des) {
        this.id = id;
        this.des = des;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
