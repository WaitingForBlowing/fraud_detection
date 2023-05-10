package cn.moozlee.fraudetection.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author moozlee
 * @create 2023-05-08 15:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("detect_record")
public class DetectRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer dataType;
    private Integer modelType;
    private String dataFile;
    private String resultFile;
    private Integer state;
    private LocalDateTime uploadTime;

    public DetectRecord(Integer dataType, Integer modelType, String dataFile, Integer state) {
        this.dataType = dataType;
        this.modelType = modelType;
        this.dataFile = dataFile;
        this.state = state;
    }
}

