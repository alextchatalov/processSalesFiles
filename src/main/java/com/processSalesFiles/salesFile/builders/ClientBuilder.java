package com.processSalesFiles.salesFile.builders;

public class ClientBuilder {

    private String cnpj;
    private String name;
    private String businessArea;

    public ClientBuilder(Builder builder) {
        this.cnpj = builder.cnpj;
        this.name = builder.name;
        this.businessArea = builder.businessArea;
    }

    public static class Builder {

        private String cnpj;
        private String name;
        private String businessArea;

        public Builder cnpj(String cnpj) {
            this.cnpj = cnpj;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder businessArea(String businessArea) {
            this.businessArea = businessArea;
            return this;
        }

        public ClientBuilder build() {
            return new ClientBuilder(this);
        }


    }
}
