package io.sugo.pio.engine.bbs;

/**
 */
public class BbsResult {
    private String[] items;
    private String[] articles;

    public BbsResult(String[] items, String[] articles){
        this.items = items;
        this.articles = articles;
    }

    public String[] getItems() {
        return items;
    }

    public String[] getArticles() {
        return articles;
    }
}
