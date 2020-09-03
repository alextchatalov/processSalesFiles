package com.processSalesFiles.salesFile.builders;

import java.util.List;

public class SaleBuilder {

    private Long id;
    private List<ItemSaleBuilder> itens;
    private SalesmanBuilder salesmanBuilder;

    private SaleBuilder(Builder builder) {
        this.id = builder.id;
        this.itens = builder.itens;
        this.salesmanBuilder = builder.salesmanBuilder;
    }

    public static class Builder {
        private Long id;
        private List<ItemSaleBuilder> itens;
        private SalesmanBuilder salesmanBuilder;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder itemBuilder(List<ItemSaleBuilder> itens) {
            this.itens = itens;
            return this;
        }

        public Builder salesmanBuilder(SalesmanBuilder salesmanBuilder) {
            this.salesmanBuilder = salesmanBuilder;
            return this;
        }

        public SaleBuilder build() {
            return new SaleBuilder(this);
        }
    }

    public Long getId() {
        return id;
    }

    public List<ItemSaleBuilder> getItens() {
        return itens;
    }

    public SalesmanBuilder getSalesmanBuilder() {
        return salesmanBuilder;
    }
}
