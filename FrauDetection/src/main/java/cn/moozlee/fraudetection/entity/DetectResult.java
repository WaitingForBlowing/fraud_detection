package cn.moozlee.fraudetection.entity;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("detect_result")
public class DetectResult {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer recordId;
    private String firm;
    private String period;
    private Integer modelType;
    private Integer dataType;
    private Integer result;
    private Integer accuracy;
    private LocalDateTime uploadTime;

    public DetectResult(Integer recordId, String firm, String period, Integer modelType, Integer dataType, Integer result) {
        this.recordId = recordId;
        this.firm = firm;
        this.period = period;
        this.modelType = modelType;
        this.dataType = dataType;
        this.result = result;
    }
}

