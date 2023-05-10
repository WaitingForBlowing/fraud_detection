package cn.moozlee.fraudetection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author moozlee
 * @create 2023-05-09 16:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponse {
    private Integer code;
    private String message;
    private Object data;
}
