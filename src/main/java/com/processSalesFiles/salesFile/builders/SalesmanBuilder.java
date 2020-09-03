package com.processSalesFiles.salesFile.builders;

public class SalesmanBuilder {
    private String cpf;
    private String name;
    private String salary;

    private SalesmanBuilder(String cpf, String name, String salary) {
        this.cpf = cpf;
        this.name = name;
        this.salary = salary;
    }

    public SalesmanBuilder(Builder builder) {
        this.cpf = builder.cpf;
        this.name = builder.name;
        this.salary = builder.salary;
    }

    public static class Builder {
        private String cpf;
        private String name;
        private String salary;

        public Builder cpf(String cpf) {
            this.cpf = cpf;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder salary(String salary) {
            this.salary = salary;
            return this;
        }

        public SalesmanBuilder build(){
            return new SalesmanBuilder(this);
        }

    }

    public String getCpf() {
        return cpf;
    }

    public String getName() {
        return name;
    }

    public String getSalary() {
        return salary;
    }
}
