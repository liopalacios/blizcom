package com.certibliz.blizcom.service.impl;

import com.certibliz.blizcom.model.ResponseServer;
import com.certibliz.blizcom.service.ComunicacionOseService;
import org.apache.http.client.HttpClient;

import java.io.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
public class ComunicacionOseServiceImpl implements ComunicacionOseService {
    @Override
    public ResponseServer sendGuiaOse(String xml, String endpoint, String tagOperacionOK) throws IOException {
        System.out.println(endpoint);
        ResponseServer responseServer = new ResponseServer();
        CloseableHttpResponse responsePost;
        String formattedSOAPResponse;
        CloseableHttpClient client = null;
        StringEntity entity = null;
        HttpPost httpPost = null;
        String inputLine;
        int responseCode = 0;
        client = HttpClients.createDefault();
        httpPost = new HttpPost(endpoint);
        httpPost.setHeader("Prama", "no-cache");
        httpPost.setHeader("Cache-Control", "no-cache");
        entity = new StringEntity(xml, "UTF-8");
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-type", "text/xml");
        responsePost = client.execute(httpPost);
        responseCode = responsePost.getStatusLine().getStatusCode();
        responseServer.setServerCode(responseCode);
        StringBuilder response = new StringBuilder();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(responsePost.getEntity().getContent()));
        while ((inputLine = in.readLine()) != null) {
            String inp = inputLine.replace("S:","soap-env:");
            inp = inp.replace(":S=",":soap-env=");
            inp = inp.replace("SOAP-ENV:","soap-env:");
            inp = inp.replace("ns2:","br:");
            inp = inp.replace(":ns2",":br");
            response.append(inp);
        }
        System.out.println("response");
        System.out.println(response);
        client.close();
        formattedSOAPResponse = formatXML(response.toString());
        responseServer.setContent(formattedSOAPResponse);

        if (formattedSOAPResponse.contains("<" + tagOperacionOK + ">")) {
            responseServer.setSuccess(true);
        } else {
            responseServer.setSuccess(false);
        }

        return responseServer;
    }
    @Override
    public ResponseServer sendOse(String xml, String endpoint, String tagOperacionOK) throws IOException {
        //System.out.println(endpoint);
        //System.out.println(xml);
        ResponseServer responseServer = new ResponseServer();

        CloseableHttpResponse  responsePost;
        String formattedSOAPResponse;
        CloseableHttpClient client = null;
        StringEntity entity = null;
        HttpPost httpPost = null;
        String inputLine;
        int responseCode = 0;

        client = HttpClients.createDefault();
        httpPost = new HttpPost(endpoint);
        httpPost.setHeader("Prama", "no-cache");
        httpPost.setHeader("Cache-Control", "no-cache");

        entity = new StringEntity(xml, "UTF-8");
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-type", "text/xml");

        responsePost = client.execute(httpPost);
        responseCode = responsePost.getStatusLine().getStatusCode();
        responseServer.setServerCode(responseCode);
        System.out.println(responseCode);
        StringBuilder response = new StringBuilder();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(responsePost.getEntity().getContent()));
        while ((inputLine = in.readLine()) != null) {
            String inp = inputLine.replace("S:","soap-env:");
            inp = inp.replace(":S=",":soap-env=");
            inp = inp.replace("SOAP-ENV:","soap-env:");
            inp = inp.replace("ns2:","br:");
            inp = inp.replace(":ns2",":br");
            response.append(inp);
            //response.append(inputLine);
        }
        System.out.println("response");
        System.out.println(response);
        client.close();
        formattedSOAPResponse = formatXML(response.toString());
        System.out.println(formattedSOAPResponse);
        responseServer.setContent(formattedSOAPResponse);

        if (formattedSOAPResponse.contains("<" + tagOperacionOK + ">")) {
            responseServer.setSuccess(true);
        } else {
            responseServer.setSuccess(false);
        }

        return responseServer;
    }

    @Override
    public InputStreamResource getimage() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:dev.jpg");
        file.getAbsolutePath();
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return resource;
    }



    public static String formatXML(String xml) {
        String result = "";
        try {
            Document doc = DocumentHelper.parseText(xml);
            StringWriter sw = new StringWriter();
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter xw = new XMLWriter(sw, format);
            xw.write(doc);
            result = sw.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String formatoXML = result.replace("\n\n", "\n").replace("&lt;", "<")
                .replace("&gt;", ">");
        return formatoXML;
    }
}
