package com.crane.wordformat.restful.service;

import io.minio.errors.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface CoverFormService {
    List<String> wordConvertPNG(String CoverTemplateUrl) throws Exception;

}
