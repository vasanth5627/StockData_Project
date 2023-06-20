package org.example.StockService;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.example.Entity.StockData;
import org.example.StockRepository.StockRepository;
import org.example.Util.GetPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.example.Util.CreateFolders.createFolders;
import static org.example.Util.GenerateId.generateId;
import static org.example.Util.GetPaths.getPaths;

@Service
public class StockServiceImpl implements StockService {
    @Value("${fileName}")
    String fileName;
    @Value("${Url}")
    String url;
    @Value("${token}")
    String token;
    @Autowired
    StockRepository stockRepository;
    @Autowired
    private  EntityManager entityManager;
    @Value("${ColumnValues}")
    String columnVales;
    @Override
    public String downloadCsv(String filePath,  String exchange, String companySymbol,String data) throws IOException {
        String path =  filePath+"/"+exchange+"/"+companySymbol+"/"+ LocalDate.now().getYear()+"/"+LocalDate.now().getMonth();
        boolean b = createFolders(path);
        if(b){
            FileWriter fileWriter = new FileWriter(path+"/"+fileName);
            //System.out.println(filePath+fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Id,ImportTS," + data.split("\n")[0]);
            bufferedWriter.newLine();
            String[] lines = data.split("\n");
            LocalDateTime importTs = LocalDateTime.now();
            for (int i = 1; i < lines.length; i++) {
                String[] fields = lines[i].split(",");
                String id = generateId(fields[0], exchange, companySymbol);
                String line = id + "," + importTs + "," + lines[i];
                bufferedWriter.write(line);
                bufferedWriter.newLine();

            }

            bufferedWriter.close();
            fileWriter.close();
            return "You have requested for CompanyTicker: "+companySymbol+" and Exchange: "+exchange;
        }
        else{
            return "Folders not created";
        }

    }

    @Override
    @Transactional
    public String importToDb(String file, String tableName) throws IOException {
        Object[] paths =  getPaths(file);
       // System.out.println(Arrays.toString(paths));
        for (Object path:
             paths) {
        //    System.out.println(path);
            createTableIfNotExists(tableName);
            try (BufferedReader br = new BufferedReader(new FileReader(path+""))) {
                String line;
                br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] fields = line.split(",");
                    String sql = "Insert into "+tableName+" "+columnVales;
                    entityManager.createNativeQuery(sql)
                            .setParameter(1,new BigDecimal(fields[7]))
                            .setParameter(2,new BigDecimal(fields[6]))
                            .setParameter(3,LocalDate.parse(fields[2], DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .setParameter(4,new BigDecimal(fields[4]))
                            .setParameter(5,LocalDateTime.now())
                            .setParameter(6,new BigDecimal(fields[5]))
                            .setParameter(7,new BigDecimal(fields[3]))
                            .setParameter(8,Long.parseLong(fields[8]))
                            .executeUpdate();
                }
                return "Data loaded successfully to table: " + tableName;
            } catch (IOException e) {
                return "Data Loading Failed";
            }

        }

        return "Not successful";
    }

    @Transactional
    private void createTableIfNotExists(String tableName) {
        String sql = "CREATE TABLE IF NOT EXISTS "+tableName+" like stock_data";
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}
