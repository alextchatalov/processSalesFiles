package com.processSalesFiles.salesFile.builders;

import java.math.BigDecimal;

public class ItemSaleBuilder {

    private Long id;
    private String item;
    private int qualyItem;
    private BigDecimal price;
    private SalesmanBuilder salesmanBuilder;

    private ItemSaleBuilder(Builder builder) {
        this.id = builder.id;
        this.item = builder.item;
        this.qualyItem = builder.qualyItem;
        this.price = builder.price;
        this.salesmanBuilder = builder.salesmanBuilder;
    }

    public static class Builder {
        private Long id;
        private String item;
        private int qualyItem;
        private BigDecimal price;
        private SalesmanBuilder salesmanBuilder;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        public Builder item(String item) {
            this.item = item;
            return this;
        }
        public Builder qualyItem(int qualyItem) {
            this.qualyItem = qualyItem;
            return this;
        }
        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder salesman(SalesmanBuilder salesmanBuilder) {
            this.salesmanBuilder = salesmanBuilder;
            return this;
        }

        public ItemSaleBuilder builder() {
            return new ItemSaleBuilder(this);
        }
    }

    public Long getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public int getQualyItem() {
        return qualyItem;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
