package jp.levtech.rookie.tutorial.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Todo {
    private int id;       // ← 追加
    private String date;
    private String title;
    private String memo;
}