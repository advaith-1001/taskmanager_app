package com.adv.taskmanager.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskComparisonDto {
    private List<Long> lastYear;
    private List<Long> thisYear;
    private List<String> months;
}
