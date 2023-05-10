package cn.moozlee.fraudetection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QueryDto {
    private String query;
    private Integer pageNumber;
    private Integer pageSize;
}
