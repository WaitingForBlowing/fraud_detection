package cn.moozlee.fraudetection.type_enum;

/**
 * @author moozlee
 * @create 2023-05-08 15:54
 */
public enum DetectState {
    COMMIT(0, "已提交"),
    SUCCESS(1, "检测完成"),
    FAILED(2, "检测失败");

    private Integer id;
    private String des;

    DetectState(Integer id, String des) {
        this.id = id;
        this.des = des;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
