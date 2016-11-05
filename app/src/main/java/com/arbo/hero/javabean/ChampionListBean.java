package com.arbo.hero.javabean;

import java.util.List;

/**
 * Created by Administrator on 2016/8/11.
 */
public class ChampionListBean {

    private List<AllBean> all;

    public List<AllBean> getAll() {
        return all;
    }

    public void setAll(List<AllBean> all) {
        this.all = all;
    }

    public static class AllBean {
        private String enName;
        private String cnName;
        private String title;
        private String tags;
        private String rating;
        private String location;
        private String price;

        public String getEnName() {
            return enName;
        }

        public void setEnName(String enName) {
            this.enName = enName;
        }

        public String getCnName() {
            return cnName;
        }

        public void setCnName(String cnName) {
            this.cnName = cnName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
}
