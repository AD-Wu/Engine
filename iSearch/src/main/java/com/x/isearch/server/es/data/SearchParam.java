package com.x.isearch.server.es.data;

import com.google.common.base.Objects;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author AD
 * @date 2022/3/5 18:09
 */
public class SearchParam {

    // ------------------------ 成员变量 ------------------------

    private String[] indices;
    private String searchValue;
    private SearchColumn[] searchColumns;
    private int page = 0;
    private int size = 10;

    private SearchFilter[] searchFilters;
    private ColumnFilter columnFilter;
    private Highlight highLight;

    // ------------------------ 构造方法 ------------------------
    public SearchParam() {}

    public SearchParam(String[] indices, String searchValue, String... searchColumns) {
        this(indices,
             Arrays.stream(searchColumns).distinct().map(sc -> new SearchColumn(sc)).collect(Collectors.toList())
                 .toArray(new SearchColumn[0]),
             searchValue);
    }

    public SearchParam(String[] indices, SearchColumn[] searchColumns, String searchValue) {
        this.indices = indices;
        this.searchColumns = searchColumns;
        this.searchValue = searchValue;
        init();
    }

    // ------------------------ 成员方法  ------------------------

    private void init() {
        // 设置搜索过滤
        this.searchFilters = new SearchFilter[0];
        // 设置字段排除
        this.columnFilter = new ColumnFilter();
        // 设置高亮
        String[] columns = new String[searchColumns.length];
        for (int i = 0, c = searchColumns.length; i < c; i++) {
            columns[i] = searchColumns[i].getColumn();
        }
        this.highLight = new Highlight();
        this.highLight.setColumns(columns);
    }

    public String[] getIndices() {
        return indices;
    }

    public SearchColumn[] getSearchColumns() {
        return searchColumns;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setIndices(String[] indices) {
        this.indices = indices;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public void setSearchColumns(SearchColumn[] searchColumns) {
        this.searchColumns = searchColumns;
    }

    public ColumnFilter getColumnFilter() {
        return columnFilter;
    }

    public void setColumnFilter(ColumnFilter columnFilter) {
        this.columnFilter = columnFilter;
    }

    public Highlight getHighLight() {
        return highLight;
    }

    public void setHighLight(Highlight highLight) {
        this.highLight = highLight;
    }

    public SearchFilter[] getSearchFilters() {
        return searchFilters;
    }

    public void setSearchFilters(SearchFilter[] searchFilters) {
        this.searchFilters = searchFilters;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    // ------------------------ 内部类 ------------------------

    public static class SearchColumn {

        /**
         * 列名
         */

        private String column;
        /**
         * 权重
         */
        private float boost = 1;

        public SearchColumn() {}

        public SearchColumn(String column) {
            this.column = column;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public float getBoost() {
            return boost;
        }

        public void setBoost(float boost) {
            this.boost = boost;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SearchColumn that = (SearchColumn) o;
            return Objects.equal(column, that.column);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(column);
        }
    }

    /**
     * 搜索过滤器
     * 1.同个索引,多个字段之间是"与"关系
     * 2.同个索引,同一个字段之间,值是"或"关系
     * 3.多个索引,索引间是"或"关系
     */
    public static class SearchFilter {

        /**
         * 字段名
         */
        private String column;
        /**
         * 字段值,多个值表示进行"或"搜索
         */
        private Object[] value;


        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public Object[] getValue() {
            return value;
        }

        public void setValue(Object[] value) {
            this.value = value;
        }
    }

    public static class ColumnFilter {

        /**
         * 包含字段
         */
        private String[] includes;
        /**
         * 过滤字段
         */
        private String[] excludes;

        public String[] getIncludes() {
            return includes;
        }

        public void setIncludes(String[] includes) {
            this.includes = includes;
        }

        public String[] getExcludes() {
            return excludes;
        }

        public void setExcludes(String[] excludes) {
            this.excludes = excludes;
        }
    }

    public static class Highlight {

        /**
         * 高亮字段名数组
         */
        private String[] columns;
        /**
         * 片段大小
         */
        private int fragmentSize = 100;
        /**
         * 最大片段数
         */
        private int maxFragmentCount = 3;
        /**
         * 片段开始到高亮的偏移
         */
        private int fragmentOffset = -1;
        /**
         * 高亮前置标签
         */
        private String preTags = "<strong style=\"color: red;\">";
        /**
         * 高亮后置标签
         */
        private String postTags = "</strong>";

        public String[] getColumns() {
            return columns;
        }

        public void setColumns(String[] columns) {
            this.columns = columns;
        }

        public int getFragmentSize() {
            return fragmentSize;
        }

        public void setFragmentSize(int fragmentSize) {
            this.fragmentSize = fragmentSize;
        }

        public int getMaxFragmentCount() {
            return maxFragmentCount;
        }

        public void setMaxFragmentCount(int maxFragmentCount) {
            this.maxFragmentCount = maxFragmentCount;
        }

        public int getFragmentOffset() {
            return fragmentOffset;
        }

        public void setFragmentOffset(int fragmentOffset) {
            this.fragmentOffset = fragmentOffset;
        }

        public String getPreTags() {
            return preTags;
        }

        public void setPreTags(String preTags) {
            this.preTags = preTags;
        }

        public String getPostTags() {
            return postTags;
        }

        public void setPostTags(String postTags) {
            this.postTags = postTags;
        }
    }
}
