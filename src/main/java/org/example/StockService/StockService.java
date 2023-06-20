package org.example.StockService;

import java.io.IOException;

public interface StockService {
     public  String  downloadCsv(String filePath, String exchange, String companySymbol, String data) throws IOException;

     String importToDb(String file, String tableName) throws IOException;
}
