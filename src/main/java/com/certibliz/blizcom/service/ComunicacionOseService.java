package com.certibliz.blizcom.service;

import com.certibliz.blizcom.model.ResponseServer;
import org.springframework.core.io.InputStreamResource;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ComunicacionOseService {
    ResponseServer sendOse(String xml, String endpoint, String tagOperacionOK) throws IOException;

    InputStreamResource getimage() throws FileNotFoundException;

    ResponseServer sendGuiaOse(String xml, String endpoint, String tagOperacionOK) throws IOException;
}
