package com.processSalesFiles.salesFile.threads;

import ch.qos.logback.core.util.FileUtil;
import com.processSalesFiles.salesFile.builders.ClientBuilder;
import com.processSalesFiles.salesFile.builders.ItemSaleBuilder;
import com.processSalesFiles.salesFile.builders.SaleBuilder;
import com.processSalesFiles.salesFile.builders.SalesmanBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.sound.midi.Patch;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SalesThread extends Thread  {

    private static final String REGISTER_SEPARATOR = "ç";
    private static final String REGISTER_SALESMAN = "001";
    private static final String REGISTER_CLIENT = "002";
    private static final String REGISTER_SALE = "003";
    private static final String BASE_DIR = "/home/alext/sales/";
    private static final String DIR_INPUT = BASE_DIR + "in";
    private static final String DIR_OUT = BASE_DIR + "out";
    private static final String DIR_PROCESSED = BASE_DIR + "processed";
    private List<SalesmanBuilder> salesmans = new ArrayList<>();
    private List<ClientBuilder> clients = new ArrayList<>();
    private List<SaleBuilder> sales = new ArrayList<>();
    private List<String> err = new ArrayList<>();
    private File input = new File(DIR_INPUT);
    private File outPut = new File(DIR_OUT);
    private File processed = new File(DIR_PROCESSED);
    public static boolean isDone = false;

    private void setup() {
        File baseDirectory = new File(BASE_DIR);

        if (!baseDirectory.exists()) {
            baseDirectory.mkdir();
            input.mkdir();
            outPut.mkdir();
            processed.mkdir();
        }
    }

    public void run() {
        setup();
        while (!isDone) {
            File[] files = input.listFiles();
            if (files != null && files.length > 0) {

                for (File file : files) {

                    if (file.isFile()) {
                        processFile(file);
                        generateReport(file.getName());
                        file.delete();
                    }

                }
            } else {
                try {
                    System.out.println("SLEEP");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void generateReport(String fileName)  {

        if (err.size() == 0) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(DIR_OUT + "/result.txt"))) {
                writer.write("Amount clients in file: " + fileName + "\n");
                writer.write(String.valueOf(clients.size()));
                writer.newLine();
                writer.write("Amount salesman in file: " + fileName + "\n");
                writer.write(String.valueOf(salesmans.size()));
                writer.newLine();
                writer.write("The max price in file: " + fileName + "\n");
                Long saleID = searchBestSale();
                writer.write(String.valueOf(saleID));
                writer.newLine();
                writer.write("The bad saleman in: " + fileName + "\n");
                String badSaleman = searchBadSaleman();
                writer.write(badSaleman);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            createErrorFile();
        }
        if (sales != null && !sales.isEmpty()) {
            for (SaleBuilder sale : sales) {
                System.out.println(sale.getId());
                System.out.println(sale.getSalesmanBuilder().getCpf());
            }
        }


    }

    private String searchBadSaleman() {
        Map<SaleBuilder, BigDecimal> salesAndTotalSold = new HashMap<>();
        for (SaleBuilder sale : sales) {
            BigDecimal totalSold = sale.getItens().stream().map(ItemSaleBuilder::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            salesAndTotalSold.put(sale, totalSold);
        }
        BigDecimal maxSold = BigDecimal.ZERO;
        String badSaleman = "";
        for (Map.Entry<SaleBuilder, BigDecimal> salesSum : salesAndTotalSold.entrySet()) {
            if (salesSum.getValue().compareTo(maxSold) < 0 || maxSold.compareTo(BigDecimal.ZERO) == 0) {
                maxSold = salesSum.getValue();
                badSaleman = salesSum.getKey().getSalesmanBuilder().getName();
            }
        }
        return badSaleman;
    }

    private Long searchBestSale() {
        BigDecimal maxPrice = BigDecimal.ZERO;
        Long saleId = 0L;
        for (SaleBuilder sale : sales) {
            BigDecimal maxPricePerSaleman = Collections.max(sale.getItens().stream().map(ItemSaleBuilder::getPrice).collect(Collectors.toList()));
            if (maxPrice.compareTo(maxPricePerSaleman) < 0) {
                maxPrice = maxPricePerSaleman;
                saleId = sale.getId();
            }
        }
        return saleId;
    }

    private void createErrorFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DIR_OUT + "/err.txt"))) {
            err.stream().forEach(error -> {
                try {
                    writer.write(error);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processFile(File file) {

        BufferedReader reader = null;
        System.out.println("Processing file: " + file.getName());

        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(REGISTER_SALESMAN)) {
                    salesmans.add(createSalemanBuilder(line, lineNumber));
                } else if (line.startsWith(REGISTER_CLIENT)) {
                    clients.add(createClientBuilder(line, lineNumber));
                } else if (line.startsWith(REGISTER_SALE)) {
                    sales.add(createSale(line, lineNumber));
                } else {
                    err.add("segmento invalido: " + line.substring(0,3));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SaleBuilder createSale(String line, int lineNumber) {
        String[] fields = line.split(REGISTER_SEPARATOR);
        List<ItemSaleBuilder> itensSale = createItemBuilder(line, lineNumber);

        SalesmanBuilder salesmanByName = findSalesmanByName(fields[3]);
        if (salesmanByName == null) {
            err.add("Não foi possivel localizar o vendedor: "+ fields[3]+ " Para a venda: "+ fields[1]+ " na linha: "+ lineNumber);
        } else {
            return new SaleBuilder.Builder().id(Long.valueOf(fields[1])).itemBuilder(itensSale).salesmanBuilder(salesmanByName).build();
        }
        return null;
    }

    private List<ItemSaleBuilder> createItemBuilder(String line, int lineNumber) {
        String itensLine = StringUtils.substringBetween(line, "[", "]");
        String[] itens = itensLine.split(",");
        List<ItemSaleBuilder> itensSale = new ArrayList<>();
        for (String item : itens) {
            if (itens.length >=3) {
                String[] itemField = item.split("-");
                ItemSaleBuilder builder = new ItemSaleBuilder.Builder().id(Long.valueOf(itemField[0])).item(itemField[1]).price(new BigDecimal(itemField[2])).builder();
                itensSale.add(builder);
            } else {
                err.add("Arquivo esta com informações faltantes do item de venda na linha: "+ lineNumber);
            }
        }
        return itensSale;
    }

    private ClientBuilder createClientBuilder(String line, int lineNumber) {
        String[] fields = line.split(REGISTER_SEPARATOR);
        if (fields.length >= 3) {
            return new ClientBuilder.Builder().cnpj(fields[1]).name(fields[2]).businessArea(fields[3]).build();
        } else {
            err.add("Arquivo esta com informações faltantes do cliente na linha: "+lineNumber);
        }
        return null;
    }

    private SalesmanBuilder createSalemanBuilder(String line, int lineNumber) {
        String[] fields = line.split(REGISTER_SEPARATOR);
        if (fields.length >= 3) {
            return new SalesmanBuilder.Builder().cpf(fields[1]).name(fields[2]).salary(fields[3]).build();
        } else {
            err.add("Arquivo esta com informações faltantes do vendedor na linha: "+ lineNumber);
        }

        return null;
    }

    private SalesmanBuilder findSalesmanByName(String salesman) {
        Optional<SalesmanBuilder> first = salesmans.stream().filter(salman -> salman.getName().equals(salesman)).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }

}
