package cn.moozlee.fraudetection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author moozlee
 * @create 2023-05-06 15:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("ml_model")
public class MlModel {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String url;
    private String auc;
    private String ndcg;

    public MlModel(String name, String url) {
        this.name = name;
        this.url = url;
    }
}

