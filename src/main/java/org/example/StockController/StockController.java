package org.example.StockController;

import org.example.StockService.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.io.*;
import java.util.Arrays;

import static org.example.Util.GetPaths.getPaths;


@RestController
public class StockController {
    @Value("${filePath}")
    private String filePath;
    @Autowired
    private StockService stockService;
    @Value("${Url}")
    private String url;
    @Value("${token}")
    String token;
    @Value("${market1}")
    String market1;
    @Value("${market2}")
    String market2;
    @Value("${fileName}")
    String fileName;

    @GetMapping("/importToCsv/{exchange}/{companySymbol}")
    public ResponseEntity<String> getdata(@PathVariable("exchange") String exchange, @PathVariable("companySymbol") String companySymbol) throws Exception {
        if(exchange.equals(market1) || exchange.equals(market2)){
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = url+companySymbol+"."+exchange+"?api token="+token;
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null, String.class);
            int status = response.getStatusCode().value();
            if(status>199 && status<300){
                String data = response.getBody();
                return  new ResponseEntity<String>(stockService.downloadCsv(filePath,exchange,companySymbol,data), HttpStatus.CREATED);
            }
            else{
               throw new Exception("Invalid Response from API "+status);
            }
        }
        else{
             throw new Exception("Invalid Path Variables");
        }
    }
    static volatile boolean process = true;

    @GetMapping("/importToCsv/{companySymbol}")
    public ResponseEntity<String> getAllData(@PathVariable("companySymbol") String companySymbol) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrlNSE = url+companySymbol+"."+market1+"?api token="+token;
        String apiUrlBSE = url+companySymbol+"."+market2+"?api token="+token;
        ResponseEntity<String> responseNSE = restTemplate.exchange(apiUrlNSE, HttpMethod.GET, null, String.class);
        ResponseEntity<String> responseBSE = restTemplate.exchange(apiUrlBSE, HttpMethod.GET, null, String.class);
        if((responseBSE.getStatusCode().value()>199 && responseBSE.getStatusCode().value()<300)&&(responseNSE.getStatusCode().value()>199 && responseNSE.getStatusCode().value()<300)){
            Thread t1 = new Thread(()->{
                try {
                    String NSE =  stockService.downloadCsv(filePath,market1,companySymbol,responseNSE.getBody());
                    if(!NSE.equals("You have requested for CompanyTicker: "+companySymbol+" and Exchange:"+market1)) process = false;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            Thread t2 = new Thread(()->{
                try {
                    String BSE =  stockService.downloadCsv(filePath,market2,companySymbol,responseBSE.getBody());
                    if(!BSE.equals("You have requested for CompanyTicker: "+companySymbol+" and Exchange:"+market2)) process = false;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            t1.start();
            t2.start();
            t1.join();
            t2.join();

            if(process){
                return new ResponseEntity<String>("Files created for CompanyTicker: "+companySymbol+" and Exchange:"+market1+"and "+market2,HttpStatus.CREATED);
            }
            else{
                return new ResponseEntity<String>("Failed creating files",HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>("API Validation Failed",HttpStatus.BAD_REQUEST);


    }

    @GetMapping("/importToDb/{exchange}/{companySymbol}")
    public ResponseEntity<String> importToDb(@PathVariable String exchange, @PathVariable String companySymbol) throws IOException {
        String file = filePath+"/" + exchange + "/" + companySymbol;
        String tableName = companySymbol.toLowerCase() + "_"+fileName.split("\\.")[0];
      //  System.out.println(file+" "+tableName);
        return new ResponseEntity<>(stockService.importToDb(file,tableName),HttpStatus.CREATED);
    }

    @GetMapping("/importToDb/{exchange}")
    public ResponseEntity<String> importTodbExchange(@PathVariable String exchange) throws IOException {
        String file = filePath+"/" + exchange;
        Object[] arr = getPaths(file);
      //  System.out.println(Arrays.toString(arr));
        for (Object tableName: arr) {
            String companySymbol = tableName.toString().split("/")[2];
            String fileNew = filePath+"/" + exchange + "/" + companySymbol;
            String table = companySymbol.toLowerCase() + "_"+fileName.split("\\.")[0];
          //  System.out.println(fileNew+" "+table);
            stockService.importToDb(fileNew,table);
        }
        return new ResponseEntity<String>("Data Loaded in DB",HttpStatus.CREATED);

    }

    @GetMapping("/importToDb/all")
    public ResponseEntity<String> importTodbAll() throws IOException {
        Object[] arr = getPaths(filePath);
        for(Object path: arr){
            String exchange = path.toString().split("/")[1];
            String companySymbol = path.toString().split("/")[2];
            String fileNew = filePath+"/" + exchange + "/" + companySymbol;
            String table = companySymbol.toLowerCase() + "_"+fileName.split("\\.")[0];
          //  System.out.println(fileNew+" "+table);
            stockService.importToDb(fileNew,table);
        }
        return new ResponseEntity<String>("Data Loaded in DB",HttpStatus.CREATED);
    }









}
