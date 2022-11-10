package cn.flood.db.elasticsearch.repository;

import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * program: esdemo
 * description: 高亮对象封装
 * author: X-Pacific zhang
 * create: 2019-01-23 11:13
 **/
public class HighLight {
    private String preTag = "";
    private String postTag = "";
    private List<String> highLightList = null;
    private HighlightBuilder highlightBuilder = null;

    public HighLight(){
        highLightList = new ArrayList<>();
    }

    public HighLight field(String fieldValue){
        highLightList.add(fieldValue);
        return this;
    }

    public List<String> getHighLightList(){
        return highLightList;
    }

    public String getPreTag() {
        return preTag;
    }

    public void setPreTag(String preTag) {
        this.preTag = preTag;
    }

    public String getPostTag() {
        return postTag;
    }

    public void setPostTag(String postTag) {
        this.postTag = postTag;
    }

    public HighlightBuilder getHighlightBuilder() {
        return highlightBuilder;
    }

    public void setHighlightBuilder(HighlightBuilder highlightBuilder) {
        this.highlightBuilder = highlightBuilder;
    }
}
