package com.blogswebsite.params;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Data
@NoArgsConstructor
public class BlogListParams {
    private List<Map<String,Object>> listBlog;
    private int countBlog;
}
